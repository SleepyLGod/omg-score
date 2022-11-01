package com.pianotranscriptioncli.service;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.dto.Mp3ImportWithFileDTO;

public interface TranscriptionService {
    CommonResult Mp3TOMidiUpload(Mp3ImportDTO mp3ImportDTO) throws Exception;

    CommonResult Mp3TOMidiUploadWithFile(Mp3ImportWithFileDTO mp3ImportWithFileDTO) throws Exception;

    String WavToMidiUpload();
}
