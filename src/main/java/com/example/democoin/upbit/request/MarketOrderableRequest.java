package com.example.democoin.upbit.request;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class MarketOrderableRequest {
    private String market; // 마켓 아이디

    public String toQueryString() {
        if (StringUtils.isBlank(market)) {

        }
        List<String> queryElements = new ArrayList<>();
        queryElements.add("market=" + market);
        return String.join("&", queryElements.toArray(new String[0]));
    }

}
