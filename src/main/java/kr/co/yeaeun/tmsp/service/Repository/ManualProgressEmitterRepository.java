package kr.co.yeaeun.tmsp.service.Repository;

import kr.co.yeaeun.tmsp.model.Manual.ManualAnalyzeRequest;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ManualProgressEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, ManualAnalyzeRequest> requests = new ConcurrentHashMap<>();

    public void save(String taskId, SseEmitter emitter) {
        emitters.put(taskId, emitter);
    }

    public SseEmitter get(String taskId) {
        return emitters.get(taskId);
    }

    public void saveRequest(String taskId, ManualAnalyzeRequest request) {
        requests.put(taskId, request);
    }

    public ManualAnalyzeRequest getRequest(String taskId) {
        return requests.get(taskId);
    }

    public void remove(String taskId) {
        emitters.remove(taskId);
        requests.remove(taskId);
    }
}
