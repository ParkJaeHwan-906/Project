package hwannee.project.token.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenRequestTest {
    @Autowired
    TokenRequest tokenRequest;

    @DisplayName("toObject() : 직렬화 된 문자열을 객체로 반환한다.")
    @Test
    void toObject() {
        // given : 객체를 생성하고 직렬화한다.
        TokenRequest request = TokenRequest.builder()
                .idx(1)
                .name("테스트")
                .id("test")
                .build();

        String serialization = request.toString();

        // when : 직렬화 된 문자열을 역직렬화한다.
        TokenRequest response = TokenRequest.toObject(serialization);
        System.out.println(response.getIdx());
        System.out.println(response.getName());
        System.out.println(response.getId());

        // then : request 객체와 response 객체가 같은지 확인한다.
        assertThat(request).isEqualTo(response);

    }
}