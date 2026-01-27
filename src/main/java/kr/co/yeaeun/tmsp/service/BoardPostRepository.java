package kr.co.yeaeun.tmsp.service;

import kr.co.yeaeun.tmsp.model.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    //게시글 조회
    List<BoardPost> findByUseYnTrueOrderByIdDesc();

    //게시글 검색
    List<BoardPost> findByTitleContaining(String title);
    List<BoardPost> findByContentContaining(String content);
    List<BoardPost> findByWriterContaining(String writer);

}
