package hwannee.project.User.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseUser {
    private String name;
    private String id;
    private String passWord;
    private String role;

    @Builder
    public ResponseUser(String name, String id, String passWord, String role){
        this.name = name;
        this.id = id;
        this.passWord = passWord;
        this.role = role;
    }
}
