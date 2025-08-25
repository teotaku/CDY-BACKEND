package com.cdy.cdy.service;

import com.cdy.cdy.dto.response.CustomUserDetails;
import com.cdy.cdy.entity.User;
import com.cdy.cdy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // findByUserEmail 이 Optional<User> 반환하니까 orElseThrow로 꺼내야 함
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // DB에서 가져온 User → Spring Security에서 쓰는 UserDetails 로 감싸서 리턴
        return new CustomUserDetails(user);
    }
}
