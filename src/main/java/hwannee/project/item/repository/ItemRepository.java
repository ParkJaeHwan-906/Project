package hwannee.project.item.repository;

import hwannee.project.item.domain.Item;
import hwannee.project.token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findByIdx(Integer idx);
    Optional<Item> findByCategory(Integer category);

    // 아이템을 삭제한다.
    void deleteByIdx(Integer item_idx);
}
