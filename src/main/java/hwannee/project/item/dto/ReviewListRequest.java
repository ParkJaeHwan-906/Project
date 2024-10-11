package hwannee.project.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewListRequest {
    private Integer item_idx;
    private String item_name;
    private Integer ea;
    private Integer orders_idx;
    private LocalDateTime order_date;
    private Integer review_idx;
    private Integer order_idx;
    private Integer user_idx;
    private String user_name;
    private Integer score;
    private String review;
}
