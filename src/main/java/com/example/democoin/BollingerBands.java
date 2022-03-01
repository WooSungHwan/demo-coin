package com.example.democoin;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value(staticConstructor = "of")
public class BollingerBands {

    private List<BigDecimal> udd;

    private List<BigDecimal> mdd;

    private List<BigDecimal> ldd;

}
