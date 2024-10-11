package hwannee.project.item.controller;

import hwannee.project.User.domain.User;
import hwannee.project.User.dto.UserInfo;
import hwannee.project.User.service.UserService;
import hwannee.project.item.dto.ReviewListRequest;
import hwannee.project.item.dto.ReviewModifyRequest;
import hwannee.project.item.service.ReviewService;
import hwannee.project.libs.Auth;
import hwannee.project.token.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    // 특정 상품의 리뷰들을 조회한다.
    @GetMapping("/api/public/reviews")
    @PreAuthorize("true")
    public ResponseEntity<Map<String, Object>> getItemReviews(@RequestParam("item_idx") Integer item_idx){
        Map<String, Object> response = new HashMap<>();
        try{
            List<?> itemReviews = reviewService.getItemReviews(item_idx);
            response.put("message", "상품 후기를 조회했습니다.");
            response.put("reviews", itemReviews);
            return ResponseEntity.ok().body(response);
        } catch(Exception e){
            System.out.println(e);
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 특정 사용자의 리뷰 작성 목록을 보여준다.
    @GetMapping("/api/management/reviews")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> managementReview(){
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest request = (TokenRequest) authentication.getPrincipal();

            // 현재 로그인한 유저 정보 추가
            User user = userService.findByIdx(request.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(request.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            // 특정 사용자의 인덱스로 리뷰를 조회한다.
            List<ReviewListRequest> reviewList = reviewService.getReviewList(request.getIdx());
            response.put("reviewList", reviewList);
            response.put("userInfo", userInfo);
            response.put("message", "작성 후기를 조회하였습니다.");

            return ResponseEntity.ok().body(response);
        } catch(Exception e){
            System.out.println(e);
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 특정 사용자의 구매 목록을 보여준다. (리뷰 작성하기 구분)
    @GetMapping("/api/orderlist")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> orderList(){
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
            List<Map<String, Object>> orderList = reviewService.getOrderList(tokenRequest.getIdx());

            // 주문 상태 코드에 따라 문자로 변경
            orderList.forEach((e) -> {
                Integer step = (Integer) e.get("delivery_step");
                StringBuilder sb = new StringBuilder();
                switch (step){
                    case 1:
                        sb.append("주문 완료");
                        break;
                    case 2:
                        sb.append("배송 출발");
                        break;
                    case 3:
                        sb.append("배송 완료");
                        break;
                    default:
                        sb.append("주문 취소");
                }
                e.put("delivery_step", sb.toString());
            });
            response.put("message", "주문 목록을 조회합니다.");
            response.put("userInfo", userInfo);
            response.put("orderList", orderList);

            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            System.out.println(e);
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/api/write/review")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    // 리뷰 작성하기
    public ResponseEntity<Map<String, Object>> writeReview(@RequestBody Map<String, Object> request){
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

            Boolean flag = reviewService.writeReview(tokenRequest.getIdx(),
                    (Integer) request.get("orders_idx"), (Integer) request.get("item_idx"), (Integer) request.get("score"), (String) request.get("review"));
            if(!flag) throw new IllegalArgumentException("리뷰 작성 오류");
            response.put("userInfo", userInfo);
            response.put("message", "상품 후기가 등록되었습니다.");

        } catch(Exception e){
            System.out.println(e);
            response.put("message", "상품 후기 작성 중 예상치 못한 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 리뷰를 수정한다.
    @PutMapping("/api/modify/review")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> modifyReview(@RequestBody ReviewModifyRequest request){
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest auth = (TokenRequest) authentication.getPrincipal();

            Boolean flag = reviewService.modifyReview(auth.getIdx(), request.getReview_idx(), request.getScore(), request.getReview());

            if(!flag) throw new IllegalArgumentException("리뷰 수정에 실패햐였습니다.");

            response.put("message", "상품 후기 수정에 성공하였습니다.");
            return ResponseEntity.ok().body(response);
        } catch(Exception e) {
            System.out.println(e);
            response.put("message", "상품 후기 작성 중 예상치 못한 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 📌 관리자 권한
    // 리뷰를 삭제한다. (관리자 / 일반 사용자)
    @DeleteMapping("/api/admin/delete/review")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteReview(@RequestParam("review_idx") Integer review_idx){
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest auth = (TokenRequest) authentication.getPrincipal();

            Boolean flag;
            // 관리자라면, 사용자 인증 과정 없이 상품 후기 삭제
            if(auth.getRole().equals("ROLE_ADMIN")){
                flag = reviewService.deleteReview(review_idx);
            }else{
                flag = reviewService.deleteReview(auth.getIdx(), review_idx);
            }

            response.put("message", "상품 후기가 정상적으로 삭제되었습니다.");

            if(!flag) throw new IllegalArgumentException("상품 후기 삭제 중 오류 발생");

            return ResponseEntity.ok().body(response);
        } catch(Exception e) {
            System.out.println(e);
            response.put("message", "상품 후기가 삭제 중 예상치 못한 오류가 발생하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 모든 리뷰를 관리한다.
    @GetMapping("/api/admin/management/reviews")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> managementAllReviews
    (@RequestParam("itemSort") Integer itemSort,
     @RequestParam("scoreSort") Integer scoreSort) {
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest request = (TokenRequest) authentication.getPrincipal();
            // 현재 로그인한 유저 정보 추가
            User user = userService.findByIdx(request.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(request.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            // 리뷰를 조회한다.
            List<ReviewListRequest> reviewList = reviewService.getAllReviews(itemSort, scoreSort);

            response.put("reviewList", reviewList);
            response.put("userInfo", userInfo);
            response.put("message", "(관리자) 모든 상품 후기를 조회합니다.");

            log.info("(관리자) 모든 상품 후기를 조회하였습니다.");

            return ResponseEntity.ok().body(response);
        } catch(Exception e){
            log.error(e.getMessage());
            response.put("message", "Bad Request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
