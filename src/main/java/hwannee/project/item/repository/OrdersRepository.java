package hwannee.project.item.repository;

import hwannee.project.item.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    Optional<Orders>findByIdx(Integer idx);
    List<Orders> findByUserIdx(Integer userIdx);

    List<Orders> findAll();
    List<Orders> findByStep(Integer step);
}
