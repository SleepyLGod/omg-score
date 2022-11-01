package com.pianotranscriptioncli.controller;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.service.TranscriptionService;
import com.pianotranscriptioncli.service.impl.TranscriptionServiceImpl;
import com.pianotranscriptioncli.vo.Mp3ImportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/transcription")
public class TranscriptionController {

    @Autowired
    TranscriptionService transcriptionService;

    @PostMapping(value = "/fuck")
    public String Mp3TOMidiUpload() throws Exception {
        return "hello";
    }

    @PostMapping(value = "/mp3ToMidi", consumes = {"application/json"})
    @ResponseBody
    public Mp3ImportVO Mp3ToMidi(@RequestBody Mp3ImportDTO mp3ImportDTO) throws Exception {
        try {
            CommonResult commonResult = transcriptionService.Mp3TOMidiUpload(mp3ImportDTO);
            if (commonResult.getCode() == 1) {
                return new Mp3ImportVO(true, commonResult.getData().toString(), null);
            } else {
                return new Mp3ImportVO(false, null, commonResult.getMessage());
            }
        } catch (NullPointerException e) {
            return new Mp3ImportVO(false, null, "请检查是否传入了正确的参数");
        }
    }


    @ResponseBody
    @RequestMapping(value = "/mp3ToMidiWithFile", produces = {"text/html;charset=UTF-8;"})
    public void uploadLog(@RequestParam("Mp3FileName") MultipartFile file, HttpServletRequest request) {
        if (!file.isEmpty()) {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            assert fileName != null;
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            // 文件上传后的路径
            String filePath = "";
            File dest = new File(filePath + fileName);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
                System.out.println("日志文件上传成功!");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("日志文件上传失败!" + e);
            }
        } else {
            System.out.println("日志文件上传失败！");
        }
    }
}