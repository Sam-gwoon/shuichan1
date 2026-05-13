package com.aquatic.service;

import com.aquatic.entity.EnterpriseInfo;

public interface EnterpriseService {
    EnterpriseInfo get();
    void update(EnterpriseInfo info);
}
