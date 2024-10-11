package hwannee.project.item.service;

import hwannee.project.Senior2ProjectApplication;
import hwannee.project.item.domain.OrderLog;
import hwannee.project.item.domain.Orders;
import hwannee.project.item.domain.Review;
import hwannee.project.item.dto.ReviewListRequest;
import hwannee.project.item.dto.ReviewResponse;
import hwannee.project.item.repository.OrderLogRepository;
import hwannee.project.item.repository.OrdersRepository;
import hwannee.project.item.repository.ReviewRepository;
import hwannee.project.libs.DataBase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrdersRepository ordersRepository;
    private final OrderLogRepository orderLogRepository;
    private final DataBase db;

    // 특정 상풍에 대한 리뷰를 불러온다.
    public List<Map<String, Object>> getItemReviews(Integer item_idx){
        String query = "SELECT i_r.idx AS 'review_idx', u.name AS 'user_name', i.item_name AS 'item_name', o_l.ea AS 'ea', os.date AS 'order_date', i_r.score AS 'score', i_r.review AS 'review' " +
                "FROM item_review i_r\n" +
                "JOIN order_log o_l ON o_l.idx = i_r.order_idx\n" +
                "JOIN item i ON i.idx = o_l.item_idx\n" +
                "JOIN orders os ON os.idx = o_l.orders_idx\n" +
                "JOIN users u ON u.idx = os.user_idx\n" +
                "\n" +
                "WHERE o_l.item_idx = ?" + "\n" +
                "AND os.step = 3" + "\n" +
                "AND u.ban = 0;";

        // db.query에서 반환된 Map의 "result"를 List<Map<String, Object>>로 캐스팅
        return (List<Map<String, Object>>) db.query(query, Arrays.asList(item_idx)).get("result");
    }

    // 사용자가 구매한 제품에 대해서만 리뷰가 작성 가능하다.

    // 1. 구매 목록에서 리뷰를 작성할 목록을 보여준다. (내림차순 : 최근 날짜 순)
    //  order_log 테이블의 review 컬럼의 값으로 구분
    public List<Map<String, Object>> getOrderList(Integer user_idx){
        String query = "SELECT i.idx AS 'item_idx', os.idx AS 'orders_idx', i.item_name AS 'item_name', o_l.ea AS 'item_ea', os.date AS 'order_date', os.step AS 'delivery_step', o_l.review AS 'review_check'\n" +
                "FROM orders os \n" +
                "\n" +
                "JOIN order_log o_l ON o_l.orders_idx = os.idx\n" +
                "JOIN item i ON i.idx = o_l.item_idx\n" +
                "\n" +
                "WHERE os.user_idx = ?\n" +
                "\n" +
                "ORDER BY order_date DESC;";

        return (List<Map<String, Object>>) db.query(query, Arrays.asList(user_idx)).get("result");
    }

    // 2. 리뷰를 작성한다. (여러 곳에서 쓰일 수 있음)
    /*
    1. 사용자의 user_idx 를 확인한다.
    2. 사용자의 주문 내역을 확인한다. (orders 테이블)
    3. order_log 의 상세 내역을 확인 후 review 컬럼의 값이 0 이라면 리뷰를 작성한다.
     */
    private Integer order_logIdx;
    // 리뷰를 쓸 수 있는지 확인한다.
    private Boolean checkReview(Integer user_idx, Integer orders_idx, Integer item_idx){
        try{
            String query = "SELECT o_l.idx AS 'idx' FROM orders os \n" +
                    "JOIN order_log o_l ON o_l.orders_idx = os.idx\n" +
                    "\n" +
                    "WHERE os.user_idx = ?\n" +
                    "AND os.idx = ?\n" +
                    "AND o_l.item_idx = ?\n" +
                    "AND o_l.review = 0\n" +
                    "AND os.step = 3;";

            List<Map<String, Object>> list = (List<Map<String, Object>>) db.query(query, Arrays.asList(user_idx, orders_idx, item_idx)).get("result");
            order_logIdx = (Integer) list.get(0).get("idx");

            return list.size() == 1;
        } catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    // 리뷰를 작성한다.
    public Boolean writeReview(Integer user_idx, Integer orders_idx, Integer item_idx
    , Integer score, String review){
        try{
            if(!checkReview(user_idx, orders_idx, item_idx)){   // 리뷰를 쓸 대상이 있는지 조회한다.
                // 거짓일 때 예외 발생
                throw new IllegalArgumentException();
            }

            // 리뷰 작성 가능 -> 리뷰 작성
            Integer orderLogIdx = reviewRepository.save(Review.builder()
                            .order_idx(order_logIdx)
                            .score(score)
                            .review(review).build()).getOrderIdx();

            // order_log 테이블의 review 값 1(작성 완료)로 변경
            String setQuery = "UPDATE order_log SET review = 1 WHERE idx = ?";
            db.query(setQuery, Arrays.asList(orderLogIdx));
            // 리뷰 작성 완료
            return true;
        }catch (Exception e){
            System.out.println(e);
            // 리뷰 작성 실패
            return false;
        }
    }

    // 사용자가 쓴 리뷰를 조회한다. (내 리뷰 관리하기)
    public List<ReviewListRequest> getReviewList(Integer user_idx){
        String query = "SELECT i.idx AS 'item_idx', i.item_name AS 'item_name', o_l.ea 'ea', \n" +
                "os.idx AS 'orders_idx', os.date 'order_date', \n" +
                "i_r.idx AS 'review_idx', i_r.order_idx AS 'order_idx', u.idx AS 'user_idx', u.name AS 'user_name', i_r.score AS 'score', i_r.review AS 'review'\n" +
                "\n" +
                "FROM orders os\n" +
                "\n" +
                "JOIN order_log o_l ON o_l.orders_idx = os.idx\n" +
                "JOIN item_review i_r ON i_r.order_idx = o_l.idx\n" +
                "JOIN item i ON i.idx = o_l.item_idx\n" +
                "JOIN users u ON u.idx = os.user_idx\n" +
                "\n" +
                "WHERE os.user_idx = ?";
        return (List<ReviewListRequest>) db.query(query, Arrays.asList(user_idx)).get("result");
    }

    // 리뷰를 수정한다.
    // 리뷰를 쓴 사용자가 맞는지 확인한다. 수정하려는 리뷰가 맞는지 확인한다.
    private Boolean checkReview(Integer user_idx, Integer review_idx){
        String query = "SELECT COUNT(*) AS 'cnt' FROM orders os\n" +
                "JOIN order_log o_l ON o_l.orders_idx = os.idx\n" +
                "JOIN item_review i_r ON i_r.order_idx = o_l.idx\n" +
                "\n" +
                "WHERE os.user_idx = ?\n" +
                "AND i_r.idx = ?";

        // 해당 사용자가 쓴 리뷰의 존재 유무를 확인한다.
        List<Map<String, Object>> list = (List<Map<String, Object>>) db.query(query, Arrays.asList(user_idx, review_idx)).get("result");
        if((Long)list.get(0).get("cnt") == 1) return true;

        return false;
    }
    public Boolean modifyReview(Integer user_idx, Integer review_idx, Integer score, String reviewText){
        try{
            if(!checkReview(user_idx, review_idx)) throw new IllegalArgumentException("해당 유저의 리뷰가 존재하지 않습니다.");

            // 리뷰 내용을 수정한다.
            Review review =reviewRepository.findByIdx(review_idx)
                    .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

            review.setReview(reviewText);
            review.setScore(score);

//            reviewRepository.save(review);
            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    // 리뷰를 삭제한다. (사용자 본인이 사용자 본인의 리뷰를 삭제한다.)
    public Boolean deleteReview(Integer user_idx, Integer reivew_idx){
        try{
            // review idx 와 user_idx 로 리뷰를 불러온다.
            Boolean flag = checkReview(user_idx, reivew_idx);
            if(!flag) throw new IllegalArgumentException("삭제하려는 리뷰가 존재하지 않습니다.");

            // 리뷰를 삭제한다.
            // ✅ 테스트 완료
            reviewRepository.deleteByidx(reivew_idx);

            return true;
        } catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    // 📌 관리자 전용
    // 리뷰를 삭제한다. (관리자 권한으로 사용자의 리뷰를 삭제한다.)
    public Boolean deleteReview(Integer reivew_idx){
        try{
            // 리뷰를 삭제한다.
            // ✅ 테스트 완료
            reviewRepository.deleteByidx(reivew_idx);

            return true;
        } catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    // 리뷰를 관리한다. (관리자 권한으로 모든 상품의 리뷰를 한 곳에 모아 관리한다.)
    public List<ReviewListRequest> getAllReviews(Integer itemSort, Integer scoreSort){
        List<Object> params = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT i.idx AS 'item_idx', i.item_name AS 'item_name', o_l.ea 'ea', \n" +
                "os.idx AS 'orders_idx', os.date 'order_date', \n" +
                "i_r.idx AS 'review_idx', i_r.order_idx AS 'order_idx', u.idx AS 'user_idx', u.name AS 'user_name', i_r.score AS 'score', i_r.review AS 'review'\n" +
                "\n" +
                "FROM orders os\n" +
                "\n" +
                "JOIN order_log o_l ON o_l.orders_idx = os.idx\n" +
                "JOIN item_review i_r ON i_r.order_idx = o_l.idx\n" +
                "JOIN item i ON i.idx = o_l.item_idx\n" +
                "JOIN users u ON u.idx = os.user_idx\n");

        // A. 상품별 (자동으로 상품별로 묶임)
        // 1. 모든 상품 리뷰 보기 (특정 조건 X) -> itemSort = 0

        // 2. 특정 상품별 리뷰 보기
        if(itemSort != 0){
            query.append("WHERE item_idx = ?\n");
            params.add(itemSort);
        }

        // B. 상품 후기 점수 (Default : 내림차순) ( 0 : 상품 별 점수 별 내림차순, 1 : 상품 별 점수 별 오름차순)
        //  ⚠️ 상품 관계없이 평점 별 정렬? ( 10 : 전체 상품 점수 별 내림차순, 11 : 전체 상품 점수 별 오름차순)
        if(scoreSort == 0 || scoreSort == 1){
            query.append("ORDER BY item_idx, score");
        }else{
            query.append("ORDER BY score");
        }


        if(scoreSort == 0 || scoreSort == 10){
            query.append(" DESC");
        }

        //  ⚠️ ? ( 바인딩 파라미터 ) 가 없을 때 값을 전달하면 오류가 뜸 -> 빈 리스트로 전달해야함
        return (List<ReviewListRequest>) db.query(query.toString(), params).get("result");
    }
}