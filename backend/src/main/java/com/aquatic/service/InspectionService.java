package com.aquatic.service;

import com.aquatic.entity.FinishedInspection;
import com.aquatic.entity.IncomingInspection;
import java.util.List;
import java.util.Map;

public interface InspectionService {
    List<Map<String, Object>> getPendingWorkbench();
    List<Map<String, Object>> getRecords(String batchNo, String type, String result);
    void performIncoming(IncomingInspection inspection);
    void performFinished(FinishedInspection inspection);
}
