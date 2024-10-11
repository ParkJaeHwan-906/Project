package hwannee.project.item.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "item")
@NoArgsConstructor
@Getter
@Setter
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Integer idx;

    @Column(name = "user_idx", updatable = false, nullable = false)
    private Integer user_idx;

    @Column(name = "item_name", nullable = false)
    private String item_name;

    // 별도 테이블 구성
    @Column(name = "category", nullable = false)
    private Integer category;

    // 상품 옵션는 idx 와 조인하여 따로 테이블 관리 예정

    // 이미지는 idx 와 조인하여 따로 테이블 관리 예정

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "explanation")
    private String explanation; // 상품 상세 설명

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Builder
    public Item(String item_name, Integer user_idx, Integer category, Integer price, String explanation){
        this.item_name = item_name;
        this.user_idx = user_idx;
        this.category = category;
        this.price = price;
        this.explanation = explanation;
    }
}
