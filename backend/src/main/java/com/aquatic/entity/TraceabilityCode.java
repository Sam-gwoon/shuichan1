package com.aquatic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("traceability_codes")
public class TraceabilityCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private String traceCode;
    private String qrUrl;
    private Integer isPublic;
    private LocalDateTime generateTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
