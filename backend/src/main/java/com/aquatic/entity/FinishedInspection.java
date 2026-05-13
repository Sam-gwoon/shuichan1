package com.aquatic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("finished_inspections")
public class FinishedInspection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private Long inspectorId;
    private String result;
    private String items;
    private String remark;
    private LocalDateTime inspectTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
