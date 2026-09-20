package com.aireview.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("document")
public class Document {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String fileName;
    private String content;
    private Long fileSize;
    private Integer contentVersion;
    private String indexStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
