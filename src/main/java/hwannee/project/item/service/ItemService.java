package hwannee.project.item.service;

import hwannee.project.config.cloud.s3.S3Properties;
import hwannee.project.item.domain.Item;
import hwannee.project.item.domain.ItemImage;
import hwannee.project.item.dto.ItemListResponse;
import hwannee.project.item.dto.ItemRegistrationRequest;
import hwannee.project.item.repository.ItemImageRepository;
import hwannee.project.item.repository.ItemRepository;
import hwannee.project.libs.DataBase;
import hwannee.project.libs.S3;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final S3 s3;
    private final DataBase db;
    private final S3Properties s3Properties;

    // 상품 리스트 조회 (메인 페이지)
    public List<ItemListResponse> getAllItemList(Integer itemSort){
        try{
            List<Object> params = new ArrayList<>();
            StringBuilder query  = new StringBuilder("SELECT i.idx AS 'item_idx', i.item_name AS 'item_name', i.price AS 'item_price', i_c.category_name AS 'category',\n" +
                    "u.idx AS 'user_idx', u.name AS 'user_name', u.tel AS 'user_tel', u.address AS 'user_address', u.detail AS 'user_detail'\n" +
                    "\n" +
                    "FROM item i\n" +
                    "JOIN users u ON u.idx = i.user_idx\n" +
                    "JOIN item_category i_c ON i_c.idx = i.category\n");

            // 필터링
            if(itemSort != 0){
                query.append("WHERE i.category = ?");
                params.add(itemSort);
            }
            List<Map<String, Object>> queryList = (List<Map<String, Object>>) db.query(query.toString(), params).get("result");

            List<ItemListResponse> itemAllList = queryList.stream().map((e) -> {
                ItemListResponse itemListResponse = new ItemListResponse();
                itemListResponse.setItem_idx((Integer) e.get("item_idx"));
                itemListResponse.setItem_name((String) e.get("item_name"));
                itemListResponse.setItem_price((Integer) e.get("item_price"));
                itemListResponse.setCategory((String) e.get("category"));
                itemListResponse.setUser_idx((Integer) e.get("user_idx"));
                itemListResponse.setUser_name((String) e.get("user_name"));
                itemListResponse.setUser_tel((String) e.get("user_tel"));
                itemListResponse.setUser_address((String) e.get("user_address"));
                itemListResponse.setUser_detail((String) e.get("user_detail"));

                // 이미지 정보 호출
                List<ItemImage> imageList = itemImageRepository.findByItemIdx(itemListResponse.getItem_idx());
                List<String> imgUrl =
                        imageList.stream().map((el) -> s3Properties.getUrl().concat(el.getImageUrl())).toList();
                itemListResponse.setItemImages(imgUrl);
                return itemListResponse;
            }).collect(Collectors.toList());


            return itemAllList;
        } catch (Exception e){
            throw new IllegalArgumentException("전체 상품 목록 조회 중 예상치 못한 오류가 발생하였습니다.");
        }
    }

    // 상품 등록
    public Integer save(ItemRegistrationRequest request){
        try{
            return itemRepository.save(Item.builder()
                    .item_name(request.getItem_name())
                    .user_idx(request.getUser_idx())
                    .category(request.getCategory())
                    .price(request.getPrice())
                    .explanation(request.getExplanation())
                    .build()).getIdx();
        }catch(Exception e){
            log.error("상품 등록 : "+ e.getMessage());
            throw new SqlScriptException("SQL Error");
        }
    }

    // 상품 조회
    public List<ItemListResponse> getItem(Integer itemIdx){
        try{
            String itemQuery = "SELECT i.idx AS 'item_idx', i.item_name AS 'item_name', i.price AS 'item_price', i_c.category_name AS 'category', i.explanation AS 'explanation',\n" +
                    "u.idx AS 'user_idx', u.name AS 'user_name', u.tel AS 'user_tel', u.address AS 'user_address', u.detail AS 'user_detail'\n" +
                    "\n" +
                    "FROM item i\n" +
                    "JOIN users u ON u.idx = i.user_idx\n" +
                    "JOIN item_category i_c ON i_c.idx = i.category\n" +
                    "\n" +
                    "WHERE i.idx = ?;";

            List<Map<String, Object>> queryList = (List<Map<String, Object>>) db.query(itemQuery, List.of(itemIdx)).get("result");

            List<ItemListResponse> itemAllList = queryList.stream().map((e) -> {
                ItemListResponse itemListResponse = new ItemListResponse();
                itemListResponse.setItem_idx((Integer) e.get("item_idx"));
                itemListResponse.setItem_name((String) e.get("item_name"));
                itemListResponse.setItem_price((Integer) e.get("item_price"));
                itemListResponse.setCategory((String) e.get("category"));
                itemListResponse.setExplanation((String) e.get("explanation"));
                itemListResponse.setUser_idx((Integer) e.get("user_idx"));
                itemListResponse.setUser_name((String) e.get("user_name"));
                itemListResponse.setUser_tel((String) e.get("user_tel"));
                itemListResponse.setUser_address((String) e.get("user_address"));
                itemListResponse.setUser_detail((String) e.get("user_detail"));

                // 이미지 정보 호출
                List<ItemImage> imageList = itemImageRepository.findByItemIdx(itemListResponse.getItem_idx());
                List<String> imgUrl =
                        imageList.stream().map((el) -> s3Properties.getUrl().concat(el.getImageUrl())).toList();
                itemListResponse.setItemImages(imgUrl);

                // 리뷰 정보는 리뷰 컨트롤러에서

                return itemListResponse;
            }).collect(Collectors.toList());


            return itemAllList;
        } catch(Exception e){
            log.error("상품 조회 : "+e.getMessage());
            throw new IllegalArgumentException("상품 조회 중 예상치 못한 오류가 발생하였습니다.");
        }
    }

    // 상품 이미지 업로드
    public List<String> uploadImgs(List<MultipartFile> multipartFiles){
        List<String> result = s3.saveFiles(multipartFiles);

        return result;
    }

    // 📌 관리자

    // 상품 삭제
    public Boolean deleteItem(Integer item_idx){
        try{
            Item item = itemRepository.findByIdx(item_idx)
                    .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
            System.out.println(item);
            List<ItemImage> imageList = itemImageRepository.findByItemIdx(item_idx);
            imageList.forEach((e) -> {
                s3.deleteFile(e.getImageUrl());
            });
            itemRepository.deleteByIdx(item_idx);
            return true;
        } catch (Exception e){
            log.error("상품 삭제 중 오류가 발생하였습니다. : "+e.getMessage());
            return false;
        }
    }
}
