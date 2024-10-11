package hwannee.project.item.repository;

import hwannee.project.item.domain.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemImageRepository extends JpaRepository<ItemImage, Integer> {
    List<ItemImage> findByItemIdx(Integer itemIdx);
}
