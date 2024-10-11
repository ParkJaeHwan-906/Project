//package hwannee.project.libs;
//
//import hwannee.project.User.domain.User;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.Duration;
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class AuthTest {
//    @Autowired
//    private Auth auth;
//
//    // 테스트에선 application.yml 값 못불러옴
////    @DisplayName("generateToken() : 토큰을 생성한다.")
////    @Test
////    void generateToken() {
////        //given : user 를 생성한다.
////        User user = User.builder()
////                .name("테스트")
////                .birth(LocalDate.parse("2024-09-11"))
////                .tel("01012345678")
////                .address("서울시")
////                .detail("1층")
////                .id("test")
////                .passWord("1234")
////                .build();
////        Duration expiredAt = Duration.ofHours(2);
////
////        // when : 토큰을 생성한다.
////        String token = auth.generateToken(user, expiredAt);
////
////
////        //then : 토큰을 검증한다.
////        assertThat(auth.vaildToken(token)).isTrue();
////    };
//
//
//}