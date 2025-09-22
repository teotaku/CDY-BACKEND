package com.cdy.cdy.config;

import com.cdy.cdy.entity.User;
import com.cdy.cdy.entity.UserCategory;
import com.cdy.cdy.entity.UserRole;
import com.cdy.cdy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUsers() {
        return args -> {
            if (userRepository.count() == 0) { // 이미 있으면 중복 방지

                // CODING 10명
                for (int i = 1; i <= 10; i++) {
                    userRepository.save(User.builder()
                            .nickname("coding" + i)
                            .email("coding" + i + "@test.com")
                            .passwordHash(passwordEncoder.encode("abcd123!"))
                            .role(UserRole.USER)
                            .category(UserCategory.CODING)
                            .build());
                }

                // DESIGN 10명
                for (int i = 1; i <= 10; i++) {
                    userRepository.save(User.builder()
                            .nickname("design" + i)
                            .email("design" + i + "@test.com")
                            .passwordHash(passwordEncoder.encode("abcd123!"))
                            .role(UserRole.USER)
                            .category(UserCategory.DESIGN)
                            .build());
                }

                // VIDEO_EDITING 10명
                for (int i = 1; i <= 10; i++) {
                    userRepository.save(User.builder()
                            .nickname("video" + i)
                            .email("video" + i + "@test.com")
                            .passwordHash(passwordEncoder.encode("abcd123!"))
                            .role(UserRole.USER)
                            .category(UserCategory.VIDEO_EDITING)
                            .build());
                }
            log.info("더미 데이터 10개식 생성");
            }
        };
    }
}
