package hwannee.project.item.controller;

import hwannee.project.User.domain.User;
import hwannee.project.User.dto.UserInfo;
import hwannee.project.User.service.UserService;
import hwannee.project.config.cloud.s3.S3Properties;
import hwannee.project.item.domain.Item;
import hwannee.project.item.domain.ItemImage;
import hwannee.project.item.dto.ItemListResponse;
import hwannee.project.item.dto.ItemRegistrationRequest;
import hwannee.project.item.repository.ItemImageRepository;
import hwannee.project.item.repository.ItemRepository;
import hwannee.project.item.service.ItemService;
import hwannee.project.libs.S3;
import hwannee.project.token.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLDataException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemApiController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final S3 s3;
    private final ItemImageRepository itemImageRepository;

    // 상품 등록 API (관리자 (판매자) 만 상품 등록)
    @PostMapping("/api/item")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> RegistrationItem(
            @RequestPart(value = "item") ItemRegistrationRequest itemRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images){
        Map<String, Object> response = new HashMap<>();
        try{
            // 토큰 인증 후 정보를 불러온다.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest token = (TokenRequest) authentication.getPrincipal();

            // 현재 로그인한 유저 정보 추가
            User user = userService.findByIdx(token.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(token.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            // 토큰 내부의 user_idx 값을 가져온다.
            itemRequest.setUser_idx(token.getIdx());

            // 상품 정보를 저장하고, 해당 상품 정보를 가져온다.
            Integer idx = itemService.save(itemRequest);
            Item item = itemRepository.findByIdx(idx)
                    .orElseThrow(() -> new SQLDataException("상품이 존재하지 않습니다."));

            if(images != null) {    // 이미지 정보가 들어왔을때만
                // 이미지 등록
                List<String> imageUrls = itemService.uploadImgs(images);

                for (int i = 0; i < imageUrls.size(); i++) {
                    String objectKey = s3.getObjectKey(imageUrls.get(i));

                    itemImageRepository.save(ItemImage.builder()
                            .itemIdx(idx)
                            .itemUrl(objectKey)
                            .seq(i)
                            .build());

                }
            }
            List<ItemImage> itemImageList = itemImageRepository.findByItemIdx(idx);
            log.info("상품이 등록되었습니다. 유저 정보 : "+userInfo.getUser_idx());
            response.put("itemInfo", item);
            response.put("itemImageInfo", itemImageList);
            response.put("userInfo", userInfo);
            response.put("message", "상품이 등록되었습니다.");
            return ResponseEntity.ok().body(response);
        } catch (SQLDataException e) {
            log.error("상품 등록에 실패하였습니다.\n"+e.getMessage());
            response.put("message", "상품 등록에 실패하였습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 상품 리스트 조회 (메인 페이지)
    @GetMapping("/api/public/alllist")
    @PreAuthorize("true")
    public ResponseEntity<?> getAllList(@RequestParam("itemSort") Integer itemSort){
        /*
        카태고리로 분류
        0 : 분류 X
        1 : 음식
        2 : 과일
        3 : 장난감
        4 : 주류
         */
        try{
            List<ItemListResponse> allList = itemService.getAllItemList(itemSort);
            log.info("메인 페이지 조회에 성공하였습니다.");
            return ResponseEntity.ok().body(Map.of("result", allList));
        } catch (Exception e){
            log.error("메인페이지 조회 : "+ e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Bad Request"));
        }
    }

    // 상품 상세 정보 조회
    @GetMapping("/api/public/item")
    @PreAuthorize("true")
    public ResponseEntity<?> getItem(@RequestParam("item_idx") Integer item_idx){
        try{
            Map<String, Object> response = new HashMap<>();
            List<ItemListResponse> item = itemService.getItem(item_idx);
            log.info("상품 조회에 성공하였습니다.");
            response.put("item", item);
            response.put("message", "ok");
            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            log.error("상품 조회 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Bad Request"));
        }
    }

    // 📌 관리자

    // (관리자) 상품 삭제
    @DeleteMapping("/api/admin/item")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteItem(@RequestParam("item_idx") Integer item_idx){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            TokenRequest auth = (TokenRequest) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();

            // 현재 로그인한 유저 정보 추가
            User user = userService.findByIdx(auth.getIdx());
            UserInfo userInfo = UserInfo.builder()
                    .user_idx(auth.getIdx())
                    .id(user.getId())
                    .user_name(user.getName())
                    .tel(user.getTel()).build();

            Boolean flag = itemService.deleteItem(item_idx);
            if(!flag) throw new IllegalArgumentException("상품 삭제 중 예상치 못한 오류가 발생하였습니다.");
            log.info("(관리자) 상품이 삭제되었습니다.");
            response.put("message", "(관리자)상품이 삭제되었습니다.");
            response.put("userInfo", userInfo);

            return ResponseEntity.ok().body(response);
        } catch (Exception e){
            log.error("상품 삭제 중 예상치 못한 오류가 발생하였습니다. : "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Bad Request"));
        }
    }

//    // 이미지 등록 API
//    @PostMapping("/api/upload/images")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
//    public ResponseEntity<List<String>> uploadImages
//    (@RequestPart(value="post", required = true) Map<String, Object> post,
//                                                     @RequestPart(value="image", required = true) List<MultipartFile> image){
//        System.out.println(post);
//
//        List<String> result = itemService.uploadImgs(image);
//
//        return ResponseEntity.ok().body(result);
//    }
}
