package com.aquatic.service.impl;

import com.aquatic.entity.*;
import com.aquatic.mapper.*;
import com.aquatic.service.BatchService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    private BatchMapper batchMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IncomingInspectionMapper incomingMapper;
    @Autowired
    private FinishedInspectionMapper finishedMapper;
    @Autowired
    private ProductionRecordMapper productionMapper;
    @Autowired
    private ReleaseRecordMapper releaseMapper;
    @Autowired
    private TraceabilityCodeMapper traceMapper;

    @Override
    public List<Map<String, Object>> listAll(String batchNo, String productType, String productionStatus, String inspectionStatus) {
        LambdaQueryWrapper<Batch> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(batchNo)) qw.like(Batch::getBatchNo, batchNo);
        if (StringUtils.hasText(productType)) qw.like(Batch::getProductType, productType);
        if (StringUtils.hasText(productionStatus)) qw.eq(Batch::getProductionStatus, productionStatus);
        qw.orderByDesc(Batch::getCreateTime);

        List<Batch> batches = batchMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Batch b : batches) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", b.getId());
            row.put("batchNo", b.getBatchNo());
            row.put("productType", b.getProductType());
            row.put("rawMaterialType", b.getRawMaterialType());
            row.put("productionStatus", b.getProductionStatus());
            row.put("releaseStatus", b.getReleaseStatus());
            row.put("createTime", b.getCreateTime());

            // 操作人
            User operator = userMapper.selectById(b.getOperatorId());
            row.put("operatorName", operator != null ? operator.getRealName() : "-");

            // 来料质检状态
            IncomingInspection inc = incomingMapper.selectOne(
                    new LambdaQueryWrapper<IncomingInspection>().eq(IncomingInspection::getBatchId, b.getId())
            );
            row.put("incomingInspection", inc != null ? inc.getResult() : "PENDING");

            // 若按质检状态筛选
            if (StringUtils.hasText(inspectionStatus)) {
                String current = inc != null ? inc.getResult() : "PENDING";
                if (!inspectionStatus.equalsIgnoreCase(current)) continue;
            }

            // 追溯码
            TraceabilityCode tc = traceMapper.selectOne(
                    new LambdaQueryWrapper<TraceabilityCode>().eq(TraceabilityCode::getBatchId, b.getId())
            );
            row.put("traceCode", tc != null ? tc.getTraceCode() : null);
            row.put("tracePublic", tc != null && tc.getIsPublic() == 1);

            result.add(row);
        }
        return result;
    }

    @Override
    public Map<String, Object> getDetail(Long id) {
        Batch batch = batchMapper.selectById(id);
        if (batch == null) throw new RuntimeException("批次不存在");

        Map<String, Object> map = new LinkedHashMap<>();

        // 基本信息
        map.put("batch", batch);
        User operator = userMapper.selectById(batch.getOperatorId());
        map.put("operator", operator != null ? operator.getRealName() : "-");

        // 来料质检
        IncomingInspection inc = incomingMapper.selectOne(
                new LambdaQueryWrapper<IncomingInspection>().eq(IncomingInspection::getBatchId, id)
        );
        if (inc != null) {
            User inspector = userMapper.selectById(inc.getInspectorId());
            Map<String, Object> incMap = new LinkedHashMap<>();
            incMap.put("inspection", inc);
            incMap.put("inspectorName", inspector != null ? inspector.getRealName() : "-");
            map.put("incomingInspection", incMap);
        }

        // 成品质检
        FinishedInspection fin = finishedMapper.selectOne(
                new LambdaQueryWrapper<FinishedInspection>().eq(FinishedInspection::getBatchId, id)
        );
        if (fin != null) {
            User inspector = userMapper.selectById(fin.getInspectorId());
            Map<String, Object> finMap = new LinkedHashMap<>();
            finMap.put("inspection", fin);
            finMap.put("inspectorName", inspector != null ? inspector.getRealName() : "-");
            map.put("finishedInspection", finMap);
        }

        // 生产工序记录
        List<ProductionRecord> records = productionMapper.selectList(
                new LambdaQueryWrapper<ProductionRecord>().eq(ProductionRecord::getBatchId, id)
        );
        map.put("productionRecords", records);

        // 放行记录
        ReleaseRecord release = releaseMapper.selectOne(
                new LambdaQueryWrapper<ReleaseRecord>().eq(ReleaseRecord::getBatchId, id)
        );
        if (release != null) {
            User manager = userMapper.selectById(release.getManagerId());
            Map<String, Object> relMap = new LinkedHashMap<>();
            relMap.put("release", release);
            relMap.put("managerName", manager != null ? manager.getRealName() : "-");
            map.put("releaseRecord", relMap);
        }

        // 追溯码
        TraceabilityCode tc = traceMapper.selectOne(
                new LambdaQueryWrapper<TraceabilityCode>().eq(TraceabilityCode::getBatchId, id)
        );
        if (tc != null) {
            map.put("traceability", tc);
        }

        // 操作历史（组合所有时间线事件）
        List<Map<String, Object>> history = new ArrayList<>();
        addHistory(history, "批次登记", operator != null ? operator.getRealName() : "-", "已登记", batch.getCreateTime(), "初始批次登记记录");
        if (inc != null && inc.getInspectTime() != null) {
            User insp = userMapper.selectById(inc.getInspectorId());
            addHistory(history, "来料质检", insp != null ? insp.getRealName() : "-", inc.getResult(), inc.getInspectTime(), inc.getRemark());
        }
        for (ProductionRecord pr : records) {
            User op = userMapper.selectById(pr.getOperatorId());
            String type = "";
            switch (pr.getProcessType()) {
                case "sorting": type = "分拣记录"; break;
                case "cleaning": type = "清洗记录"; break;
                case "packaging": type = "包装记录"; break;
            }
            addHistory(history, type, op != null ? op.getRealName() : "-", "已记录", pr.getRecordTime(), pr.getEquipment());
        }
        if (fin != null && fin.getInspectTime() != null) {
            User insp = userMapper.selectById(fin.getInspectorId());
            addHistory(history, "成品质检", insp != null ? insp.getRealName() : "-", fin.getResult(), fin.getInspectTime(), fin.getRemark());
        }
        if (release != null) {
            User mgr = userMapper.selectById(release.getManagerId());
            addHistory(history, "产品放行", mgr != null ? mgr.getRealName() : "-", "已放行", release.getReleaseTime(), release.getRemark());
        }
        history.sort((a, b) -> {
            Object ta = a.get("time");
            Object tb = b.get("time");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return ta.toString().compareTo(tb.toString());
        });
        map.put("history", history);

        return map;
    }

    private void addHistory(List<Map<String, Object>> list, String action, String person, String result, Object time, String remark) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("action", action);
        item.put("person", person);
        item.put("result", result);
        item.put("time", time);
        item.put("remark", remark);
        list.add(item);
    }

    @Override
    public void create(Batch batch) {
        batch.setProductionStatus("created");
        batch.setReleaseStatus("unreleased");
        batchMapper.insert(batch);
    }

    @Override
    public List<Map<String, Object>> getRecent(int limit) {
        LambdaQueryWrapper<Batch> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(Batch::getCreateTime).last("LIMIT " + limit);
        List<Batch> batches = batchMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Batch b : batches) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", b.getId());
            row.put("batchNo", b.getBatchNo());
            row.put("productType", b.getProductType());
            row.put("productionStatus", b.getProductionStatus());
            row.put("updateTime", b.getUpdateTime());
            IncomingInspection inc = incomingMapper.selectOne(
                    new LambdaQueryWrapper<IncomingInspection>().eq(IncomingInspection::getBatchId, b.getId())
            );
            row.put("incomingInspection", inc != null ? inc.getResult() : "PENDING");
            result.add(row);
        }
        return result;
    }
}
