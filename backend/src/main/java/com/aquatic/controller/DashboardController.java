package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.dto.DashboardVO;
import com.aquatic.service.BatchService;
import com.aquatic.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private BatchService batchService;

    @GetMapping("/stats")
    public ApiResult<DashboardVO> getStats() {
        return ApiResult.success(dashboardService.getStats());
    }

    @GetMapping("/recent-batches")
    public ApiResult<?> getRecentBatches() {
        return ApiResult.success(batchService.getRecent(10));
    }
}
