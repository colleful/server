package com.colleful.server.user.repository;

import com.colleful.server.global.exception.NotFoundResourceException;
import com.colleful.server.user.domain.EmailVerification;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class EmailVerificationRepositoryImpl implements EmailVerificationRepository {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(EmailVerification emailVerification) {
        stringRedisTemplate.opsForValue()
            .set(emailVerification.getEmail(), emailVerification.toString());
    }

    @Override
    public EmailVerification findByEmail(String email) {
        Optional<String> serialized = Optional
            .ofNullable(stringRedisTemplate.opsForValue().get(email));
        return new EmailVerification(
            serialized.orElseThrow(() -> new NotFoundResourceException("먼저 이메일을 인증해 주세요.")));
    }

    @Override
    public void deleteByEmail(String email) {
        stringRedisTemplate.delete(email);
    }
}
