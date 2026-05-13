package com.aquatic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("batches")
public class Batch {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String productType;
    private String rawMaterialType;
    private String source;
    private String origin;
    private LocalDateTime receiptTime;
    private BigDecimal quantity;
    private String certificateNo;
    private String remark;
    private Long operatorId;
    private String productionStatus;
    private String releaseStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
