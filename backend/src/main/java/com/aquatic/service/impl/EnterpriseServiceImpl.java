package com.aquatic.service.impl;

import com.aquatic.entity.EnterpriseInfo;
import com.aquatic.mapper.EnterpriseInfoMapper;
import com.aquatic.service.EnterpriseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {

    @Autowired
    private EnterpriseInfoMapper mapper;

    @Override
    public EnterpriseInfo get() {
        return mapper.selectOne(new LambdaQueryWrapper<EnterpriseInfo>().last("LIMIT 1"));
    }

    @Override
    public void update(EnterpriseInfo info) {
        mapper.updateById(info);
    }
}
