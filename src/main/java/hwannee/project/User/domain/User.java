package hwannee.project.User.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    /*
    <users>
    idx         |   INTEGER         | NOT NULL  |   PK  |   일련번호, 기본키
    name        |   VARCHAR(255)    | NOT NULL  |       |   사용자 이름
    birth       |   DATE            | NOT NULL  |       |   사용자 생년월일
    tel         |   VARCHAR(255)    | NOT NULL  |       |   사용자 전화번호
    address     |   VARCHAR(255)    | NOT NULL  |       |   사용자 주소
    detail      |   VARCHAR(255)    | NOT NULL  |       |   사용자 상세주소
    id          |   VARCHAR(255)    | NOT NULL  |       |   아이디
    passWord    |   VARCHAR(255)    | NOT NULL  |       |   패스워드(암호화하여 저장)
    created_at  |   DATETIME        | NOT NULL  |       |   생성일자
    updated_at  |   DATETIME        | NOT NULL  |       |   수정 일자
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Integer idx;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Column(name = "tel", nullable = false, unique = true)
    private String tel;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail", nullable = false)
    private String detail;

    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "password", nullable = false)
    private String passWord;

    // ⚠️ 문자열로 처리할 때 작은 따옴표로 감싸야함
    @Column(name = "role")
    @ColumnDefault("'ROLE_USER'")
    /*
        권한 종류
        1. ROLE_USER : 일반 사용자
        2. ROLE_ADMIN : 관리 권한
        3. ROLE_SUPER_ADMIN : 최상위 관리자 권한
        4. ROLE_GUEST : 게스트 권한
         */
    private String role = "ROLE_USER";

    @Column(name = "ban")
    private Integer ban;
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Builder
    public User(String name, LocalDate birth, String tel, String address, String detail, String id, String passWord, String role){
        this.name = name;
        this.birth = birth;
        this.tel = tel;
        this.address = address;
        this.detail = detail;
        this.id = id;
        this.passWord = passWord;
        this.role = (role == null || role.isEmpty()) ? this.role : role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return passWord;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
