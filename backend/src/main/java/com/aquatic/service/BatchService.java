package com.aquatic.service;

import com.aquatic.entity.Batch;
import java.util.List;
import java.util.Map;

public interface BatchService {
    List<Map<String, Object>> listAll(String batchNo, String productType, String productionStatus, String inspectionStatus);
    Map<String, Object> getDetail(Long id);
    void create(Batch batch);
    void update(Batch batch);
    void delete(Long id);
    List<Map<String, Object>> getRecent(int limit);
}
