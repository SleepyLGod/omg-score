package com.pianotranscriptioncli.service;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;

public interface TranscriptionService {
    CommonResult Mp3TOMidiUpload(Mp3ImportDTO mp3ImportDTO) throws Exception;

    String WavToMidiUpload();
}
