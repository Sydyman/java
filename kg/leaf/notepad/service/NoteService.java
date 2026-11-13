package kg.leaf.notepad.service;

import kg.leaf.notepad.dto.NoteRequest;
import kg.leaf.notepad.dto.NoteResponse;
import kg.leaf.notepad.entity.Note;
import kg.leaf.notepad.entity.User;
import kg.leaf.notepad.repository.NoteRepository;
import kg.leaf.notepad.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<NoteResponse> getAll(Long userId) {
        return noteRepository.findAllByUserIdOrderByUpdatedAtDesc(userId)
                .stream().map(NoteResponse::from).toList();
    }

    public NoteResponse getOne(Long id, Long userId) {
        return NoteResponse.from(findNote(id, userId));
    }

    @Transactional
    public NoteResponse create(NoteRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        Note note = new Note(request.title().trim(), request.content().trim(), user);
        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional
    public NoteResponse update(Long id, NoteRequest request, Long userId) {
        Note note = findNote(id, userId);
        note.setTitle(request.title().trim());
        note.setContent(request.content().trim());
        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Note note = findNote(id, userId);
        noteRepository.delete(note);
    }

    private Note findNote(Long id, Long userId) {
        return noteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заметка не найдена"));
    }
}
