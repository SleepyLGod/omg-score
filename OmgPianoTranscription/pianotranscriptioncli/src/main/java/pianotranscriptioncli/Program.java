package pianotranscriptioncli;

import libpianotranscription.Transcriptor;

import java.io.*;
import java.nio.file.Files;

public class Program {
    public static void main(String[] args) throws Exception {
        
        System.out.println(System.getProperty("user.dir"));
        String resourcePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
        String songName = "雨的印记";
        String inputFilePath = resourcePath + "input\\" + songName + ".mp3";
        String outPutFilePath = resourcePath + "output\\" + songName + ".mid";

        preProcessFile(inputFilePath);
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
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        System.out.println("OK");
        System.exit(0);
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
