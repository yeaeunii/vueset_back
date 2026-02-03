package kr.co.yeaeun.tmsp.web;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.yeaeun.tmsp.model.Board.BoardPost;
import kr.co.yeaeun.tmsp.model.Login.User;
import kr.co.yeaeun.tmsp.service.BoardPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardPostController {

    private final BoardPostService boardPostService;

   //게시글 조회
    @GetMapping("/postselect")
    public List<BoardPost> getPosts() {
        return boardPostService.getBoardPosts();

    }

    //게시글 작성
    @PostMapping("/postwrite")
    public ResponseEntity<BoardPost> writePost(
            HttpServletRequest request,
            @RequestBody BoardPost post
    ) {
        User loginUser = (User) request.getAttribute("loginUser");

        System.out.println("loginUser = " + loginUser);


        // 익명
        if (loginUser == null && post.getWriterid() != null && post.getWriterid() != 1L) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        BoardPost savedPost = boardPostService.writeBoardPost(post, loginUser);
        return ResponseEntity.ok(savedPost);
    }


    //게시글 수정
    @PutMapping("/postmodify/{id}")
    public ResponseEntity<BoardPost> modifyPost(
            @PathVariable Long id,
            @RequestBody BoardPost post
    ) {

        return ResponseEntity.ok(boardPostService.modifyBoardPost(id, post));
    }

    //게시글 삭제
    @DeleteMapping("/postdelete/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestBody BoardPost password
    ) {
        boardPostService.deleteBoardPost(id,password);

        return ResponseEntity.noContent().build();
    }

    //게시글 검색
    @GetMapping("/postsearch")
    public List<BoardPost> searchPosts(
            @RequestParam String type,
            @RequestParam String keyword
    ) {
        return boardPostService.search(type, keyword);
    }


    //비밀번호 검사
    @PostMapping("/post/{id}/check-password")
    public ResponseEntity<Boolean> checkPassword(
            @PathVariable Long id,
            @RequestBody BoardPost password
    ) {
        return ResponseEntity.ok(
                boardPostService.checkPassword(id, password.getBoardPW())
        );
    }


}
