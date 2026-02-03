package kr.co.yeaeun.tmsp.service.Repository;

import kr.co.yeaeun.tmsp.model.Board.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    //게시글 조회
    List<BoardPost> findByUseYnTrueOrderByIdDesc();

    //게시글 검색
    List<BoardPost> findByUseYnTrueAndTitleContaining(String title);
    List<BoardPost> findByUseYnTrueAndContentContaining(String content);
    List<BoardPost> findByUseYnTrueAndWriterContaining(String writer);

}
