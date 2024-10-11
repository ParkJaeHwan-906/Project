package hwannee.project.User.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 현재 로그인 한 사용자의 정보를 간단하게 보여준다.
public class UserInfo {
    private Integer user_idx;
    private String user_name;
    private String id;
    private String tel;

    @Builder
    public UserInfo(Integer user_idx, String user_name, String id, String tel){
        this.user_idx = user_idx;
        this.user_name =user_name;
        this.id = id;
        this.tel = tel;
    }
}
