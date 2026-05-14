package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.entity.ProductionRecord;
import com.aquatic.service.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    @GetMapping("/batch/{batchId}")
    public ApiResult<?> getByBatchId(@PathVariable Long batchId) {
        return ApiResult.success(productionService.getByBatchId(batchId));
    }

    @PostMapping("/records")
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'PROD_MANAGER', 'ADMIN')")
    public ApiResult<?> addRecord(@RequestBody ProductionRecord record) {
        productionService.addRecord(record);
        return ApiResult.success();
    }

    @PutMapping("/records/{id}")
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'PROD_MANAGER', 'ADMIN')")
    public ApiResult<?> updateRecord(@PathVariable Long id, @RequestBody ProductionRecord record) {
        record.setId(id);
        productionService.updateRecord(record);
        return ApiResult.success();
    }

    @DeleteMapping("/records/{id}")
    @PreAuthorize("hasAnyAuthority('OPERATOR', 'PROD_MANAGER', 'ADMIN')")
    public ApiResult<?> deleteRecord(@PathVariable Long id) {
        productionService.deleteRecord(id);
        return ApiResult.success();
    }
}
