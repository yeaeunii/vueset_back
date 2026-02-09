package kr.co.yeaeun.tmsp.model.News.DTO;

import java.time.LocalDateTime;

public record NewsletterList(
        Long id,
        String writer,
        String category,
        String title,
        String description,
        LocalDateTime createdAt,
        String image   // 대표 이미지 URL
){}
