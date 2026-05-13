package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.entity.Batch;
import com.aquatic.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @GetMapping
    public ApiResult<?> list(@RequestParam(required = false) String batchNo,
                              @RequestParam(required = false) String productType,
                              @RequestParam(required = false) String productionStatus,
                              @RequestParam(required = false) String inspectionStatus) {
        return ApiResult.success(batchService.listAll(batchNo, productType, productionStatus, inspectionStatus));
    }

    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResult.success(batchService.getDetail(id));
    }

    @PostMapping
    public ApiResult<?> create(@RequestBody Batch batch) {
        batchService.create(batch);
        return ApiResult.success();
    }

    @GetMapping("/recent")
    public ApiResult<?> recent(@RequestParam(defaultValue = "10") int limit) {
        return ApiResult.success(batchService.getRecent(limit));
    }
}
