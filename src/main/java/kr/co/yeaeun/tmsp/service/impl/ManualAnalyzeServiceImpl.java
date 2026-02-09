package kr.co.yeaeun.tmsp.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.microsoft.playwright.options.WaitUntilState;
import kr.co.yeaeun.tmsp.enums.ManualProgressCode;
import kr.co.yeaeun.tmsp.model.Manual.DTO.ElementDto;
import kr.co.yeaeun.tmsp.model.Manual.DTO.ScreenDto;
import kr.co.yeaeun.tmsp.model.Manual.ManualAnalyzeRequest;
import kr.co.yeaeun.tmsp.model.Manual.ManualAnalyzeResponse;
import kr.co.yeaeun.tmsp.service.ManualAnalyzeService;
import kr.co.yeaeun.tmsp.service.Repository.ManualProgressEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.microsoft.playwright.*;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ManualAnalyzeServiceImpl implements ManualAnalyzeService {

    private final ManualProgressEmitterRepository emitterRepository;
    private final ObjectMapper objectMapper;

    @Async
    @Override
    public void analyzeAsync(String taskId, ManualAnalyzeRequest request) {

        SseEmitter emitter = emitterRepository.get(taskId);

        try {
            sendProgress(emitter, ManualProgressCode.PAGE_LOAD);
            sendProgress(emitter, ManualProgressCode.SCREEN_CAPTURE);

            ManualAnalyzeResponse response = buildResponse(request, taskId);

            sendProgress(emitter, ManualProgressCode.DESCRIPTION);
            sendDone(emitter, response);
            emitter.complete();

        } catch (Exception e) {
            sendProgressSafe(emitter, ManualProgressCode.ERROR);
            if (emitter != null) {
                emitter.completeWithError(e);
            }
        }
    }

    private void sendProgress(SseEmitter emitter, ManualProgressCode code) throws IOException {
        if (emitter != null) {
            emitter.send(
                    SseEmitter.event()
                            .name("progress")
                            .data(code.name())
            );
        }
    }

    private void sendDone(SseEmitter emitter, ManualAnalyzeResponse response) throws IOException {
        if (emitter != null) {
            String json = objectMapper.writeValueAsString(response);

            emitter.send(
                    SseEmitter.event()
                            .name("done")
                            .data(json)
            );
        }
    }

    private void sendProgressSafe(SseEmitter emitter, ManualProgressCode code) {
        try {
            sendProgress(emitter, code);
        } catch (Exception ignored) {}
    }

    private ManualAnalyzeResponse buildResponse(ManualAnalyzeRequest request, String taskId) {

        String imagePath = captureScreen(request.getUrl(), taskId);

        ElementDto element = new ElementDto(
                1,
                "예시 버튼",
                "화면에서 탐지된 기능입니다.",
                "high"
        );

        ScreenDto screen = new ScreenDto(
                0,
                imagePath,
                List.of(element)
        );

        return new ManualAnalyzeResponse(List.of(screen));
    }

    public byte[] exportHtml(ManualAnalyzeResponse response) {

        StringBuilder html = new StringBuilder();

        html.append("""
            <html>
            <head>
              <meta charset="UTF-8">
              <title>사용자 매뉴얼</title>
              <style>
                body { font-family: Arial; }
                h1 { border-bottom: 2px solid #333; }
                img { max-width: 600px; }
              </style>
            </head>
            <body>
            <h1>자동 생성 사용자 매뉴얼</h1>
        """);

        for (int i = 0; i < response.getScreens().size(); i++) {
            ScreenDto screen = response.getScreens().get(i);

            html.append("<h2>")
                    .append(i + 1)
                    .append(". 화면 ")
                    .append(i + 1)
                    .append("</h2>");

            html.append("<img src='")
                    .append(screen.getImage())
                    .append("'/>");

            html.append("<ul>");
            screen.getElements().forEach(el -> {
                html.append("<li>")
                        .append(el.getLabel())
                        .append(" : ")
                        .append(el.getDescription())
                        .append("</li>");
            });
            html.append("</ul>");
        }

        html.append("</body></html>");

        return html.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportHwp(ManualAnalyzeResponse response) {
        return new byte[0];
    }

    private String captureScreen(String url, String taskId) {

        String dir = "screenshots";
        new File(dir).mkdirs();
        String filePath = dir + "/" + taskId + ".png";

        System.out.println(" 캡쳐 시작 URL = " + url);

        try (Playwright playwright = Playwright.create()) {

            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            Page page = browser.newPage();

            page.onConsoleMessage(msg ->
                    System.out.println(" PAGE LOG: " + msg.text())
            );

            page.navigate(
                    url,
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE)
            );

            System.out.println(" 페이지 로딩 완료");

            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(Paths.get(filePath))
                            .setFullPage(true)
            );

            System.out.println("캡쳐 완료: " + filePath);

            browser.close();

        } catch (Exception e) {
            throw new RuntimeException("화면 캡쳐 실패: " + url, e);
        }

        return "/" + filePath;
    }
}
