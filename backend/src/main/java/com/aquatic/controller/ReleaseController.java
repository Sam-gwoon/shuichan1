package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.service.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/release")
public class ReleaseController {

    @Autowired
    private ReleaseService releaseService;

    @GetMapping("/stats")
    public ApiResult<Map<String, Object>> stats() {
        return ApiResult.success(releaseService.getReleaseStats());
    }

    @GetMapping("/pending")
    public ApiResult<?> pending() {
        return ApiResult.success(releaseService.getPendingReleases());
    }

    @GetMapping("/released")
    public ApiResult<?> released() {
        return ApiResult.success(releaseService.getReleasedList());
    }

    @GetMapping("/traceability")
    public ApiResult<?> traceability() {
        return ApiResult.success(releaseService.getTraceabilityRecords());
    }

    @PostMapping("/execute/{batchId}")
    public ApiResult<?> execute(@PathVariable Long batchId, @RequestParam Long managerId) {
        releaseService.performRelease(batchId, managerId);
        return ApiResult.success();
    }
}
