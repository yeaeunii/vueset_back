package kr.co.yeaeun.tmsp.model.Manual;

import kr.co.yeaeun.tmsp.model.Manual.DTO.ScreenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ManualAnalyzeResponse {

    private List<ScreenDto> screens;
}