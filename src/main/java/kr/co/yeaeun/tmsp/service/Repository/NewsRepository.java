package kr.co.yeaeun.tmsp.service.Repository;

import kr.co.yeaeun.tmsp.model.News.DTO.NewsletterList;
import kr.co.yeaeun.tmsp.model.News.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<Newsletter, Long> {

    @Query("""
        SELECT new kr.co.yeaeun.tmsp.model.News.DTO.NewsletterList(
            n.id,
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
}
