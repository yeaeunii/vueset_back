package kr.co.yeaeun.tmsp.service;

import jakarta.transaction.Transactional;
import kr.co.yeaeun.tmsp.model.BoardPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.config.HateoasAwareSpringDataWebConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardPostService {

    private final BoardPostRepository boardPostRepository;
//    private final PasswordEncoder passwordEncoder;
    //게시글 조회
    public List<BoardPost> getBoardPosts() {
        return boardPostRepository.findByUseYnTrueOrderByIdDesc();
    }

    //게시글 작성
    public BoardPost writeBoardPost(BoardPost write) {

        BoardPost post = new BoardPost();

        post.setWriterid(write.getWriterid());
        post.setWriter(write.getWriter());
        post.setTitle(write.getTitle());
        post.setContent(write.getContent());
        post.setBoardPW(write.getBoardPW());
//        post.setBoardPW(passwordEncoder.encode(write.getBoardPW()));

        return boardPostRepository.save(post);
    }


    //게시글 수정
    public BoardPost modifyBoardPost(Long postId, BoardPost write) {

        BoardPost post = boardPostRepository.findById(postId)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."   ));


        if (!post.getUseYn()) {
            throw new ResponseStatusException(HttpStatus.GONE, "이미 삭제된 게시글입니다.");

        }


        post.setTitle(write.getTitle());
        post.setContent(write.getContent());


        return boardPostRepository.save(post);
    }

    //게시글 삭제
    @Transactional
    public void deleteBoardPost(Long id) {
        BoardPost post = boardPostRepository.findById(id)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."   ));


        if (!post.getUseYn()) {
            throw new ResponseStatusException(HttpStatus.GONE, "이미 삭제된 게시글입니다.");

        }

        post.setUseYn(false);
    }


    //게시글 검색
    public List<BoardPost> search(String type, String keyword) {
        switch (type) {
            case "title":
                return boardPostRepository.findByTitleContaining(keyword);
            case "content":
                return boardPostRepository.findByContentContaining(keyword);
            case "writer":
                return boardPostRepository.findByWriterContaining(keyword);
            default:
                return List.of();
        }
    }





}
