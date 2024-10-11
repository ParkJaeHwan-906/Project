package hwannee.project.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemListResponse {
    private Integer item_idx;
    private String item_name;
    private Integer item_price;
    private String category;
    private String explanation;
    private Integer user_idx;   // 판매자 인덱스 (item - user_idx)
    private String user_name;  // 판매자 이름 (users - name)
    private String user_tel;    // 판매자 전화번호 (users - tel)
    private String user_address; // 판매자 주소 (users - address)
    private String user_detail; // 판매자 상세주소 (users - detail)
    private List<String> itemImages;
//    private List<?> reviews;
}
