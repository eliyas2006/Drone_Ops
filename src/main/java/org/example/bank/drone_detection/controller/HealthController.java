package org.example.bank.drone_detection.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {

        Map<String, Object> response = new LinkedHashMap<>();

        try {

            Integer result =
                    jdbcTemplate.queryForObject(
                            "SELECT 1",
                            Integer.class
                    );

            response.put("status", "UP");
            response.put("application", "DroneOps");
            response.put("database", result != null && result == 1
                    ? "UP"
                    : "DOWN");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("status", "DOWN");
            response.put("application", "DroneOps");
            response.put("database", "DOWN");
            response.put("error", "Database connection failed");

            return ResponseEntity
                    .status(503)
                    .body(response);
        }
    }
}