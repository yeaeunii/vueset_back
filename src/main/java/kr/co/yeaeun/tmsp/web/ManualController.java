package kr.co.yeaeun.tmsp.web;

import kr.co.yeaeun.tmsp.model.Manual.ManualAnalyzeRequest;
import kr.co.yeaeun.tmsp.model.Manual.ManualAnalyzeResponse;
import kr.co.yeaeun.tmsp.service.ManualAnalyzeService;
import kr.co.yeaeun.tmsp.service.Repository.ManualProgressEmitterRepository;
import kr.co.yeaeun.tmsp.service.impl.ManualAnalyzeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/manual")
@RequiredArgsConstructor
public class ManualController {

    private final ManualAnalyzeService manualAnalyzeService;
    private final ManualAnalyzeServiceImpl manualAnalyzeServiceimpl;
    private final ManualProgressEmitterRepository emitterRepository;

    @PostMapping("/analyze")
    public Map<String, String> analyze(
            @RequestBody ManualAnalyzeRequest request
    ) {
        String taskId = UUID.randomUUID().toString();

        // ✅ 요청 저장
        emitterRepository.saveRequest(taskId, request);

        // taskId 반환 (분석은 SSE 연결 후 시작)
        return Map.of("taskId", taskId);
    }

    @GetMapping("/progress/{taskId}")
    public SseEmitter connect(@PathVariable String taskId) {

        System.out.println("SSE 연결 요청 taskId = " + taskId);

        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);
        emitterRepository.save(taskId, emitter);

        emitter.onCompletion(() -> emitterRepository.remove(taskId));
        emitter.onTimeout(() -> emitterRepository.remove(taskId));
        emitter.onError(e -> emitterRepository.remove(taskId));

        //  여기서 분석 시작 (emitter가 존재하는 상태)
        ManualAnalyzeRequest request = emitterRepository.getRequest(taskId);
        if (request != null) {
            manualAnalyzeService.analyzeAsync(taskId, request);
        } else {
            try {
                emitter.send(SseEmitter.event().name("progress").data("ERROR"));
            } catch (Exception ignored) {}
            emitter.complete();
        }

        return emitter;
    }

    @PostMapping("/export/html")
    public ResponseEntity<byte[]> exportHtml(
            @RequestBody ManualAnalyzeResponse response
    ) {
        byte[] html = manualAnalyzeServiceimpl.exportHtml(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=manual.html")
                .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                .body(html);
    }

    @PostMapping("/export/hwp")
    public ResponseEntity<byte[]> exportPdf(
            @RequestBody ManualAnalyzeResponse response
    ) {
        byte[] pdf = manualAnalyzeServiceimpl.exportHwp(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=manual.hwp")
                .header(HttpHeaders.CONTENT_TYPE, "application/hwp")
                .body(pdf);
    }
}
