package hwannee.project.item.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewModifyRequest {
    private Integer review_idx;
    private Integer score;
    private String review;
}
