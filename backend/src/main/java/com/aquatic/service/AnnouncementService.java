package com.aquatic.service;

import com.aquatic.entity.Announcement;

public interface AnnouncementService {
    Announcement getLatest();
    void save(Announcement announcement);
}
