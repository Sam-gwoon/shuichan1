package com.aquatic.service.impl;

import com.aquatic.entity.Announcement;
import com.aquatic.mapper.AnnouncementMapper;
import com.aquatic.service.AnnouncementService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper mapper;

    @Override
    public Announcement getLatest() {
        return mapper.selectOne(
                new LambdaQueryWrapper<Announcement>()
                        .orderByDesc(Announcement::getCreateTime)
                        .last("LIMIT 1")
        );
    }

    @Override
    public void save(Announcement announcement) {
        Announcement exist = getLatest();
        if (exist != null) {
            announcement.setId(exist.getId());
            mapper.updateById(announcement);
        } else {
            mapper.insert(announcement);
        }
    }
}
