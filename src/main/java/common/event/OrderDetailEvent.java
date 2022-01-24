package common.event;

import com.example.ordermodule.entity.OrderDetail;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetailEvent {

    private Long productId;
    private Long orderId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderDetailEvent(OrderDetail orderDetail) {
        this.productId = orderDetail.getProductId();
        this.orderId = orderDetail.getOrderId();
        this.productName = orderDetail.getProductName();
        this.quantity = orderDetail.getQuantity();
        this.unitPrice = orderDetail.getUnitPrice();
    }

    public boolean validation() {
        return this.orderId != null && this.productId != null && this.productName != null
                && this.quantity > 0 && this.unitPrice.compareTo(BigDecimal.valueOf(0)) > 0;
    }

}
