package com.example.democoin.upbit.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeResult {
    private String type; // trade
    private String code; // KRW-BTC
    private Long timestamp;
    public String trade_date; // 최근 거래 일자(UTC) yyyyMMdd
    public String trade_time; // 최근 거래 시각(UTC) HHmmss
    private Long trade_timestamp; // 체결 타임스탬프 (millisecond)
    private BigDecimal trade_price; // 체결 가격
    private BigDecimal trade_volume; // 체결량
    public String ask_bid; // 매수/매도 구분
    private BigDecimal prev_closing_price; // 전일 종가
    public String change; // RISE : 상승, EVEN : 보합, FALL : 하락  TODO enum
    private BigDecimal change_price; // 부호 없는 전일 대비 값
    private Long sequential_id; // 체결 번호 (Unique)
    public String stream_type; // TODO enum SNAPSHOT : 스냅샷, REALTIME : 실시간
}
