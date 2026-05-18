package com.aquatic.service.impl;

import com.aquatic.entity.*;
import com.aquatic.mapper.*;
import com.aquatic.service.ReleaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReleaseServiceImpl implements ReleaseService {

    @Autowired
    private BatchMapper batchMapper;
    @Autowired
    private ReleaseRecordMapper releaseMapper;
    @Autowired
    private TraceabilityCodeMapper traceMapper;
    @Autowired
    private FinishedInspectionMapper finishedMapper;
    @Autowired
    private ProductionRecordMapper productionMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Map<String, Object>> getPendingReleases() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Batch> batches = batchMapper.selectList(
                new LambdaQueryWrapper<Batch>().eq(Batch::getReleaseStatus, "unreleased")
        );
        for (Batch b : batches) {
            // 只显示完成且通过成品质检的
            FinishedInspection fin = finishedMapper.selectOne(
                    new LambdaQueryWrapper<FinishedInspection>().eq(FinishedInspection::getBatchId, b.getId())
            );
            if (fin == null || !"PASS".equals(fin.getResult())) continue;

            List<ProductionRecord> records = productionMapper.selectList(
                    new LambdaQueryWrapper<ProductionRecord>().eq(ProductionRecord::getBatchId, b.getId())
            );
            long stepCount = records.stream().map(ProductionRecord::getProcessType).distinct().count();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("batchId", b.getId());
            row.put("batchNo", b.getBatchNo());
            row.put("productType", b.getProductType());
            row.put("finishedInspection", fin.getResult());
            row.put("processSteps", stepCount + "/3");
            list.add(row);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getReleasedList() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<ReleaseRecord> releases = releaseMapper.selectList(null);
        for (ReleaseRecord r : releases) {
            Batch batch = batchMapper.selectById(r.getBatchId());
            if (batch == null) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("batchId", batch.getId());
            row.put("batchNo", batch.getBatchNo());
            row.put("productType", batch.getProductType());
            row.put("releaseTime", r.getReleaseTime());
            list.add(row);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getTraceabilityRecords() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<TraceabilityCode> codes = traceMapper.selectList(null);
        for (TraceabilityCode tc : codes) {
            Batch batch = batchMapper.selectById(tc.getBatchId());
            if (batch == null) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("batchId", batch.getId());
            row.put("batchNo", batch.getBatchNo());
            row.put("productType", batch.getProductType());
            row.put("traceCode", tc.getTraceCode());
            row.put("generateTime", tc.getGenerateTime());
            row.put("isPublic", tc.getIsPublic() == 1);
            row.put("qrUrl", tc.getQrUrl());
            list.add(row);
        }
        return list;
    }

    @Override
    public Map<String, Object> getReleaseStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        long pending = getPendingReleases().size();
        long released = batchMapper.selectCount(
                new LambdaQueryWrapper<Batch>().eq(Batch::getReleaseStatus, "released")
        );
        stats.put("pendingRelease", pending);
        stats.put("released", released);
        return stats;
    }

    @Override
    public void performRelease(Long batchId, Long managerId) {
        // 创建放行记录
        ReleaseRecord release = new ReleaseRecord();
        release.setBatchId(batchId);
        release.setManagerId(managerId);
        release.setReleaseTime(LocalDateTime.now());
        releaseMapper.insert(release);

        // 更新批次状态
        Batch batch = batchMapper.selectById(batchId);
        batch.setReleaseStatus("released");
        batchMapper.updateById(batch);

        // 生成追溯码
        TraceabilityCode tc = new TraceabilityCode();
        tc.setBatchId(batchId);
        String traceCode = "AQ-" + LocalDateTime.now().getYear() + "-" +
                String.format("%03d", batchId) + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        tc.setTraceCode(traceCode);
        tc.setQrUrl("http://localhost:8080/api/public/trace/" + traceCode);
        tc.setIsPublic(1);
        tc.setGenerateTime(LocalDateTime.now());
        traceMapper.insert(tc);
    }
}
