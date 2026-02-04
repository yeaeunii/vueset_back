package kr.co.yeaeun.tmsp.model.News;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "newsletter_image")
public class NewsletterImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
                      //<->EAGER (즉시 로딩)
    @ManyToOne(fetch = FetchType.LAZY) // 1:N관계 / NewsletterImage를 조회할 때 연결된 Newsletter는 지금은 안 가져오고, img.getNewsletter()를 호출하는 순간에 가져옴
    @JoinColumn(name = "newsletter_id", nullable = false) // pk컬럼과 자동 참조 매핑
    private Newsletter newsletter;

    @Column(name = "image_url",nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "originalnm", length = 255)
    private String originalNM;

    @Column(name = "sort", nullable = false)
    private int sort;

    @Column(name = "thumbnailyn")
    private Boolean thumbnailYN = false;

    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}