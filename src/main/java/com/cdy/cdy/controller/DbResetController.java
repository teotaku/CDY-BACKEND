// src/main/java/com/yourapp/dev/DbResetController.java
package com.cdy.cdy.controller;

import com.cdy.cdy.service.DbResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
//@Profile({"dev","test"})
@RequiredArgsConstructor
@RequestMapping("/__dev/db")
public class DbResetController {

    private final DbResetService service;

    @PostMapping("/reset")
    public String reset() {
        int n = service.reset();
        return "TRUNCATE OK (tables=" + n + ")";
    }
}
