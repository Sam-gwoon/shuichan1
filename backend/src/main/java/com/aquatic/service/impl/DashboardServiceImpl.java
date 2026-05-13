package com.aquatic.service.impl;

import com.aquatic.dto.DashboardVO;
import com.aquatic.entity.*;
import com.aquatic.mapper.*;
import com.aquatic.service.DashboardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private IncomingInspectionMapper incomingMapper;
    @Autowired
    private FinishedInspectionMapper finishedMapper;
    @Autowired
    private BatchMapper batchMapper;

    @Override
    public DashboardVO getStats() {
        DashboardVO vo = new DashboardVO();

        // 待来料质检
        vo.setPendingIncoming(incomingMapper.selectCount(
                new LambdaQueryWrapper<IncomingInspection>().eq(IncomingInspection::getResult, "PENDING")
        ));

        // 来料质检失败
        vo.setFailedIncoming(incomingMapper.selectCount(
                new LambdaQueryWrapper<IncomingInspection>().eq(IncomingInspection::getResult, "FAIL")
        ));

        // 已完成批次
        long completed = batchMapper.selectCount(
                new LambdaQueryWrapper<Batch>().eq(Batch::getProductionStatus, "completed")
        );

        // 待成品质检（已完成但无成品质检或PENDING）
        long noFinished = 0;
        long pendingFinished = 0;
        long failedFinished = finishedMapper.selectCount(
                new LambdaQueryWrapper<FinishedInspection>().eq(FinishedInspection::getResult, "FAIL")
        );

        java.util.List<Batch> batches = batchMapper.selectList(null);
        for (Batch b : batches) {
            FinishedInspection fin = finishedMapper.selectOne(
                    new LambdaQueryWrapper<FinishedInspection>().eq(FinishedInspection::getBatchId, b.getId())
            );
            if ("completed".equals(b.getProductionStatus())) {
                if (fin == null) {
                    noFinished++;
                } else if ("PENDING".equals(fin.getResult())) {
                    pendingFinished++;
                }
            }
        }
        vo.setPendingFinished(pendingFinished);
        vo.setNoFinishedInspection(noFinished);
        vo.setFailedFinished(failedFinished);

        // 待放行
        vo.setPendingRelease(batchMapper.selectCount(
                new LambdaQueryWrapper<Batch>().eq(Batch::getReleaseStatus, "unreleased")
        ));

        // 已放行
        vo.setReleased(batchMapper.selectCount(
                new LambdaQueryWrapper<Batch>().eq(Batch::getReleaseStatus, "released")
        ));

        return vo;
    }
}
