package hwannee.project.item.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
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
@Table(name = "order_log")
public class OrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Integer idx;

    @Column(name = "orders_idx", nullable = false, updatable = false)
    private Integer ordersIdx;

    @Column(name = "item_idx", nullable = false, updatable = false)
    private Integer itemIdx;

    @Column(name = "ea", nullable = false, updatable = false)
    private Integer ea;

    // 기본값 지정되어 있음 (0 : 작성 전)
    @Column(name = "review")
    private Integer review;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public OrderLog(Integer ordersIdx, Integer itemIdx, Integer ea){
        this.ordersIdx = ordersIdx;
        // 주문 시 리뷰 작성 전 처리
        this.review = 0;
        this.itemIdx = itemIdx;
        this.ea = ea;
    }
}
