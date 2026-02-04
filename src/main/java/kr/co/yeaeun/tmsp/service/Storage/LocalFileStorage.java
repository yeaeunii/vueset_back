package kr.co.yeaeun.tmsp.service.Storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class LocalFileStorage {

    private static final String BASE_DIR = "uploads/newsletter";

    public String save(MultipartFile file) {
        try {
            String ext = getExtension(file.getOriginalFilename());  // "photo.png" --> "png"
            String fileName = UUID.randomUUID() + "." + ext;   // UUID로 파일명 생성

            LocalDate now = LocalDate.now();
            Path dir = Paths.get(
                    BASE_DIR,
                    String.valueOf(now.getYear()),
                    String.format("%02d", now.getMonthValue())
            );

            Files.createDirectories(dir); // 해당 경로 없을 시 생성하여 저장

            Path target = dir.resolve(fileName); // 저장할 실제위치

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING); //파일을 디스크에 복사
                          //업로드된 파일의 바이너리 스트림                  // 같은이름이 있을시 덮어쓰기
            return "/" + dir.toString().replace("\\", "/") + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private String getExtension(String filename) { //확장자 찾기
        if (filename == null) return "";

        int idx = filename.lastIndexOf("."); //마지막 . 찾아서 확장자 찾기

        return idx > 0 ? filename.substring(idx + 1) : "";
    }

}