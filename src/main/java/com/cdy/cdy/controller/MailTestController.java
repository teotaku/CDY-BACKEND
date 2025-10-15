package com.cdy.cdy.controller;

import com.cdy.cdy.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailTestController {

    private final MailService mailService;

    @GetMapping("/test")
    public ResponseEntity<String> sendTestMail() {
        mailService.sendMail(
                "받는사람@gmail.com",
                "[CDY] 메일 전송 테스트",
                "Spring Boot에서 Gmail SMTP로 전송 성공 ✅"
        );
        return ResponseEntity.ok("테스트 메일 전송 완료");
    }
}
