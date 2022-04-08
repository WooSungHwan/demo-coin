package com.example.democoin.backtest.entity;

import com.example.democoin.backtest.strategy.ask.AskStrategy;
import com.example.democoin.backtest.strategy.bid.BidStrategy;
import com.example.democoin.utils.JsonUtil;
import com.example.democoin.utils.LocalDateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table
@Entity
public class ResultInfo {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Double positivePercent; // 익절률

    private String coinResult; // 코인별 수익률 정보

    @Enumerated(STRING)
    private BidStrategy bidStrategy; // 매수전략

    @Enumerated(STRING)
    private AskStrategy askStrategy; // 매도전략

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Override
    public String toString() {
        return String.format("[%s - %s] 전략 : [ask: %s, bid: %s]익절률 : %s, 코인별 수익정보 : %s"
            , LocalDateTimeUtil.format(startDate)
            , LocalDateTimeUtil.format(endDate)
            , askStrategy.name()
            , bidStrategy.name()
            , positivePercent
            , coinResult
        );
    }
}
