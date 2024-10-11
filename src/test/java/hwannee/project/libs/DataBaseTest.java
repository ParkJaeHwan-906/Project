package hwannee.project.libs;

import hwannee.project.item.domain.Orders;
import hwannee.project.item.repository.OrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // @Order 어노테이션을 통한 순서 지정
class DataBaseTest {

    @Autowired
    private DataBase db;

    @Autowired
    private OrdersRepository ordersRepository;

    @DisplayName("query() : select 결과를 확인한다.")
    @Test
    void queySelect() {
        // given : 데이터를 삽입한다.
        for(int i=1; i<=3; i++){
            ordersRepository.save(Orders.builder().userIdx(i).build());
        }

        // when : select 문을 사용해 데이터를 추출한다.
        String query = "SELECT * FROM orders";
        Map<String, Object> result = db.query(query, Arrays.asList());

        System.out.println(result.get("result"));

        // then : 결과값을 확인한다.
        assertThat(result).isNotEmpty();
    }

    @DisplayName("query() : UPDATE 결과를 확인한다.")
    @Test
    void queyUpdate() {
        // given : 데이터를 삽입한다.
        for(int i=1; i<=3; i++){
            ordersRepository.save(Orders.builder().userIdx(i).build());
        }

        // when : select 문을 사용해 데이터를 추출한다.
        String query = "UPDATE orders SET step = 10";
        Map<String, Object> result = db.query(query, Arrays.asList());

        System.out.println(result.get("result"));
        System.out.println(db.query("SELECT * FROM orders", Arrays.asList()).get("result"));
        // then : 결과값을 확인한다.
        assertThat(result).isNotEmpty();
    }

/*  // patternCheck -> private 로 변경하여 테스트 주석처리
    @DisplayName("patternCheck() : 입력된 query 에 따라 SQL 을 구분한다. (SELECT)")
    @Test
    void selectPatternCheck() {
        // given : 쿼리를 작성한다.
        String query = "SELECT * FROM orders";

        // when : 패턴을 분석한다.
        // 기댓값 false
        boolean check = db.patternCheck(query);

        // then : false 인지 확인한다.
        assertThat(check).isFalse();
    }

    @DisplayName("patternCheck() : 입력된 query 에 따라 SQL 을 구분한다. (UPDATE)")
    @Test
    void updatePatternCheck() {
        // given : 쿼리를 작성한다.
        String query = "UPDATE orders SET";

        // when : 패턴을 분석한다.
        // 기댓값 true
        boolean check = db.patternCheck(query);

        // then : true 인지 확인한다.
        assertThat(check).isTrue();
    }

    @DisplayName("patternCheck() : 입력된 query 에 따라 SQL 을 구분한다. (INSERT)")
    @Test
    void insertPatternCheck() {
        // given : 쿼리를 작성한다.
        String query = "INSERT INTO orders";

        // when : 패턴을 분석한다.
        // 기댓값 true
        boolean check = db.patternCheck(query);

        // then : true 인지 확인한다.
        assertThat(check).isTrue();
    }

    @DisplayName("patternCheck() : 입력된 query 에 따라 SQL 을 구분한다. (DELETE)")
    @Test
    void deletePatternCheck() {
        // given : 쿼리를 작성한다.
        String query = "DELETE orders";

        // when : 패턴을 분석한다.
        // 기댓값 true
        boolean check = db.patternCheck(query);

        // then : true 인지 확인한다.
        assertThat(check).isTrue();
    }
 */
}