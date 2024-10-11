package hwannee.project.item.dto;

import hwannee.project.item.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CategoryResponse {
    private String message;
    private List<Map<Integer, String>> categoryList;
}
