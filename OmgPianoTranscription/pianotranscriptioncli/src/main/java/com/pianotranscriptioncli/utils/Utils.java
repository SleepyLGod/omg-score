package com.pianotranscriptioncli.utils;

import ai.onnxruntime.OrtException;
import libpianotranscription.Transcriptor;

import javax.sound.midi.InvalidMidiDataException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    public static short[] toShortLE(byte[] bytes) {
        short[] output = new short[bytes.length / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            var x = ((bytes[i + 1]) & 0xff) << 8;
            var y = bytes[i] & 0xff;
            output[i / 2] = (short) (x | y);
        }
        return output;
    }

    public static float[] normalizeShort(short[] shorts) {
        var output = new float[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            output[i] = (float) shorts[i] / 32767;
        }
        return output;
    }

    public static String Convertor(String resourcePath, String songName) throws Exception {
        Path root = Path.of(resourcePath);
        Path inputFilePath = root.resolve("input").resolve(songName + ".mp3");
        Path outPutFilePath = root.resolve("output").resolve(songName + ".mid");
        return convertMp3ToMidi(inputFilePath, outPutFilePath, root.resolve("transcription.onnx"), root);
    }

    private static String getString(Path modelPath, Path outPutFilePath, Path pcmPath) throws IOException, OrtException, InvalidMidiDataException {
        byte[] a = Files.readAllBytes(pcmPath);
        var b = Utils.normalizeShort(Utils.toShortLE(a));

        var transcriptor = new Transcriptor(modelPath.toString());
        var out = transcriptor.transcript(b);
        if (outPutFilePath.getParent() != null) {
            Files.createDirectories(outPutFilePath.getParent());
        }
        try(var file = new FileOutputStream(outPutFilePath.toFile())) {
            file.write(out);
            System.out.println("OK");
//            System.exit(0);
            return outPutFilePath.toString();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static String ConvertorRedirect(String resourcePath, String songName, String outputPath) throws Exception {
        Path root = Path.of(resourcePath);
        Path inputFilePath = root.resolve("input").resolve(songName + ".mp3");
        return convertMp3ToMidi(inputFilePath, Path.of(outputPath), root.resolve("transcription.onnx"), root);
    }

    public static String convertMp3ToMidi(Path inputFile, Path outputFile, Path modelPath, Path workDir) throws Exception {
        Files.createDirectories(workDir);
        Path pcmPath = Files.createTempFile(workDir, "omg-transcription-", ".pcm");
        try {
            preProcessFile(inputFile, pcmPath);
            return getString(modelPath, outputFile, pcmPath);
        } finally {
            Files.deleteIfExists(pcmPath);
        }
    }

    private static void preProcessFile(Path fileName, Path pcmPath) throws Exception {
        String[] cmd = {"ffmpeg", "-i", fileName.toString(), "-ac", "1", "-ar", "16000", "-f", "s16le", pcmPath.toString(), "-y"};
        var process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        var is = process.getInputStream();
        var isr = new InputStreamReader(is);
        var br = new BufferedReader(isr);
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
        int exitCode = process.waitFor();
        if (exitCode != 0 || !Files.exists(pcmPath)) {
            throw new Exception("ffmpeg execute failed, check if the input file does not exist");
        }
    }
}
