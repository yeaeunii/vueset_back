package kr.co.yeaeun.tmsp.web;

import kr.co.yeaeun.tmsp.model.BoardPost;
import kr.co.yeaeun.tmsp.service.BoardPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BoardPost> writePost(@RequestBody BoardPost post) {
        BoardPost savedPost = boardPostService.writeBoardPost(post);
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
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boardPostService.deleteBoardPost(id);
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



}
