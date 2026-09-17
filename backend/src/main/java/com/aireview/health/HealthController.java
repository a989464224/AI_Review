package com.aireview.health;

import com.aireview.common.Result;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping
    public Result<Map<String, String>> health() {
        return Result.ok(Map.of("status", "UP"));
    }
}
