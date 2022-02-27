package com.example.democoin.upbit.result.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MarketOrderableResult {

    @JsonProperty("bid_fee")
    private BigDecimal bidFee; // 매수 수수료 비율

    @JsonProperty("ask_fee")
    private BigDecimal askFee; // 매도 수수료 비율

    @JsonProperty("maker_bid_fee")
    private BigDecimal makerBidFee;

    @JsonProperty("maker_ask_fee")
    private BigDecimal makerAskFee;

    private Market market; // 마켓에 대한 정보

    @JsonProperty("bid_account")
    private BidAccount bidAccount; // 매수 시 사용하는 화폐의 계좌 상태

    @JsonProperty("ask_account")
    private AskAccount askAccount; // 매도 시 사용하는 화폐의 계좌 상태

    @Data
    private static class Market {
        private String id; // 마켓의 유일 키
        private String name; // 마켓 이름
        @JsonProperty("order_types")
        private List<String> orderTypes; // 지원 주문 방식
        @JsonProperty("order_sides")
        private List<String> orderSides; // 지원 주문 종류
        private Bid bid; // 매수 시 제약사항
        private Ask ask; // 매도 시 제약사항
        @JsonProperty("max_total")
        private BigDecimal maxTotal; // 최대 매도/매수 금액
        private String state; // 마켓 운영 상태

        @Data
        private static class Bid {
            private String currency; // 화폐를 의미하는 영문 대문자 코드 ("KRW")
            @JsonProperty("price_unit")
            private String priceUnit; // 주문금액 단위
            @JsonProperty("min_total")
            private BigDecimal minTotal; // 최소 매도/매수 금액
        }

        @Data
        private static class Ask {
            private String currency; // 화폐를 의미하는 영문 대문자 코드 ("KRW")
            @JsonProperty("price_unit")
            private String priceUnit; // 주문금액 단위
            @JsonProperty("min_total")
            private BigDecimal minTotal; // 최소 매도/매수 금액
        }
    }

    @Data
    private static class BidAccount {
        private String currency; // 화폐를 의미하는 영문 대문자 코드 ("KRW")
        private BigDecimal balance; // 주문가능 금액/수량
        private BigDecimal locked; // 주문 중 묶여있는 금액/수량
        @JsonProperty("avg_buy_price")
        private BigDecimal avgBuyPrice; // 매수평균가
        @JsonProperty("avg_buy_price_modified")
        private boolean avgBuyPriceModified; // 매수평균가 수정 여부
        @JsonProperty("unit_currency")
        private String unitCurrency; // 평단가 기준 화폐
    }

    @Data
    private static class AskAccount {
        private String currency; // 화폐를 의미하는 영문 대문자 코드 ("KRW")
        private BigDecimal balance; // 주문가능 금액/수량
        private BigDecimal locked; //주문 중 묶여있는 금액/수량
        @JsonProperty("avg_buy_price")
        private BigDecimal avgBuyPrice; // 매수평균가
        @JsonProperty("avg_buy_price_modified")
        private boolean avgBuyPriceModified; // 매수평균가 수정 여부
        @JsonProperty("unit_currency")
        private String unitCurrency; // 평단가 기준 화폐
    }

}
