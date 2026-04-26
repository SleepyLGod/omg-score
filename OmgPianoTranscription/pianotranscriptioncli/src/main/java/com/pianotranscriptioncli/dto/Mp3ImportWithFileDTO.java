package com.pianotranscriptioncli.dto;

import lombok.Data;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Mp3ImportWithFileDTO {
    @NonNull
    private MultipartFile file;
    @NonNull
    private String songName;

    public Mp3ImportWithFileDTO(MultipartFile file, String songName) {
        this.file = file;
        this.songName = songName;
    }

}
