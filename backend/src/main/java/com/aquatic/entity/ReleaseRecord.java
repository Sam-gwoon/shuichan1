package com.aquatic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("release_records")
public class ReleaseRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private Long managerId;
    private LocalDateTime releaseTime;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
