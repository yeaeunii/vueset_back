package kr.co.yeaeun.tmsp.web;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.yeaeun.tmsp.model.Login.User;
import kr.co.yeaeun.tmsp.model.News.DTO.NewsletterList;
import kr.co.yeaeun.tmsp.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsletterService;

    //뉴스레터 조회
    @GetMapping("/list")
    public ResponseEntity<List<NewsletterList>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(
                newsletterService.getNewsletterList(category, keyword)
        );
    }



    // 뉴스레터 작성
    @PostMapping("/write")
    public ResponseEntity<?> createNewsletter(
            HttpServletRequest request,
            @RequestParam String category,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) List<MultipartFile> images
    ) {
        User loginUser = (User) request.getAttribute("loginUser");

        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        newsletterService.create(
                loginUser.getId(),
                loginUser.getUsername(),
                category,
                title,
                description,
                images
        );

        return ResponseEntity.ok().build();
    }





}
