package kg.leaf.notepad.service;

import kg.leaf.notepad.dto.LoginRequest;
import kg.leaf.notepad.dto.RegisterRequest;
import kg.leaf.notepad.dto.VerifyRequest;
import kg.leaf.notepad.entity.User;
import kg.leaf.notepad.entity.VerificationCode;
import kg.leaf.notepad.repository.UserRepository;
import kg.leaf.notepad.repository.VerificationCodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserRepository userRepository,
                       VerificationCodeRepository codeRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Этот Email уже зарегистрирован. Попробуйте войти.");
        }

        User user = new User(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password())
        );
        userRepository.save(user);

        sendNewCode(user);
    }

    @Transactional
    public void verify(VerifyRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if (user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email уже подтверждён. Войдите в аккаунт.");
        }

        VerificationCode savedCode = codeRepository.findTopByUserOrderByIdDesc(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Запросите новый код"));

        if (savedCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Срок действия кода истёк. Запросите новый код.");
        }

        if (!savedCode.getCode().equals(request.code())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный код подтверждения");
        }

        user.setVerified(true);
        userRepository.save(user);
        codeRepository.deleteByUser(user);
    }

    @Transactional
    public void resendCode(String emailAddress) {
        String email = emailAddress.trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if (user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email уже подтверждён");
        }

        sendNewCode(user);
    }

    public User login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный Email или пароль"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный Email или пароль");
        }

        if (!user.isVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Сначала подтвердите Email");
        }

        return user;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));
    }

    private void sendNewCode(User user) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        codeRepository.deleteByUser(user);
        codeRepository.save(new VerificationCode(code, LocalDateTime.now().plusMinutes(10), user));
        emailService.sendVerificationCode(user.getEmail(), code);
    }
}
