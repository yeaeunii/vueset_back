package kr.co.yeaeun.tmsp.service.Repository;

import kr.co.yeaeun.tmsp.model.Login.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    void deleteByMemberId(Long memberId);

    Optional<RefreshToken> findByToken(String token);
}