package hwannee.project.item.controller;

import hwannee.project.item.domain.Category;
import hwannee.project.item.dto.CategoryResponse;
import hwannee.project.item.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CategoryApiController {
    private final CategoryService categoryService;

    @GetMapping("/api/public/category")
    @PreAuthorize("true")
    public ResponseEntity<CategoryResponse> getAllList(){
        Map<String, Object> response = new HashMap<>();
        CategoryResponse categoryResponse = new CategoryResponse();  // 객체 생성

        List<Category> categoryList = categoryService.getAllList();

        // Category 리스트를 Map<Integer, String>으로 변환
        // 1. list 를 순회하며 각각의 Category 를 가져옴
        List<Map<Integer, String>> categoryMap = categoryList.stream()
                // 각 Category 객체를 Map으로 변환
                .map(category -> Map.of(category.getIdx(), category.getCategory_name()))
                // List로 수집
                .collect(Collectors.toList());

        categoryResponse.setCategoryList(categoryMap);
        categoryResponse.setMessage("목록을 조회하였습니다.");

        return ResponseEntity.ok().body(categoryResponse);
    }
}
