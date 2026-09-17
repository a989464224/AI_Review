package com.aireview.dto;

import java.time.LocalDateTime;

public record UserVO(Long id, String username, String nickname, LocalDateTime createdAt) {
}
