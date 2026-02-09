package kr.co.yeaeun.tmsp.model.Manual.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScreenDto {

    private int state;          // 화면 단계 번호
    private String image;       // 스크린샷 경로
    private List<ElementDto> elements;
}