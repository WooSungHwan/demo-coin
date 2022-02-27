package com.example.democoin.upbit.request;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class OrderCancelRequest {
    private String uuid;
    private String identifier;

    public String toQueryString() {
        List<String> queryElements = new ArrayList<>();
        if (StringUtils.isNotBlank(uuid)) {
            queryElements.add("uuid="+uuid);
        }

        if (StringUtils.isNotBlank(identifier)) {
            queryElements.add("uuid="+identifier);
        }

        if (queryElements.size() != 1) {
            throw new RuntimeException("uuid 혹은 identifier 파라미터가 필요합니다.");
        }

        return String.join("&", queryElements.toArray(new String[0]));
    }
}
