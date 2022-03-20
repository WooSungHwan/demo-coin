package com.example.democoin.indicator.result;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class RSIs {

    private List<Double> rsi;

    // 현재 낮음
    public boolean isUnder(double value) {
        return rsi.get(0) < value;
    }

    // 현재 높음
    public boolean isOver(double value) {
        return rsi.get(0) > value;
    }

    // 하향돌파
    public boolean isUndering(int value) {
        if(rsi.get(1) > value && rsi.get(0) < value) {
            return true;
        }
        return false;
    }

    // 상향돌파
    public boolean isOvering(int value) {
        if(rsi.get(1) < value && rsi.get(0) > value) {
            return true;
        }
        return false;
    }
}
