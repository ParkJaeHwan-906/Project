package hwannee.project.item.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "item_image")
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false, updatable = false)
    private Integer idx;

    @Column(name = "item_idx", nullable = false, updatable = false)
    private Integer itemIdx;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "seq", nullable = false)
    private Integer seq;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ItemImage(Integer itemIdx, String itemUrl, Integer seq){
        this.itemIdx = itemIdx;
        this.imageUrl = itemUrl;
        this.seq = seq;
    }

}
