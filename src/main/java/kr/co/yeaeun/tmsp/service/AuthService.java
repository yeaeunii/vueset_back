package kr.co.yeaeun.tmsp.service;

import jakarta.transaction.Transactional;
import kr.co.yeaeun.tmsp.model.Login.RefreshToken;
import kr.co.yeaeun.tmsp.model.Login.User;
import kr.co.yeaeun.tmsp.securiy.JwtProvider;
import kr.co.yeaeun.tmsp.service.Repository.MemberRepository;
import kr.co.yeaeun.tmsp.service.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //로그인
    @Transactional
    public Map<String, Object> login(User request) {

        User member = memberRepository.findByLoginIdAndUseYnTrue(request.getLoginId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 아이디"
                ));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "비밀번호 불일치"
            );
        }

        String accessToken = jwtProvider.createAccessToken(request);
        String refreshToken = jwtProvider.createRefreshToken(request);

        refreshTokenRepository.deleteByMemberId(member.getId());

        refreshTokenRepository.save(
                RefreshToken.of(
                        member.getId(),
                        refreshToken,
                        jwtProvider.getRefreshTokenExpiry()
                )
        );


        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "id", member.getId(),
                "username", member.getUsername(),
                "role",member.getRole()
        );
    }



    //회원가입
    public User register(User request) {


       User member = new User();

        // 아이디 중복 체크
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 존재하는 아이디입니다"
            );
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        member.setLoginId(request.getLoginId());
        member.setEmail(request.getEmail());
        member.setPassword(encodedPassword);
        member.setUsername(request.getUsername());

        return memberRepository.save(member);
}

    // access token
    public User getCurrentUser(String token) {

        String loginId = jwtProvider.getLoginIdFromAccessToken(token);

        return memberRepository.findByLoginIdAndUseYnTrue(loginId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자"
                ));
    }

    // refresh token → access token 재발급
    @Transactional
    public String refresh(String refreshToken) {

        // DB에서 refresh token 조회
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token"
                ));

        // JWT 만료 검증 (exp)
        try {
            jwtProvider.validateRefreshToken(refreshToken);

        } catch (Exception e) {
            // 만료되었으면 DB에서도 제거 = 로그아웃
            refreshTokenRepository.delete(saved);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "refresh token 만료"
            );
        }

        // 사용자 조회
        User member = memberRepository.findById(saved.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "사용자 없음"
                ));

        // 새 access token 발급
        return jwtProvider.createAccessToken(member);
    }


    // 로그아웃
    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}