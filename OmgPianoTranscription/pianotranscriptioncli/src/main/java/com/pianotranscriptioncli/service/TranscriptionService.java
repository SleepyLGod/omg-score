package com.pianotranscriptioncli.service;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.dto.Mp3ImportWithFileDTO;

import javax.servlet.http.HttpServletResponse;

public interface TranscriptionService {
    CommonResult Mp3TOMidiUpload(Mp3ImportDTO mp3ImportDTO) throws Exception;

    CommonResult Mp3TOMidiUploadWithFile(Mp3ImportWithFileDTO mp3ImportWithFileDTO, HttpServletResponse response) throws Exception;

    String WavToMidiUpload();
}
