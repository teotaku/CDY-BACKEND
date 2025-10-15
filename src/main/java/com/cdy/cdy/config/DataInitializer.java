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

                // CODING 5명
                for (int i = 1; i <= 5; i++) {
                    userRepository.save(User.builder()
                            .nickname("coding" + i)
                                    .name("name1"+ i)
                            .email("coding" + i + "@test.com")
                            .phoneNumber("01012345678")
                            .passwordHash(passwordEncoder.encode("abcd1234A!"))
                            .role(UserRole.USER)
                            .category(UserCategory.CODING)
                            .build());
                }

                // DESIGN 5명
                for (int i = 1; i <= 5; i++) {
                    userRepository.save(User.builder()
                            .nickname("design" + i)
                            .email("design" + i + "@test.com")
                            .name("name1"+ i)
                            .phoneNumber("01012345678")
                            .passwordHash(passwordEncoder.encode("abcd1234A!"))
                            .role(UserRole.USER)
                            .category(UserCategory.DESIGN)
                            .build());
                }

                // VIDEO 5명
                for (int i = 1; i <= 5; i++) {
                    userRepository.save(User.builder()
                            .nickname("video" + i)
                            .name("name1"+ i)
                            .email("video" + i + "@test.com")
                            .phoneNumber("01012345678")
                            .passwordHash(passwordEncoder.encode("abcd1234A!"))
                            .role(UserRole.USER)
                            .category(UserCategory.VIDEO_EDITING)
                            .build());
                }

                log.info("더미 데이터 각 카테고리 5명씩 생성 완료 ✅");
            }
        };
    }
}
