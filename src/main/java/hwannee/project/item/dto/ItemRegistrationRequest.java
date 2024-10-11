package hwannee.project.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRegistrationRequest {
    private String item_name;
    private Integer user_idx;
    private Integer category;
    private Integer price;
    private String explanation;
}
