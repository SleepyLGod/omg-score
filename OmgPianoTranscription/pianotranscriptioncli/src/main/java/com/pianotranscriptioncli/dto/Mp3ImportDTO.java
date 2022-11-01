package com.pianotranscriptioncli.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class Mp3ImportDTO {
    @NonNull
    private boolean isAbsolute;
    @NonNull
    private String resourcePath;
    @NonNull
    private String songName;
}
