package kg.leaf.notepad.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public VerificationCode() {
    }

    public VerificationCode(String code, LocalDateTime expiresAt, User user) {
        this.code = code;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public User getUser() { return user; }
}
