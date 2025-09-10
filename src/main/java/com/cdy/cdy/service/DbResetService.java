// src/main/java/com/yourapp/dev/DbResetService.java
package com.cdy.cdy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@Profile({"dev","test"}) // 운영 금지
@RequiredArgsConstructor
public class DbResetService {

    private final JdbcTemplate jdbc;

    @Transactional
    public int reset() {
        String db = jdbc.queryForObject("SELECT DATABASE()", String.class);

        List<String> tables = jdbc.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = ? AND table_type = 'BASE TABLE'",
                String.class, db);

        jdbc.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String t : tables) {
            jdbc.execute("TRUNCATE TABLE `" + t + "`");
        }
        jdbc.execute("SET FOREIGN_KEY_CHECKS = 1");

        return tables.size();
    }
}
