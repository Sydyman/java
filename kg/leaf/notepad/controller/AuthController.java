package kg.leaf.notepad.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kg.leaf.notepad.dto.LoginRequest;
import kg.leaf.notepad.dto.RegisterRequest;
import kg.leaf.notepad.dto.VerifyRequest;
import kg.leaf.notepad.entity.User;
import kg.leaf.notepad.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Map.of("message", "Код подтверждения отправлен на Email");
    }

    @PostMapping("/verify")
    public Map<String, String> verify(@Valid @RequestBody VerifyRequest request) {
        authService.verify(request);
        return Map.of("message", "Email успешно подтверждён. Теперь войдите в аккаунт.");
    }

    @PostMapping("/resend-code")
    public Map<String, String> resendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите Email");
        }
        authService.resendCode(email);
        return Map.of("message", "Новый код отправлен");
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = authService.login(request);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        return Map.of("message", "Вход выполнен", "name", user.getName(), "email", user.getEmail());
    }

    @GetMapping("/me")
    public Map<String, String> me(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Сначала войдите в аккаунт");
        }
        User user = authService.getUser(userId);
        return Map.of("name", user.getName(), "email", user.getEmail());
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "Вы вышли из аккаунта");
    }
}
