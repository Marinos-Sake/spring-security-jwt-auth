package io.github.marinossake.repository;

import io.github.marinossake.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPublicId(String publicId);

    boolean existsByUsername(String username);
}
