package com.colleful.server.domain.user.repository;

import com.colleful.server.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAll(Pageable pageable);
    Optional<User> findByEmail(String email);
    List<User> findByNicknameContaining(String nickname);
    List<User> findAllByTeamId(Long teamId);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
