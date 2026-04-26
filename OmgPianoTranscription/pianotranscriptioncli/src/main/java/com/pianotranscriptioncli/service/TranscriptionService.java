package com.pianotranscriptioncli.service;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface TranscriptionService {
    CommonResult Mp3TOMidiUpload(Mp3ImportDTO mp3ImportDTO) throws Exception;

    Path Mp3TOMidiUploadWithFile(MultipartFile file, String songName) throws Exception;

    String WavToMidiUpload();
}
