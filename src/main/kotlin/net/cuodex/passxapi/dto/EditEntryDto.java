package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class EditEntryDto {
    @NotNull
    String sessionId;
    @NotNull
    private String entryService, entryUrl, entryDescription, entryUsername, entryEmail, entryPassword;

}
