package com.aquatic.dto;

import lombok.Data;

@Data
public class DashboardVO {
    private long pendingIncoming;    // 待来料质检数
    private long pendingFinished;    // 待成品质检数
    private long pendingRelease;     // 待放行数
    private long released;           // 已放行数
    private long failedIncoming;     // 来料质检失败数
    private long noFinishedInspection; // 已完成但无成品质检数
    private long failedFinished;     // 成品质检失败数
}
