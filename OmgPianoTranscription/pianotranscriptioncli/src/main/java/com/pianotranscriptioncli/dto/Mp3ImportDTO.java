package com.pianotranscriptioncli.dto;

import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Data
public class Mp3ImportDTO {
    @NonNull
    private boolean isAbsolute;
    @NonNull
    private String resourcePath;
    @NonNull
    private String songName;
}
