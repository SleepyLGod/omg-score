package com.pianotranscriptioncli.vo;

import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

@Data
public class Mp3ImportVO {
    @NonNull
    boolean isOk;
    @Nullable
    String MidiPath;
    @Nullable
    String message;

    public Mp3ImportVO(boolean isOk, String MidiPath, String message) {
        this.isOk = isOk;
        this.MidiPath = MidiPath;
        this.message = message;
    }
}
