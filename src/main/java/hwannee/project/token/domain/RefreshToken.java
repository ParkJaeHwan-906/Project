package hwannee.project.token.domain;

import hwannee.project.User.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {
    /*
    <refreshToken>
    idx         |   INTEGER         | NOT NULL  |   PK  |   일련번호, 기본키
    userIdx     |   INTEGER         | NOT NULL  |   FK  |   사용자 인덱스 ( users 테이블 참조 )
    id          |   VARCHAR(255)    | NOT NULL  |       |   아이디
    name        |   VARCHAR(255)    | NOT NULL  |       |   사용자 이름
    refreshtoken|   VARCHAR(255)    | NOT NULL  |       |   리프래쉬 토큰
    expired_at  |   VARCHAR(500)    |           |       |   리프레쉬 토큰 만료 일자 ( 로그아웃 시 삭제 )
    created_at  |   DATETIME        | NOT NULL  |       |   생성일자
    updated_at  |   DATETIME        | NOT NULL  |       |   수정 일자
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false)
    private Integer idx;

//    // FK - users 테이블의 idx 컬럼 참조 (User 엔티티 사용)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userIdx", nullable = false)  // 외래키 설정
//    private User user;  // Integer 대신 User 타입으로 수정
    // FK - users 테이블의 idx 컬럼 참조 (User 엔티티 사용)

    @Column(name = "user_idx", nullable = false)
    private Integer userIdx;  // Integer 대신 User 타입으로 수정

    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "refreshtoken", nullable = false, updatable = false)
    private String refreshToken;

    @Column(name = "expired_at")
    private LocalDateTime expired_at;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

//    public RefreshToken(User user, String name, String id, String refresh_token) {
//        this.user = user;  // userIdx 대신 User 객체로 참조
//        this.name = name;
//        this.id = id;
//        this.refresh_token = refresh_token;
//    }
    public RefreshToken(Integer userIdx, String name, String id, String refreshToken) {
        this.userIdx = userIdx;  // userIdx 대신 User 객체로 참조
        this.name = name;
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken){
        this.refreshToken = newRefreshToken;
        return this;
    }

    public RefreshToken expiredRefreshToken(){
        this.expired_at = LocalDateTime.now();
        return this;
    }
}
