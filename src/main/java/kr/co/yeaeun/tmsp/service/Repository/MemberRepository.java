package kr.co.yeaeun.tmsp.service.Repository;

import kr.co.yeaeun.tmsp.model.Login.RefreshToken;
import kr.co.yeaeun.tmsp.model.Login.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<User, Long> {

    //로그인
    Optional<User> findByLoginIdAndUseYnTrue(String loginId);


    //회원가입
    //ID 중복체크
    boolean existsByLoginId(String loginId);



}
