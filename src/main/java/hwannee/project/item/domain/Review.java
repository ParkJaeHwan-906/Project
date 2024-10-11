package hwannee.project.item.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "item_review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Integer idx;

    @Column(name = "order_idx", updatable = false, nullable = false)
    private Integer orderIdx;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "review", nullable = false)
    private String review;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    @Builder
    public Review(Integer order_idx, Integer score, String review){
        this.orderIdx = order_idx;
        this.score = score;
        this.review = review;
    }
}
