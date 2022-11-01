package com.pianotranscriptioncli.service.impl;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.dto.Mp3ImportWithFileDTO;
import com.pianotranscriptioncli.service.TranscriptionService;
import com.pianotranscriptioncli.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class TranscriptionServiceImpl implements TranscriptionService {

    @Override
    public CommonResult Mp3TOMidiUpload(Mp3ImportDTO mp3ImportDTO) throws Exception {
        String resourcePath;
        if (mp3ImportDTO.isAbsolute()) {
            resourcePath = mp3ImportDTO.getResourcePath();
        } else {
            System.out.println(System.getProperty("user.dir"));
            resourcePath = System.getProperty("user.dir") + mp3ImportDTO.getResourcePath(); // "\\src\\main\\resources\\"
        }
        //String ans = resourcePath + "output\\" + mp3ImportDTO.getSongName() + ".mid";
        String ans = Utils.Convertor(resourcePath, mp3ImportDTO.getSongName());
        if (ans != null) {
            return CommonResult.success(ans, "mp3转换成功");
        } else {
            return CommonResult.failed("mp3转换失败");
        }
    }

    @Override
    public CommonResult Mp3TOMidiUploadWithFile(Mp3ImportWithFileDTO mp3ImportWithFileDTO) throws Exception {
        MultipartFile file = mp3ImportWithFileDTO.getFile();
        String inputFilePath;
        if (!file.isEmpty()) {
/*
            String fileName = file.getOriginalFilename(); // 获取文件名
            assert fileName != null;
            String suffixName = fileName.substring(fileName.lastIndexOf(".")); // 获取文件的后缀名
*/
            inputFilePath = mp3ImportWithFileDTO.getInputPath() + "input\\" + mp3ImportWithFileDTO.getSongName() + ".mp3";
            File dest = new File(inputFilePath);
            if (!dest.getParentFile().exists()) { // 检测是否存在目录
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
                System.out.println("mp3文件存入成功!");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return CommonResult.failed("mp3文件存入失败!" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("mp3文件上传失败!" + e);
                return CommonResult.failed("mp3文件存入失败!" + e.getMessage());
            }
        } else {
            System.out.println("无mp3文件！");
            return CommonResult.failed("失败！无mp3文件！");
        }

        String outPath = mp3ImportWithFileDTO.getOutPath() + mp3ImportWithFileDTO.getSongName() + ".mid";
        String output = Utils.ConvertorRedirect(mp3ImportWithFileDTO.getInputPath(), mp3ImportWithFileDTO.getSongName(), outPath);
        if (output != null) {
            return CommonResult.success(output, "mp3转换成功");
        } else {
            return CommonResult.failed("mp3转换失败");
        }
    }

    @Override
    public String WavToMidiUpload() {
        return "null";
    }
}


