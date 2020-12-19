package com.colleful.server.domain.user.domain;

import com.colleful.server.domain.department.domain.Department;
import com.colleful.server.domain.user.dto.UserDto.Request;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Department department;

    @Column(nullable = false)
    private String selfIntroduction;

    @Column
    private Long teamId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    public void changeInfo(Request info) {
        this.nickname = info.getNickname() != null ? info.getNickname() : this.nickname;
        this.selfIntroduction =
            info.getSelfIntroduction() != null ?
                info.getSelfIntroduction() :
                this.selfIntroduction;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void joinTeam(Long teamId) {
        this.teamId = teamId;
    }

    public void leaveTeam() {
        this.teamId = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
