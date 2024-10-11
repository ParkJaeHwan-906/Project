//package hwannee.project.libs;
//
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class EncryptTest {
//
//    @Autowired
//    private Encrypt encrypt;
//
//    @DisplayName("cryptoPassWord() : 입력 받은 비밀번호를 암호화한다. (해싱)")
//    @Test
//    void cryptoPassWord() {
//        // given : 비밀번호를 입력받는다.
//        String passWord = "hello world";
//
//        // when : 암호화 한다.
//        String hasedPassWord = encrypt.cryptoPassWord(passWord);
//
//        // then : 입력한 암호화 암호화 된 암호가 같은지 확인한다.
//        assertThat(encrypt.checkPassWord(passWord, hasedPassWord)).isTrue();
//    }
//    // 테스트에선 application.yml 값 못불러옴
//    @DisplayName("cryptoSync() : 양방향 암호화를 하고 이를 복원한다.")
//    @Test
//    void cryptoSync(){
//        // given : 비밀번호를 입력받는다.
//        String passWord = "hello world";
//
//        // when : 양방향 암호화 한다.
//        String cryptoPassWord = encrypt.cryptoSync(passWord);
//        System.out.println("앙방향 암호화 : " + cryptoPassWord);
//
//        // then : 복호화 이후 같은지 검증한다.
//        assertThat(encrypt.decryptoSync(cryptoPassWord)).isEqualTo(passWord);
//
//    }
//}