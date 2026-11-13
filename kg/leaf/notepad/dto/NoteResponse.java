package kg.leaf.notepad.dto;

import kg.leaf.notepad.entity.Note;
import java.time.LocalDateTime;

public record NoteResponse(Long id, String title, String content,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getTitle(), note.getContent(),
                note.getCreatedAt(), note.getUpdatedAt());
    }
}
