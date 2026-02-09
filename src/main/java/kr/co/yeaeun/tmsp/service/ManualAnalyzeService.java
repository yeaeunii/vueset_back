package kr.co.yeaeun.tmsp.service;

import kr.co.yeaeun.tmsp.model.Manual.ManualAnalyzeRequest;

public interface ManualAnalyzeService {
    void analyzeAsync(String taskId, ManualAnalyzeRequest request);
}