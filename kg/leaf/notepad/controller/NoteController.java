package kg.leaf.notepad.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kg.leaf.notepad.dto.NoteRequest;
import kg.leaf.notepad.dto.NoteResponse;
import kg.leaf.notepad.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteResponse> getAll(HttpSession session) {
        return noteService.getAll(getUserId(session));
    }

    @GetMapping("/{id}")
    public NoteResponse getOne(@PathVariable Long id, HttpSession session) {
        return noteService.getOne(id, getUserId(session));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@Valid @RequestBody NoteRequest request, HttpSession session) {
        return noteService.create(request, getUserId(session));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable Long id,
                               @Valid @RequestBody NoteRequest request,
                               HttpSession session) {
        return noteService.update(id, request, getUserId(session));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpSession session) {
        noteService.delete(id, getUserId(session));
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Сначала войдите в аккаунт");
        }
        return userId;
    }
}
