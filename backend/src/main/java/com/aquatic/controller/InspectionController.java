package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.entity.FinishedInspection;
import com.aquatic.entity.IncomingInspection;
import com.aquatic.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspections")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @GetMapping("/workbench")
    public ApiResult<?> workbench() {
        return ApiResult.success(inspectionService.getPendingWorkbench());
    }

    @GetMapping("/records")
    public ApiResult<?> records(@RequestParam(required = false) String batchNo,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) String result) {
        return ApiResult.success(inspectionService.getRecords(batchNo, type, result));
    }

    @PostMapping("/incoming")
    @PreAuthorize("hasAnyAuthority('INSPECTOR', 'ADMIN')")
    public ApiResult<?> performIncoming(@RequestBody IncomingInspection inspection) {
        inspectionService.performIncoming(inspection);
        return ApiResult.success();
    }

    @PostMapping("/finished")
    @PreAuthorize("hasAnyAuthority('INSPECTOR', 'ADMIN')")
    public ApiResult<?> performFinished(@RequestBody FinishedInspection inspection) {
        inspectionService.performFinished(inspection);
        return ApiResult.success();
    }
}
