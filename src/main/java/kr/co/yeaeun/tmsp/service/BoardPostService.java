package kr.co.yeaeun.tmsp.service;

import jakarta.transaction.Transactional;
import kr.co.yeaeun.tmsp.model.Board.BoardPost;
import kr.co.yeaeun.tmsp.model.Login.User;
import kr.co.yeaeun.tmsp.service.Repository.BoardPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final PasswordEncoder passwordEncoder;

    //게시글 조회
    public List<BoardPost> getBoardPosts() {
        return boardPostRepository.findByUseYnTrueOrderByIdDesc();
    }

    //게시글 작성
    public BoardPost writeBoardPost(BoardPost write, User loginUser) {

        BoardPost post = new BoardPost();

        post.setTitle(write.getTitle());
        post.setContent(write.getContent());

        if (loginUser != null) {
            // 로그인 글
            post.setWriterid(loginUser.getId());
            post.setWriter(loginUser.getUsername());
            post.setBoardPW(
                    passwordEncoder.encode(write.getBoardPW())
            );
        } else {
            // 익명 글
            post.setWriterid(1L);                 // 익명  id= 1로 고정
            post.setWriter(write.getWriter());
            post.setBoardPW(
                    passwordEncoder.encode(write.getBoardPW())
            );
        }

        return boardPostRepository.save(post);
    }


    //게시글 수정
    public BoardPost modifyBoardPost(Long postId, BoardPost write) {

        BoardPost post = boardPostRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")
                );

        // 삭제 확인
        if (!post.getUseYn()) {
            throw new ResponseStatusException(HttpStatus.GONE, "이미 삭제된 게시글입니다.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(write.getBoardPW(), post.getBoardPW())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");
        }

        post.setTitle(write.getTitle());
        post.setContent(write.getContent());

        return boardPostRepository.save(post);
    }



    @Transactional
    public void deleteBoardPost(Long id,  BoardPost password) {

        BoardPost post = boardPostRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")
                );

        // 삭제 확인
        if (!post.getUseYn()) {
            throw new ResponseStatusException(HttpStatus.GONE, "이미 삭제된 게시글입니다.");
        }

        if (!passwordEncoder.matches(password.getBoardPW().trim(), post.getBoardPW())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");
        }

        post.setUseYn(false);
    }


    //게시글 검색
    public List<BoardPost> search(String type, String keyword) {
        switch (type) {
            case "title":
                return boardPostRepository.findByUseYnTrueAndTitleContaining(keyword);
            case "content":
                return boardPostRepository.findByUseYnTrueAndContentContaining(keyword);
            case "writer":
                return boardPostRepository.findByUseYnTrueAndWriterContaining(keyword);
            default:
                return List.of();
        }
    }


    //비밀번호 검사
    public boolean checkPassword(Long postId, String wirtePassword) {

        BoardPost post = boardPostRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다.")
                );

        if (!post.getUseYn()) {
            throw new ResponseStatusException(HttpStatus.GONE, "이미 삭제된 게시글입니다.");
        }

        return passwordEncoder.matches(wirtePassword, post.getBoardPW());
    }




}
