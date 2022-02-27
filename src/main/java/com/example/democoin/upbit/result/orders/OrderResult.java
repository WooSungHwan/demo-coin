package com.example.democoin.upbit.result.orders;


import com.example.democoin.upbit.enums.OrdSideType;
import com.example.democoin.upbit.enums.OrdType;
import com.example.democoin.upbit.enums.OrderStateType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class OrderResult {
    private String uuid;
    private OrdSideType side;
    @JsonProperty("ord_type")
    private OrdType ordType;
    private String price;
    private OrderStateType state;
    private String market;
    @JsonProperty("created_at")
    private ZonedDateTime createdAt; // KST
    private BigDecimal volume;
    @JsonProperty("remaining_volume")
    private BigDecimal remainingVolume;
    @JsonProperty("reserved_fee")
    private BigDecimal reservedFee;
    @JsonProperty("remaining_fee")
    private BigDecimal remainingFee;
    @JsonProperty("paid_fee")
    private BigDecimal paidFee;
    private BigDecimal locked;
    @JsonProperty("executed_volume")
    private BigDecimal executedVolume;
    @JsonProperty("trades_count")
    private Integer tradesCount;
}
