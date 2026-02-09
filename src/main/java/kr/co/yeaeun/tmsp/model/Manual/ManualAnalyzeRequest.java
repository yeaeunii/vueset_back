package kr.co.yeaeun.tmsp.model.Manual;

import kr.co.yeaeun.tmsp.model.Manual.DTO.ManualOptionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManualAnalyzeRequest {

    private String url;
    private ManualOptionDto options;
}