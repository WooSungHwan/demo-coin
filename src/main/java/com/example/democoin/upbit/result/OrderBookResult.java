package com.example.democoin.upbit.result;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderBookResult {
    private String type; // orderbook
    private String code; // KRW-BTC (market)
    private Long timestamp;
    private BigDecimal total_ask_size; // 호가 매도 총 잔량
    private BigDecimal total_bid_size; // 호가 매수 총 잔량
    private List<OrderBookUnit> orderbook_units; // 호가
    public String stream_type; // TODO enum SNAPSHOT : 스냅샷, REALTIME : 실시간

    @Data
    private static class OrderBookUnit {
        private BigDecimal ask_price; // 매도 호가
        private BigDecimal bid_price; // 매수 호가
        private BigDecimal ask_size; // 매도 잔량
        private BigDecimal bid_size; // 매수 잔량
    }

}
