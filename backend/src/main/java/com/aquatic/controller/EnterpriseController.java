package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.entity.EnterpriseInfo;
import com.aquatic.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enterprise")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping
    public ApiResult<EnterpriseInfo> get() {
        return ApiResult.success(enterpriseService.get());
    }

    @PutMapping
    public ApiResult<?> update(@RequestBody EnterpriseInfo info) {
        enterpriseService.update(info);
        return ApiResult.success();
    }
}
