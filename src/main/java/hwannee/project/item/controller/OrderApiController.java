package hwannee.project.item.controller;

import hwannee.project.User.domain.User;
import hwannee.project.User.dto.UserInfo;
import hwannee.project.User.service.UserService;
import hwannee.project.item.dto.OrderListResponse;
import hwannee.project.item.dto.OrderRequest;
import hwannee.project.item.service.OrderService;
import hwannee.project.libs.Auth;
import hwannee.project.token.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderApiController {
    private final OrderService orderService;
    private final UserService userService;

    // 상품을 주문한다.
    @PostMapping("/api/order")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> doOrder(@RequestBody List<OrderRequest> request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal();

        // 현재 로그인한 유저 정보 추가
        User user = userService.findByIdx(tokenRequest.getIdx());
        UserInfo userInfo = UserInfo.builder()
                .user_idx(tokenRequest.getIdx())
                .id(user.getId())
                .user_name(user.getName())
                .tel(user.getTel()).build();

        Map<String, Object> response = new HashMap<>();
        try{
            // 1. 주문정보를 등록한다.
            Integer orders_idx = orderService.order(tokenRequest.getIdx());

            // 2. 주문의 상세 정보를 등록한다.
            Boolean success = orderService.orderDetail(orders_idx, request);

            if(!success){
                throw new IllegalArgumentException("주문 중 에러 발생");
            }
            response.put("userInfo", userInfo);
            response.put("message", "주문이 완료되었습니다.");
            return ResponseEntity.ok().body(response);

        } catch(Exception e){
            System.out.println(e);
            log.error(String.valueOf(e));
            response.put("message", "주문 중 에러가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 주문을 취소한다.
    @PutMapping("/api/order/cancle")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> orderCancle(@RequestBody Map<String, Object> request){
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal();

            // 현재 로그인한 유저 정보 추가
            User user = userService.findByIdx(tokenRequest.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(tokenRequest.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            boolean check = orderService.orderCancle((Integer)request.get("orders_idx"), tokenRequest.getIdx());
            if(!check){
                throw new IllegalArgumentException("주문 취소 중 예상치 못한 오류가 발생하였습니다.");
            }
            response.put("userInfo", userInfo);
            response.put("message", "주문이 취소되었습니다.");
        } catch(Exception e){
            System.out.println(e);
            response.put("message", "주문 취소 중 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    // 구매를 확정한다.
    @PutMapping("/api/order/confirm")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> orderConfirm(@RequestBody Map<String, Object> request){
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal();

            // 현재 로그인한 유저 정보 추가
            User user = userService.findByIdx(tokenRequest.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(tokenRequest.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            boolean check = orderService.orderConfirm((Integer) request.get("orders_idx"), tokenRequest.getIdx());
            if(!check){
                throw new IllegalArgumentException("구매 확정 중 예상치 못한 오류가 발생하였습니다.");
            }
            response.put("userInfo", userInfo);
            response.put("message", "구매가 확정되었습니다.");
        } catch(Exception e){
            System.out.println(e);
            response.put("message", "구매 확정 중 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    // 📌 관리자

    // 📌TODO : 관리자권한으로 배송 출발 (step = 2) 값 변환하기 ✅
    // ⚠️ 상품 등록자 별 물품관리 필요 (나중에)
    // (관리자) 배송을 출발한다.
    @PutMapping("/api/admin/order/depart")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')") // ⚠️ 관리자만 가능
    public ResponseEntity<Map<String, Object>> orderDepart(@RequestBody Map<String, Object> request){
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest tokenRequest = (TokenRequest) authentication.getPrincipal();
            // 현재 로그인한 유저 정보 추가
            User user = userService.findByIdx(tokenRequest.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(tokenRequest.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            boolean check = orderService.orderDepart((Integer) request.get("orders_idx"));
            if(!check){
                throw new IllegalArgumentException("배송 출발 중 예상치 못한 오류가 발생하였습니다.");
            }
            response.put("userInfo", userInfo);
            response.put("message", "배송이 출발하였습니다.");
        } catch(Exception e){
            System.out.println(e);
            response.put("message", "배송 출발 중 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    // (관리자) 주문을 조회한다.
    @GetMapping("/api/admin/orders")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getOrders(@RequestParam("orderStep") Integer orderStep){
        try{
            Map<String, Object> response = new HashMap<>();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest auth = (TokenRequest) authentication.getPrincipal();

            User user = userService.findByIdx(auth.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(auth.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            List<OrderListResponse> orderList = orderService.getOrders(orderStep);
            response.put("orderList", orderList);
            response.put("userInfo", userInfo);
            response.put("message", "주문 목록을 조회하였습니다.");

            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            log.error("(관리자) 주문 목록을 조회하던 도중 오류가 발생했습니다. : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Bad Request"));
        }
    }
}

