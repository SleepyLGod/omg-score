package com.pianotranscriptioncli.controller;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.service.TranscriptionService;
import com.pianotranscriptioncli.vo.Mp3ImportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
// @CrossOrigin(origins = "*", maxAge = 360000) // 不能和CorsConfig同时使用
@RequestMapping("/transcription")
public class TranscriptionController {

    @Autowired
    TranscriptionService transcriptionService;

    @GetMapping(value = "/health")
    public String health() {
        return "ok";
    }

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
    public ResponseEntity<Resource> Mp3ToMidiWithFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("songName") String songName) throws Exception {
        Path midiPath = transcriptionService.Mp3TOMidiUploadWithFile(file, songName);
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(midiPath));
        String downloadName = songName + ".mid";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/midi"))
                .contentLength(Files.size(midiPath))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(downloadName, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(resource);
    }

}
