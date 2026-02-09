package kr.co.yeaeun.tmsp.web;

import kr.co.yeaeun.tmsp.service.AuthService;

import kr.co.yeaeun.tmsp.model.Login.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService memberService;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User request) {

        Map<String, Object> token = memberService.login(request);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", (String) token.get("accessToken"))
                .httpOnly(true)
                .secure(false) // false - http도 전송 / true - https에서만 전송
                .path("/") //쿠키가 어떤 경로에서 유효한지
                .sameSite("Lax") //Strict → 같은 사이트 요청만 허용 / Lax → 일반적인 GET/POST 허용 /None → 전부 허용 (CSRF 위험, Secure 필수)
                .path("/")
                .maxAge(Duration.ofMinutes(10))
                .build();


        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token",  (String) token.get("refreshToken"))
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth/refresh") //재발급 api에서만 쿠키 유효
                .maxAge(Duration.ofDays(30))
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "username", token.get("username"),
                        "role", token.get("role"),
                        "id", token.get("id")
                ));
    }


//회원가입
    @PostMapping("/register")
    public User register(
            @RequestBody User request
    ) {
        return memberService.register(request);

    }

    //새로고침/페이지첫로드/header초기화
    @GetMapping("/me")
    public ResponseEntity<?> me(
            @CookieValue(name = "access_token", required = false) String token
    ) {
        // 토큰확인
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 토큰 만료????
        }

        // 토큰 검증 + 사용자 조회
        User member = memberService.getCurrentUser(token);

        // 화면에 필요한 정보만 응답
        return ResponseEntity.ok(
                Map.of(
                        "username", member.getUsername(),
                        "role", member.getRole()
                )
        );
    }

    //refresh토큰 만료 시
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken

    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = memberService.refresh(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(10))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                 .body(Map.of("result", "ok"));
    }


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "access_token", required = false) String token
    ) {
        if (token != null) {
            User member = memberService.getCurrentUser(token);
            memberService.logout(member.getId());
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        ResponseCookie.from("access_token", "")
                                .path("/")
                                .maxAge(0)
                                .build().toString())
                .header(HttpHeaders.SET_COOKIE,
                        ResponseCookie.from("refresh_token", "")
                                .path("/api/auth/refresh")
                                .maxAge(0)
                                .build().toString())
                .build();
    }



}
