package hwannee.project.item.repository;

import hwannee.project.item.domain.Review;
import hwannee.project.item.dto.ReviewResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review>findByOrderIdx(Integer order_idx);
    Optional<Review>findByIdx(Integer idx);

    @Transactional
    void deleteByidx(Integer idx);
}
