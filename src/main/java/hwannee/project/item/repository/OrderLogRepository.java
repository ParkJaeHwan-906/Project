package hwannee.project.item.repository;

import hwannee.project.item.domain.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderLogRepository extends JpaRepository<OrderLog, Integer> {
//    Optional<OrderLog>findByOrdersIdx(Integer orderIdx);
    List<OrderLog>findByOrdersIdx(Integer orderIdx);
}
