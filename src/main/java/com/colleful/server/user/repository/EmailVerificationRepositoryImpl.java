package com.colleful.server.user.repository;

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
    public Optional<EmailVerification> findByEmail(String email) {
        Optional<String> serialized = Optional
            .ofNullable(stringRedisTemplate.opsForValue().get(email));
        return serialized.map(EmailVerification::new);
    }

    @Override
    public void deleteByEmail(String email) {
        stringRedisTemplate.delete(email);
    }
}
