package com.pianotranscriptioncli.service.impl;

import com.pianotranscriptioncli.common.api.CommonResult;
import com.pianotranscriptioncli.dto.Mp3ImportDTO;
import com.pianotranscriptioncli.service.TranscriptionService;
import com.pianotranscriptioncli.utils.Utils;
import org.springframework.stereotype.Service;

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
    public String WavToMidiUpload() {
        return null;
    }
}


