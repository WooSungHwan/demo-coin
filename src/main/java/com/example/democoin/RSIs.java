package com.example.democoin;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class RSIs {

    private List<Double> rsi;

    public boolean isUnder(double value) {
        return rsi.get(0) < value;
    }

    public boolean isOver(double value) {
        return rsi.get(0) > value;
    }
}
