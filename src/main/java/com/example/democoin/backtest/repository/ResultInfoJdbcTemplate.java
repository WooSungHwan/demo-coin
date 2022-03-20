package com.example.democoin.backtest.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ResultInfoJdbcTemplate {

    private final JdbcTemplate jdbcTemplate;

    public String getResultInfo() {
        String query = "with RESULT AS (select ouu.market, sum(ouu.proceeds) - (select sum(inn.fee) from back_test_orders inn where inn.market = ouu.market) proceeds from back_test_orders ouu where side = 'ask' group by market)SELECT JSON_ARRAYAGG(JSON_OBJECT('market', market, 'proceeds', proceeds)) from RESULT";
        return jdbcTemplate.queryForObject(query, String.class);
    }

    public Double getPositivePercent() {
        String query = "select (select count(*) from back_test_orders where side = 'ask' and proceed_rate > 0) / (select count(*) from back_test_orders where side = 'ask') * 100";
        return jdbcTemplate.queryForObject(query, Double.class);
    }

}
