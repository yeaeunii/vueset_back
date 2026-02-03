package kr.co.yeaeun.tmsp.securiy;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.co.yeaeun.tmsp.model.Login.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expire-ms}")
    private long accessTokenExpireMs;

    @Value("${jwt.refresh-token-expire-ms}")
    private long refreshTokenExpireMs ;


    //accesstoken
    public String createAccessToken(User member) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpireMs);


        SecretKey key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );


        return Jwts.builder()
                .setSubject(member.getLoginId())
                .claim("id", member.getId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //refreshtoken
    public String createRefreshToken(User member) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpireMs);

        SecretKey key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );


        return Jwts.builder()
                .setSubject(member.getLoginId())
                .claim("id", member.getId())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //access token에서 loginId 추출
    public String getLoginIdFromAccessToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // loginId
    }

    // DB에 저장할 refresh 만료 시각
    public LocalDateTime getRefreshTokenExpiry() {
        return LocalDateTime.now()
                .plusSeconds(refreshTokenExpireMs  / 1000);
    }


    // refresh token 검증
    public void validateRefreshToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);  // 만료되면 예외로 보내기
    }


}
