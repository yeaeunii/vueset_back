package kr.co.yeaeun.tmsp.enums;

public enum ManualProgressCode {

    INIT,           // 요청 시작
    PAGE_LOAD,      // 페이지 로딩
    DOM_ANALYZE,    // DOM 분석
    SCREEN_CAPTURE, // 화면 캡쳐
    DESCRIPTION,    // 설명 생성
    DONE,           // 완료
    ERROR           // 오류
}