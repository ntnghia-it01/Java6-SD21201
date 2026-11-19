package com.fpoly.java6.jpas;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fpoly.java6.entities.UserEntity;

public interface UserJPA extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
