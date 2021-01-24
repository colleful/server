package com.colleful.server.user.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer code;

    @Column(nullable = false)
    private Boolean isChecked;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public EmailVerification(String email, Integer code) {
        this.email = email;
        this.code = code;
        this.isChecked = false;
    }

    public void changeCode(Integer code) {
        this.code = code;
        this.isChecked = false;
    }

    public boolean verify(Integer code) {
        return this.code.equals(code);
    }

    public void check() {
        this.isChecked = true;
    }
}
