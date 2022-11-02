package com.pianotranscriptioncli.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Mp3ImportWithFileDTO {
    @NonNull
    private MultipartFile file;
    @NonNull
    private String songName;
    @NonNull
    private String inputPath = "D:\\gitrepositories\\omg-score\\OmgPianoTranscription\\pianotranscriptioncli\\src\\main\\resources\\";
    @NonNull
    private String outPath = "D:\\gitrepositories\\omg-score\\OmgPianoTranscription\\pianotranscriptioncli\\src\\main\\resources\\output\\";
    public Mp3ImportWithFileDTO(MultipartFile file, String songName) {
        this.file = file;
        this.songName = songName;
    }

}

