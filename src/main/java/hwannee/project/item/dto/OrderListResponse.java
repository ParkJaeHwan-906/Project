package hwannee.project.item.dto;

import hwannee.project.item.domain.OrderLog;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderListResponse {
    private Integer orderIdx;
    private Integer orderStep;
    private String orderUser;
    private String orderUserTel;
    private String address;
    private String addressDetail;
    private LocalDateTime orderDate;
    private List<?> orderItems;

    @Builder
    public OrderListResponse(Integer orderIdx, Integer orderStep,
                             String orderUser, String orderUserTel, String address, String addressDetail,
                             LocalDateTime orderDate,
                             List<?> orderItems){
        this.orderIdx = orderIdx;
        this.orderStep = orderStep;
        this.orderUser = orderUser;
        this.orderUserTel = orderUserTel;
        this.address = address;
        this.addressDetail = addressDetail;
        this.orderDate = orderDate;
        this.orderItems = orderItems;
    }
}
