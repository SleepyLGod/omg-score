package com.pianotranscriptioncli.service.impl;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.service.TranscriptionService;
import com.pianotranscriptioncli.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class TranscriptionServiceImpl implements TranscriptionService {
    @Value("${omg.transcription.work-dir}")
    private String workDir;

    @Value("${omg.transcription.model-path}")
    private String modelPath;

    @Override
    public CommonResult Mp3TOMidiUpload(Mp3ImportDTO mp3ImportDTO) throws Exception {
        Path resourcePath;
        if (mp3ImportDTO.isAbsolute()) {
            resourcePath = normalizePath(mp3ImportDTO.getResourcePath());
        } else {
            System.out.println(System.getProperty("user.dir"));
            resourcePath = Path.of(System.getProperty("user.dir")).resolve(normalizeRelativePath(mp3ImportDTO.getResourcePath()));
        }
        Path outPath = normalizePath(mp3ImportDTO.getOutPath()).resolve(mp3ImportDTO.getSongName() + ".mid");
        String ans = Utils.ConvertorRedirect(resourcePath.toString(), mp3ImportDTO.getSongName(), outPath.toString());
        if (ans != null) {
            return CommonResult.success(ans, "mp3转换成功");
        } else {
            return CommonResult.failed("mp3转换失败");
        }
    }

    /**
     * 上传文件
     * @param file 上传的mp3文件
     * @param songName 歌曲名
     * @return Path
     * @throws Exception
     */
    @Override
    public Path Mp3TOMidiUploadWithFile(MultipartFile file, String songName) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("mp3文件不能为空");
        }

        Path model = Path.of(modelPath);
        if (!Files.exists(model)) {
            throw new FileNotFoundException("ONNX model not found: " + model);
        }

        String safeSongName = sanitizeSongName(songName);
        Path root = Path.of(workDir);
        Path inputDir = root.resolve("input");
        Path outputDir = root.resolve("output");
        Path tmpDir = root.resolve("tmp");
        Files.createDirectories(inputDir);
        Files.createDirectories(outputDir);

        Path inputFile = inputDir.resolve(safeSongName + ".mp3");
        Path outputFile = outputDir.resolve(safeSongName + ".mid");
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, inputFile, StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("mp3文件存入成功!");

        String output = Utils.convertMp3ToMidi(inputFile, outputFile, model, tmpDir);
        if (output == null) {
            throw new IOException("mp3转换失败");
        }
        return Path.of(output);
    }

    @Override
    public String WavToMidiUpload() {
        return "null";
    }

    private Path normalizePath(String path) {
        return Path.of(path.replace("\\", "/"));
    }

    private Path normalizeRelativePath(String path) {
        String normalized = path.replace("\\", "/");
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return Path.of(normalized);
    }

    private String sanitizeSongName(String songName) {
        String safeSongName = songName == null ? "" : songName.strip();
        safeSongName = safeSongName.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        if (safeSongName.isEmpty()) {
            return "upload";
        }
        return safeSongName;
    }
}

