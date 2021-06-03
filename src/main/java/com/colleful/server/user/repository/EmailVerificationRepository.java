package com.colleful.server.user.repository;

import com.colleful.server.user.domain.EmailVerification;
import java.util.Optional;

public interface EmailVerificationRepository {

    void save(EmailVerification emailVerification);

    Optional<EmailVerification> findByEmail(String email);

    void deleteByEmail(String email);
}
