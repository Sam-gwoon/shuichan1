package com.aquatic.service;

import java.util.List;
import java.util.Map;

public interface ReleaseService {
    List<Map<String, Object>> getPendingReleases();
    List<Map<String, Object>> getReleasedList();
    List<Map<String, Object>> getTraceabilityRecords();
    Map<String, Object> getReleaseStats();
    void performRelease(Long batchId, Long managerId);
}
