package com.aquatic.service;

import com.aquatic.entity.ProductionRecord;
import java.util.List;

public interface ProductionService {
    List<ProductionRecord> getByBatchId(Long batchId);
    void addRecord(ProductionRecord record);
    void updateRecord(ProductionRecord record);
    void deleteRecord(Long id);
}
