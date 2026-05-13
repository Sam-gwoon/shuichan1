package com.aquatic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("incoming_inspections")
public class IncomingInspection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private Long inspectorId;
    private String result;
    private String disposal;
    private String remark;
    private LocalDateTime inspectTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
