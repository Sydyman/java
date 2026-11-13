package kg.leaf.notepad.repository;

import kg.leaf.notepad.entity.User;
import kg.leaf.notepad.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTopByUserOrderByIdDesc(User user);
    void deleteByUser(User user);
}
