package com.aquatic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("production_records")
public class ProductionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private String processType;
    private Long operatorId;
    private String equipment;
    private LocalDateTime recordTime;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
