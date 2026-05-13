package com.aquatic.service.impl;

import com.aquatic.entity.*;
import com.aquatic.mapper.*;
import com.aquatic.service.InspectionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InspectionServiceImpl implements InspectionService {

    @Autowired
    private IncomingInspectionMapper incomingMapper;
    @Autowired
    private FinishedInspectionMapper finishedMapper;
    @Autowired
    private BatchMapper batchMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Map<String, Object>> getPendingWorkbench() {
        List<Map<String, Object>> list = new ArrayList<>();

        // 待来料质检
        List<IncomingInspection> incs = incomingMapper.selectList(
                new LambdaQueryWrapper<IncomingInspection>().eq(IncomingInspection::getResult, "PENDING")
        );
        for (IncomingInspection inc : incs) {
            Batch batch = batchMapper.selectById(inc.getBatchId());
            if (batch != null) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("batchId", batch.getId());
                row.put("batchNo", batch.getBatchNo());
                row.put("productType", batch.getProductType());
                row.put("type", "来料质检");
                row.put("status", batch.getProductionStatus());
                row.put("createTime", batch.getCreateTime());
                list.add(row);
            }
        }

        // 待成品质检（生产完成但没有成品质检记录或结果为PENDING的）
        List<Batch> completedBatches = batchMapper.selectList(
                new LambdaQueryWrapper<Batch>().eq(Batch::getProductionStatus, "completed")
        );
        for (Batch batch : completedBatches) {
            FinishedInspection fin = finishedMapper.selectOne(
                    new LambdaQueryWrapper<FinishedInspection>().eq(FinishedInspection::getBatchId, batch.getId())
            );
            if (fin == null || "PENDING".equals(fin.getResult())) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("batchId", batch.getId());
                row.put("batchNo", batch.getBatchNo());
                row.put("productType", batch.getProductType());
                row.put("type", "成品质检");
                row.put("status", batch.getProductionStatus());
                row.put("createTime", batch.getCreateTime());
                list.add(row);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getRecords(String batchNo, String type, String result) {
        List<Map<String, Object>> records = new ArrayList<>();

        // 来料质检记录
        List<IncomingInspection> incs = incomingMapper.selectList(null);
        for (IncomingInspection inc : incs) {
            Batch batch = batchMapper.selectById(inc.getBatchId());
            if (batch == null) continue;
            if (batchNo != null && !batch.getBatchNo().contains(batchNo)) continue;
            if (type != null && !type.equals("来料质检")) continue;
            if (result != null && !result.equals(inc.getResult())) continue;

            User inspector = userMapper.selectById(inc.getInspectorId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("batchNo", batch.getBatchNo());
            row.put("type", "来料质检");
            row.put("inspector", inspector != null ? inspector.getRealName() : "-");
            row.put("result", inc.getResult());
            row.put("time", inc.getInspectTime());
            row.put("remark", inc.getRemark());
            records.add(row);
        }

        // 成品质检记录
        List<FinishedInspection> fins = finishedMapper.selectList(null);
        for (FinishedInspection fin : fins) {
            Batch batch = batchMapper.selectById(fin.getBatchId());
            if (batch == null) continue;
            if (batchNo != null && !batch.getBatchNo().contains(batchNo)) continue;
            if (type != null && !type.equals("成品质检")) continue;
            if (result != null && !result.equals(fin.getResult())) continue;

            User inspector = userMapper.selectById(fin.getInspectorId());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("batchNo", batch.getBatchNo());
            row.put("type", "成品质检");
            row.put("inspector", inspector != null ? inspector.getRealName() : "-");
            row.put("result", fin.getResult());
            row.put("time", fin.getInspectTime());
            row.put("remark", fin.getRemark());
            records.add(row);
        }

        records.sort((a, b) -> {
            Object ta = a.get("time");
            Object tb = b.get("time");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.toString().compareTo(ta.toString());
        });
        return records;
    }

    @Override
    public void performIncoming(IncomingInspection inspection) {
        inspection.setInspectTime(LocalDateTime.now());
        if (inspection.getId() != null) {
            incomingMapper.updateById(inspection);
        } else {
            incomingMapper.insert(inspection);
        }
        // 更新批次状态
        if ("PASS".equals(inspection.getResult())) {
            Batch batch = batchMapper.selectById(inspection.getBatchId());
            if (batch != null && "created".equals(batch.getProductionStatus())) {
                batch.setProductionStatus("processing");
                batchMapper.updateById(batch);
            }
        }
    }

    @Override
    public void performFinished(FinishedInspection inspection) {
        inspection.setInspectTime(LocalDateTime.now());
        if (inspection.getId() != null) {
            finishedMapper.updateById(inspection);
        } else {
            finishedMapper.insert(inspection);
        }
    }
}
