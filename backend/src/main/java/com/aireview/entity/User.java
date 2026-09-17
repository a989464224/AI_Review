package com.aireview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("`user`")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** BCrypt 哈希，禁止出现在任何对外 DTO 中。 */
    private String passwordHash;

    private String nickname;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
