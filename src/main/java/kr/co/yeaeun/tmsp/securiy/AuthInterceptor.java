package kr.co.yeaeun.tmsp.securiy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.yeaeun.tmsp.model.Login.User;
import kr.co.yeaeun.tmsp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String token = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // 익명 요청
        if (token == null) {
            return true;
        }

        try {
            // 토큰이 있을 때만 검증
            User user = authService.getCurrentUser(token);
            request.setAttribute("loginUser", user);
            return true;

        } catch (Exception e) {
            // 401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }
}

