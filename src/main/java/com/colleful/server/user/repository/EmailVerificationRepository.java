package com.colleful.server.user.repository;

import com.colleful.server.user.domain.EmailVerification;

public interface EmailVerificationRepository {

    void save(EmailVerification emailVerification);

    EmailVerification findByEmail(String email);

    void deleteByEmail(String email);
}
