package kr.co.yeaeun.tmsp.service.Repository.News;

import kr.co.yeaeun.tmsp.model.News.NewsletterImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsImageRepository
        extends JpaRepository<NewsletterImage, Long> {

    //이미지 조회
    Optional<NewsletterImage> findByNewsletterIdAndThumbnailYNTrue(Long newsletterId);

}