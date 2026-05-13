package com.aquatic.service.impl;

import com.aquatic.entity.*;
import com.aquatic.mapper.*;
import com.aquatic.service.PublicService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PublicServiceImpl implements PublicService {

    @Autowired
    private TraceabilityCodeMapper traceMapper;
    @Autowired
    private BatchMapper batchMapper;
    @Autowired
    private EnterpriseInfoMapper enterpriseMapper;
    @Autowired
    private IncomingInspectionMapper incomingMapper;
    @Autowired
    private FinishedInspectionMapper finishedMapper;
    @Autowired
    private ReleaseRecordMapper releaseMapper;

    @Override
    public Map<String, Object> getTraceInfo(String traceCode) {
        TraceabilityCode tc = traceMapper.selectOne(
                new LambdaQueryWrapper<TraceabilityCode>().eq(TraceabilityCode::getTraceCode, traceCode)
        );
        if (tc == null || tc.getIsPublic() != 1) {
            throw new RuntimeException("追溯码无效或未公开");
        }

        Map<String, Object> result = new LinkedHashMap<>();

        // 企业信息
        EnterpriseInfo enterprise = enterpriseMapper.selectOne(
                new LambdaQueryWrapper<EnterpriseInfo>().last("LIMIT 1")
        );
        if (enterprise != null) {
            result.put("enterprise", enterprise);
        }

        // 批次信息
        Batch batch = batchMapper.selectById(tc.getBatchId());
        if (batch != null) {
            Map<String, Object> batchInfo = new LinkedHashMap<>();
            batchInfo.put("batchNo", batch.getBatchNo());
            batchInfo.put("productType", batch.getProductType());
            batchInfo.put("traceCode", tc.getTraceCode());
            result.put("batch", batchInfo);
        }

        // 来料质检
        IncomingInspection inc = incomingMapper.selectOne(
                new LambdaQueryWrapper<IncomingInspection>().eq(IncomingInspection::getBatchId, tc.getBatchId())
        );
        if (inc != null) {
            Map<String, Object> incInfo = new LinkedHashMap<>();
            incInfo.put("result", inc.getResult());
            incInfo.put("time", inc.getInspectTime());
            result.put("incomingInspection", incInfo);
        }

        // 成品质检
        FinishedInspection fin = finishedMapper.selectOne(
                new LambdaQueryWrapper<FinishedInspection>().eq(FinishedInspection::getBatchId, tc.getBatchId())
        );
        if (fin != null) {
            Map<String, Object> finInfo = new LinkedHashMap<>();
            finInfo.put("result", fin.getResult());
            finInfo.put("time", fin.getInspectTime());
            result.put("finishedInspection", finInfo);
        }

        // 放行信息
        ReleaseRecord release = releaseMapper.selectOne(
                new LambdaQueryWrapper<ReleaseRecord>().eq(ReleaseRecord::getBatchId, tc.getBatchId())
        );
        if (release != null) {
            Map<String, Object> relInfo = new LinkedHashMap<>();
            relInfo.put("status", "已放行");
            relInfo.put("time", release.getReleaseTime());
            result.put("release", relInfo);
        }

        return result;
    }
}
