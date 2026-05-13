package com.aquatic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("enterprise_info")
public class EnterpriseInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String licenseNo;
    private String address;
    private String phone;
    private String scope;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
