package kg.leaf.notepad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 20000) String content
) {}
