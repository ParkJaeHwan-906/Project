package hwannee.project.item.service;

import hwannee.project.item.domain.Review;
import hwannee.project.item.repository.ReviewRepository;
import hwannee.project.libs.DataBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private DataBase db;

    @DisplayName("JPA 의 DELETE 를 테스트한다.")
    @Test
    void deleteJPA() {
        // given : Review 데이터를 생성한다.
        reviewRepository.save(Review.builder()
                .order_idx(1)
                .score(1)
                .review("안녕하세요?").build());

        Review review = reviewRepository.findByIdx(1)
                .orElseThrow(() -> new IllegalArgumentException("저장실패"));

        // when : 데이터를 삭제한다.
        reviewRepository.deleteByidx(1);
        Review review2 = reviewRepository.findByIdx(1)
                .orElseThrow(() -> new IllegalArgumentException("삭제 성공"));

        // then : 삭제되었는지 확인한다.
        assertThat(review2).isNull();

    }
}