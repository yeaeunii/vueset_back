package kr.co.yeaeun.tmsp.model.Manual.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElementDto {

    private int index;              // 화면 내 번호
    private String label;           // 버튼 이름
    private String description;     // 기능 설명
    private String confidence;      // high / medium / low
}