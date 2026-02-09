package kr.co.yeaeun.tmsp.service;


import kr.co.yeaeun.tmsp.model.News.DTO.NewsletterList;
import kr.co.yeaeun.tmsp.model.News.Newsletter;
import kr.co.yeaeun.tmsp.model.News.NewsletterImage;
import kr.co.yeaeun.tmsp.service.Repository.News.NewsImageRepository;
import kr.co.yeaeun.tmsp.service.Repository.News.NewsRepository;
import kr.co.yeaeun.tmsp.service.Storage.LocalFileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {


    private final NewsRepository newsRepository;
    private final NewsImageRepository newsImageRepository;
    private final LocalFileStorage fileStorage;

    //  뉴스레터 리스트 조회
    public List<NewsletterList> getNewsletterList(
            String category,
            String keyword
    ) {
        return newsRepository.findNewsletterList(
                category,
                keyword
        );
    }

//  뉴스레터 작성
public void create(
        Long memberId,
        String username,
        String category,
        String title,
        String description,
        List<MultipartFile> images
) {
    // 내용 저장
    Newsletter newsletter = new Newsletter();
    newsletter.setMemberId(memberId);
    newsletter.setUsername(username);
    newsletter.setCategory(category);
    newsletter.setTitle(title);
    newsletter.setDescription(description);

    newsRepository.save(newsletter);

    //  이미지 저장
    if (images == null || images.isEmpty()) return;

    int order = 0;

    for (MultipartFile image : images) {
        if (image.isEmpty()) continue;

        String imagePath = fileStorage.save(image);

        NewsletterImage img = new NewsletterImage();
        img.setNewsletter(newsletter);
        img.setImageUrl(imagePath);
        img.setOriginalNM(image.getOriginalFilename());
        img.setSort(order);
        img.setThumbnailYN(order == 0); // 첫 이미지 = 썸네일

        newsImageRepository.save(img);
        order++;
    }
}

//    public Newsletter getNewsletterDetail(Long id) {
//        return newsRepository.findDetailById(id)
//                .orElseThrow(() ->
//                        new ResponseStatusException(
//                                HttpStatus.NOT_FOUND,
//                                "뉴스레터를 찾을 수 없습니다."
//                        )
//                );
//    }




}

