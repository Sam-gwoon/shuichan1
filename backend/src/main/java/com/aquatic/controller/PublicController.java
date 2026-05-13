package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private PublicService publicService;

    @GetMapping("/trace/{traceCode}")
    public ApiResult<?> trace(@PathVariable String traceCode) {
        try {
            return ApiResult.success(publicService.getTraceInfo(traceCode));
        } catch (RuntimeException e) {
            return ApiResult.error(404, e.getMessage());
        }
    }
}
