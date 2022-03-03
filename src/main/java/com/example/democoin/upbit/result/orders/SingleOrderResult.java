package com.example.democoin.upbit.result.orders;

import com.example.democoin.upbit.enums.OrdSideType;
import com.example.democoin.upbit.enums.OrdType;
import com.example.democoin.upbit.enums.OrderStateType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class SingleOrderResult {
    private String uuid;                    //	주문의 고유 아이디	String
    private OrdSideType side;               //	주문 종류	String
    @JsonProperty("ord_type")
    private OrdType ordType;               //	주문 방식	String
    private BigDecimal price;               //	주문 당시 화폐 가격	NumberString
    private OrderStateType state;           //	주문 상태	String
    private String market;                  //	마켓의 유일키	String
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;        //	주문 생성 시간	DateString
    private BigDecimal volume;              //	사용자가 입력한 주문 양	NumberString
    @JsonProperty("remaining_volume")
    private BigDecimal remainingVolume;    //	체결 후 남은 주문 양	NumberString
    @JsonProperty("reserved_fee")
    private BigDecimal reservedFee;        //	수수료로 예약된 비용	NumberString (실질 수수료)
    @JsonProperty("remaining_fee")
    private BigDecimal remainingFee;       //	남은 수수료	NumberString
    @JsonProperty("paid_fee")
    private BigDecimal paidFee;            //	사용된 수수료	NumberString
    private BigDecimal locked;              //	거래에 사용중인 비용	NumberString
    @JsonProperty("executed_volume")
    private BigDecimal executedVolume;     //	체결된 양	NumberString
    @JsonProperty("trade_count")
    private Integer tradeCount;            //	해당 주문에 걸린 체결 수	Integer
    private List<Trade> trades;

    @Data
    public static class Trade {
        private String market;      // 마켓의 유일 키
        private String uuid;        // 체결의 고유 아이디
        private BigDecimal price;   // 체결 가격
        private BigDecimal volume;  // 체결 양
        private BigDecimal funds;   // 체결된 총 가격
        private OrdSideType side;   // 체결 종류
        @JsonProperty("created_at")
        private ZonedDateTime createdAt; // 체결 시각
    }
}
