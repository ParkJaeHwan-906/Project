package hwannee.project.item.service;

import hwannee.project.item.domain.Category;
import hwannee.project.item.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 카테고리의 목록을 가져온다.
    public List<Category> getAllList(){
        return categoryRepository.getAllList();
    }
}
