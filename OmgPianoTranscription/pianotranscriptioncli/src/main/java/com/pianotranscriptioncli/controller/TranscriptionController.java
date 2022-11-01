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
    @PostMapping(value = "/mp3ToMidiWithFile", consumes = {"multipart/form-data"})
    public Mp3ImportVO Mp3ToMidiWithFile(@RequestParam("file")MultipartFile file,
                                         @RequestParam("outPath")String outPath,
                                         @RequestParam("songName")String songName) throws Exception {
        Mp3ImportWithFileDTO mp3ImportWithFileDTO = new Mp3ImportWithFileDTO(file, outPath, songName);
        try {
            CommonResult commonResult = transcriptionService.Mp3TOMidiUploadWithFile(mp3ImportWithFileDTO);
            if (commonResult.getCode() == 1) {
                return new Mp3ImportVO(true, commonResult.getData().toString(), null);
            } else {
                return new Mp3ImportVO(false, null, commonResult.getMessage());
            }
        } catch (NullPointerException e) {
            return new Mp3ImportVO(false, null, "请检查是否传入了正确的参数");
        }
    }

}