package hwannee.project.item.service;

import hwannee.project.User.domain.User;
import hwannee.project.User.repository.UserRepository;
import hwannee.project.item.domain.OrderLog;
import hwannee.project.item.domain.Orders;
import hwannee.project.item.dto.OrderListResponse;
import hwannee.project.item.dto.OrderRequest;
import hwannee.project.item.repository.OrderLogRepository;
import hwannee.project.item.repository.OrdersRepository;
import hwannee.project.libs.DataBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final OrderLogRepository orderLogRepository;
    private final UserRepository userRepository;
    private final DataBase db;

    // 주문하기
    // 1. orders 테이블 내에 주문 정보를 저장한다.
    public int order(Integer user_idx){
//        orders 테이블 내에 주문 정보를 저장 한 뒤, 생성된 주문 번호를 가져온다.
        return ordersRepository.save(Orders.builder()
                .userIdx(user_idx).build()).getIdx();
    }

    // 2. order_log 테이블 내 주문의 상세 정보를 저장한다.
    public boolean orderDetail(Integer orders_idx, List<OrderRequest> orderList){
        try{
            orderList.stream().forEach((e) -> {
                orderLogRepository.save(OrderLog.builder()
                        .ordersIdx(orders_idx)
                        .itemIdx(e.getItme_idx())
                        .ea(e.getEa()).build());
            });
            return true;
        } catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    // 주문 취소하기 orders 테이블의 step 값을 10으로 변경
    // ✅ 테스트 완료 (OrderServiceTest - orderCancle())
    public Boolean orderCancle(Integer ordersIdx, Integer user_idx){
        try{
            // orders 의 idx로 해당 주문 정보를 가져온다.
            Orders orders = ordersRepository.findByIdx(ordersIdx)
                    .orElseThrow(() -> new SQLDataException("해당하는 주문 목록이 없습니다."));

            // 요청하는 user 의 주문이 맞는지 확인한다.
            if(user_idx != orders.getUserIdx()) throw new IllegalArgumentException("유저 정보가 잘못되었습니다.");

            // step 값을 변경한다.
            if(orders.getStep() != 10){
                orders.setStep(10);
                ordersRepository.save(orders);
            } else{
                throw new IllegalArgumentException("이미 취소되었습니다.");
            }

            return true;
        } catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    // 구매 확정하기 orders 테이블의 step 값을 3으로 변경
    public Boolean orderConfirm(Integer orders_idx, Integer user_idx){
        try{
            // orders 테이블에서 해당하는 idx의 구매 정보를 가져온다.
            Orders orders = ordersRepository.findByIdx(orders_idx)
                    .orElseThrow(() -> new SQLDataException("해당하는 주문 목록이 없습니다."));

            if(orders.getUserIdx() != user_idx) throw new IllegalArgumentException("유저 정보가 잘못되었습니다.");
            // 해당 주문 건이 배송중인 건인지 확인한다 (step = 2)
            if(orders.getStep() != 2){  // 배송 출발 이전, 구매 확정, 주문 취소의 경우
                throw new IllegalArgumentException("구매 확정 단계가 아닙니다.");
            }

            // step 의 값을 변경 후 저장한다.
            orders.setStep(3);
            ordersRepository.save(orders);

            return true;
        } catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    // 📌TODO : 관리자권한으로 배송 출발 (step = 2) 값 변환하기 ✅
    // 배송 출발 처리 ( ⚠️ 관리자만 가능 )
    public boolean orderDepart(Integer orders_idx){
        try{
            Orders orders = ordersRepository.findByIdx(orders_idx)
                    .orElseThrow(() -> new SQLDataException("해당하는 주문 목록이 없습니다."));

            // 해당 주문 건이 주문 완료 상태인지 확인
            if(orders.getStep() != 1){
                throw new IllegalArgumentException("배송 출발 단계가 아닙니다.");
            }

            // step 값을 변경 후 저장한다.
            orders.setStep(2);
            ordersRepository.save(orders);

            return true;
        } catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    // (관리자) 주문 목록 조회하기 ( 모든 주문 목록을 조회한다. )
    public List<OrderListResponse> getOrders(Integer orderStep){
        try {
            List<Orders> orderList = orderStep == 0 ? ordersRepository.findAll() : ordersRepository.findByStep(orderStep);

            // 주문의 상세 내역을 조회한다.
            List<OrderListResponse> orderDetailList = new ArrayList<>();
            orderList.stream().forEach((e) -> {
                // orderIdx 를 가져온다.
                Integer orderIdx = e.getIdx();
                // orderUser 를 가져온다.
                User user = userRepository.findByIdx(e.getUserIdx())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
                // 상세 주문 내역을 가져온다.
                String detailQuery = "SELECT i.idx AS 'item_idx', i.item_name AS 'item_name', o_l.ea AS 'ea' FROM order_log o_l \n" +
                        "JOIN item i ON i.idx = o_l.item_idx \n" +
                        "WHERE o_l.orders_idx = ?;";
                List<Map<String, Object>> itemInfo = (List<Map<String, Object>>) db.query(detailQuery, List.of(orderIdx)).get("result");
                orderDetailList.add(OrderListResponse.builder()
                                .orderIdx(orderIdx)
                                .orderStep(e.getStep())
                                .orderUser(user.getName())
                                .orderUserTel(user.getTel())
                                .address(user.getAddress())
                                .addressDetail(user.getDetail())
                                .orderDate(e.getDate())
                                .orderItems(itemInfo)
                        .build());
            });

            return orderDetailList;
        } catch (IllegalArgumentException e) {
            log.error("사용자 정보 조회 중 오류 발생: ", e.getMessage());
            throw new IllegalArgumentException();
        } catch (Exception e) {
            log.error("주문 목록 조회 중 오류 발생", e);
            throw new IllegalArgumentException();
        }
    }

}