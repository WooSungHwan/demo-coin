package com.example.democoin.upbit.result.orders;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderCancelResult {
    private String uuid;                            // 주문의 고유 아이디
    private String side;                            // 주문 종류
    private String ord_type;                        // 주문 방식
    private BigDecimal price;                       // 주문 당시 화폐 가격
    private String state;                           // 주문 상태
    private String market;                          // 마켓의 유일키
    private LocalDateTime created_at;               // 주문 생성 시간
    private String volume;                          // 사용자가 입력한 주문 양
    private BigDecimal remaining_volume;            // 체결 후 남은 주문 양
    private BigDecimal reserved_fee;                // 수수료로 예약된 비용
    private BigDecimal remaining_fee;               // 남은 수수료
    private BigDecimal paid_fee;                    // 사용된 수수료
    private BigDecimal locked;                      // 거래에 사용중인 비용
    private BigDecimal executed_volume;             // 체결된 양
    private Integer trade_count;                    // 해당 주문에 걸린 체결 수
}
