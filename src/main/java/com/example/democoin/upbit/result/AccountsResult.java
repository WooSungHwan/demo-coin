package com.example.democoin.upbit.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class AccountsResult {

    private String currency; // 화폐를 의미하는 영문 대문자 코드 ex) "KRW"

    private String balance; // 주문가능 금액/수량 ex) "99188.33845701"

    private String locked; // 주문 중 묶여있는 금액/수량 ex) "0.0"

    @JsonProperty("avg_buy_price")
    private String avgBuyPrice; // 매수평균가

    @JsonProperty("avg_buy_price_modified")
    private boolean avgBuyPriceModified; // 매수평균가 수정여부

    @JsonProperty("unit_currency")
    private String unitCurrency; // 평단가 기준 화폐

}
