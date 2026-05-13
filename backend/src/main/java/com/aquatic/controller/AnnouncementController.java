package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.entity.Announcement;
import com.aquatic.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/latest")
    public ApiResult<Announcement> getLatest() {
        return ApiResult.success(announcementService.getLatest());
    }

    @PutMapping
    public ApiResult<?> save(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return ApiResult.success();
    }
}
