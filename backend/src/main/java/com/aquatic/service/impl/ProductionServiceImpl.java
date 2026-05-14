package com.aquatic.service.impl;

import com.aquatic.entity.Batch;
import com.aquatic.entity.ProductionRecord;
import com.aquatic.mapper.BatchMapper;
import com.aquatic.mapper.ProductionRecordMapper;
import com.aquatic.service.ProductionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductionServiceImpl implements ProductionService {

    @Autowired
    private ProductionRecordMapper recordMapper;
    @Autowired
    private BatchMapper batchMapper;

    @Override
    public List<ProductionRecord> getByBatchId(Long batchId) {
        return recordMapper.selectList(
                new LambdaQueryWrapper<ProductionRecord>().eq(ProductionRecord::getBatchId, batchId)
        );
    }

    @Override
    public void addRecord(ProductionRecord record) {
        record.setRecordTime(LocalDateTime.now());
        recordMapper.insert(record);
        // 检查是否能标记为完成
        Batch batch = batchMapper.selectById(record.getBatchId());
        if (batch != null) {
            List<ProductionRecord> records = getByBatchId(record.getBatchId());
            boolean hasSorting = false, hasCleaning = false, hasPackaging = false;
            for (ProductionRecord r : records) {
                if ("sorting".equals(r.getProcessType())) hasSorting = true;
                if ("cleaning".equals(r.getProcessType())) hasCleaning = true;
                if ("packaging".equals(r.getProcessType())) hasPackaging = true;
            }
            if (hasSorting && hasCleaning && hasPackaging) {
                batch.setProductionStatus("completed");
                batchMapper.updateById(batch);
            }
        }
    }

    @Override
    public void updateRecord(ProductionRecord record) {
        recordMapper.updateById(record);
    }

    @Override
    public void deleteRecord(Long id) {
        recordMapper.deleteById(id);
    }
}
