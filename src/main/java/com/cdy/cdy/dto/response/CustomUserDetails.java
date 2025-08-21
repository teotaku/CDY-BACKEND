package com.cdy.cdy.dto.response;

import com.cdy.cdy.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;                       // 👈 DB User PK
    private final String email;                  // 👈 username 대용
    private final String password;               // 👈 해시 비번
    private final Collection<? extends GrantedAuthority> authorities;

    // User 엔티티 -> CustomUserDetails 변환자
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // UserDetails 기본 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // 👈 이미 생성자에서 넣어둔 걸 반환
    }

    @Override
    public String getPassword() {
        return password; // 👈 따로 user 호출 안 하고 필드 반환
    }

    @Override
    public String getUsername() {
        return email; // 👈 username 대체
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
