package kr.co.yeaeun.tmsp.service.Repository.News;

import kr.co.yeaeun.tmsp.model.News.DTO.NewsletterList;
import kr.co.yeaeun.tmsp.model.News.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<Newsletter, Long> {


    //이미지 리스트조회
    @Query("""
        SELECT new kr.co.yeaeun.tmsp.model.News.DTO.NewsletterList(
            n.id,
            n.username,
            n.category,
            n.title,
            n.description,
            n.createdAt,
            i.imageUrl
        )
        FROM Newsletter n
        LEFT JOIN NewsletterImage i
               ON i.newsletter = n
              AND i.thumbnailYN = true
        WHERE (:category IS NULL OR n.category = :category)
          AND (
                :keyword IS NULL
                OR n.title LIKE %:keyword%
                OR n.description LIKE %:keyword%
          )
        ORDER BY n.createdAt DESC
    """)
    List<NewsletterList> findNewsletterList(
            @Param("category") String category,
            @Param("keyword") String keyword
    );


    //이미지 상세조회
//    @Query("""
//        SELECT n
//        FROM Newsletter n
//        LEFT JOIN FETCH n.images
//        WHERE n.id = :id
//    """)
//    Optional<Newsletter> findDetailById(Long id);
//



}
