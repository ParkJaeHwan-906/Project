package hwannee.project.User.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddUserRequest {
    private String name;
    private LocalDate birth;
    private String tel;
    private String address;
    private String detail;
    private String id;
    private String passWord;
    private String role;
}
