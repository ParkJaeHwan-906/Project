package hwannee.project.item.service;

import hwannee.project.item.domain.Orders;
import hwannee.project.item.repository.OrdersRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLDataException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // @Order 어노테이션을 통한 순서 지정
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrdersRepository ordersRepository;

    @DisplayName("orderCancle() : 사용자의 주문 정보를 취소한다.(성공)")
    @Test
    @Order(3)
    void orderCancle() {
        // given : 주문 목록을 idx 를 통해 불러온다.
        ordersRepository.save(Orders.builder().userIdx(4).build());
        Orders orders = ordersRepository.findByIdx(1)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 주문이 존재하지 않습니다."));

        // when : 해당 idx 의 step 값을 10으로 변경한다.
        if(orders.getUserIdx() != 4) throw new IllegalArgumentException("유저 정보가 잘못되었습니다.");
        orders.setStep(10);
        ordersRepository.save(orders);

        // then : 저장이 되어있는지 검증한다.
        Integer step = ordersRepository.findByIdx(1)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 주문이 존재하지 않습니다.")).getStep();

        assertThat(step).isEqualTo(10);
    }

    @DisplayName("orderConfirm() : 구매 확정을 한다. (실패)")
    @Test
    @Order(1)
    void orderConfirmFail(){
        // given : 주문 정보를 만들고, order_idx 를 통해 구매 정보를 불러온다.
        ordersRepository.save(Orders.builder().userIdx(1).build());

        // when : 주문 정보의 step 값을 변경(3) 후 저장한다.
        orderService.orderConfirm(1,1);

        // then : step 의 값이 1로 유지되었는지 확인한다.
        Integer step = ordersRepository.findByIdx(1)
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 주문 정보가 존재하지 않습니다.")).getStep();
        assertThat(step).isEqualTo(1);
    }

    @DisplayName("orderConfirm() : 구매 확정을 한다. (성공)")
    @Test
    @Order(2)
    void orderConfirmSuccess(){
        // given : 주문 정보를 만들고, order_idx 를 통해 구매 정보를 불러온다.
        ordersRepository.save(Orders.builder().userIdx(1).build());
        Orders orders = ordersRepository.findByIdx(1)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 주문 정보가 존재하지 않습니다."));

        // when : 주문 정보의 step 값을 변경(3) 후 저장한다.
        orders.setStep(2);
        ordersRepository.save(orders);
        orderService.orderConfirm(1,1);

        // then : step 의 값이 1로 유지되었는지 확인한다.
        Integer step = ordersRepository.findByIdx(1)
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 주문 정보가 존재하지 않습니다.")).getStep();
        assertThat(step).isEqualTo(3);
    }
}