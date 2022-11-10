package com.pianotranscriptioncli.utils;

import ai.onnxruntime.OrtException;
import libpianotranscription.Transcriptor;

import javax.sound.midi.InvalidMidiDataException;
import java.io.*;
import java.nio.file.Files;

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

        String inputFilePath = resourcePath + "input\\" + songName + ".mp3";
        String outPutFilePath = resourcePath + "output\\" + songName + ".mid";

        preProcessFile(inputFilePath);
        return getString(resourcePath, outPutFilePath);
    }

    private static String getString(String resourcePath, String outPutFilePath) throws IOException, OrtException, InvalidMidiDataException {
        byte[] a = Files.readAllBytes(new File("test.pcm").toPath());
        var b = Utils.normalizeShort(Utils.toShortLE(a));
        try {
            var ans = new File("test.pcm").delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        var transcriptor = new Transcriptor(resourcePath + "transcription.onnx");
        var out = transcriptor.transcript(b);
        try(var file = new FileOutputStream(outPutFilePath)) {
            file.write(out);
            System.out.println("OK");
//            System.exit(0);
            return outPutFilePath;
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static String ConvertorRedirect(String resourcePath, String songName, String outputPath) throws Exception {
        preProcessFile(resourcePath + "input\\" + songName + ".mp3");
        return getString(resourcePath, outputPath);
    }

    private static void preProcessFile(String fileName) throws Exception {
        String[] cmd = {"ffmpeg", "-i", fileName, "-ac", "1", "-ar", "16000", "-f", "s16le", "test.pcm", "-y"};
        var process = Runtime.getRuntime().exec(cmd); // 调用命令行
        var is = process.getErrorStream();
        var isr = new InputStreamReader(is);
        var br = new BufferedReader(isr);
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
            line = br.readLine();
        }
        process.waitFor();
        var file = new File("test.pcm");
        if (!file.exists()) throw new Exception("ffmpeg execute failed, check if the input file does not exist");
    }
}
