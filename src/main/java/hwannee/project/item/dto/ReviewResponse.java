package hwannee.project.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private String user_name;
    private String item_name;
    private Integer ea;
    private LocalDateTime order_date;
    private Integer score;
    private String review;
}
