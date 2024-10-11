package hwannee.project.User.repository;

import hwannee.project.User.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(String id);
    Optional<User> findByTel(String tel);
    Optional<User> findByIdx(Integer idx);
}
