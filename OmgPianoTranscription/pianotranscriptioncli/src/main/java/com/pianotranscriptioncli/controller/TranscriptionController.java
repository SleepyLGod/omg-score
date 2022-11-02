package com.pianotranscriptioncli.controller;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.dto.Mp3ImportWithFileDTO;
import com.pianotranscriptioncli.service.TranscriptionService;
import com.pianotranscriptioncli.service.impl.TranscriptionServiceImpl;
import com.pianotranscriptioncli.vo.Mp3ImportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
// @CrossOrigin(origins = "*", maxAge = 360000) // 不能和CorsConfig同时使用
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
    @PostMapping(value = "/mp3ToMidiWithFile", consumes = {"multipart/form-data"})
    public void Mp3ToMidiWithFile(@RequestParam("file")MultipartFile file,
                                  // @RequestParam("outPath")String outPath,
                                  @RequestParam("songName")String songName,
                                  HttpServletResponse response) throws Exception {
        Mp3ImportWithFileDTO mp3ImportWithFileDTO = new Mp3ImportWithFileDTO(file, songName);
        try {
            CommonResult commonResult = transcriptionService.Mp3TOMidiUploadWithFile(mp3ImportWithFileDTO, response);
            if (commonResult.getCode() == 1) {
                System.out.println("success");
            } else {
                System.out.println("fail");
            }
        } catch (NullPointerException e) {
            System.out.println("fail");
            e.printStackTrace();
        }
    }

}