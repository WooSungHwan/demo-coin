package com.example.democoin;

import com.example.democoin.backtest.BackTest2;
import com.example.democoin.backtest.repository.AccountCoinWalletRepository;
import com.example.democoin.backtest.repository.ResultInfoJdbcTemplate;
import com.example.democoin.backtest.repository.ResultInfoRepository;
import com.example.democoin.configuration.properties.UpbitProperties;
import com.example.democoin.slack.SlackMessageService;
import com.example.democoin.task.service.ScheduleService;
import com.example.democoin.upbit.client.UpbitAllMarketClient;
import com.example.democoin.upbit.client.UpbitAssetClient;
import com.example.democoin.upbit.client.UpbitCandleClient;
import com.example.democoin.upbit.client.UpbitOrderClient;
import com.example.democoin.upbit.db.entity.FiveMinutesCandle;
import com.example.democoin.upbit.db.entity.Orders;
import com.example.democoin.upbit.db.repository.FiveMinutesCandleRepository;
import com.example.democoin.upbit.db.repository.OrdersRepository;
import com.example.democoin.upbit.enums.MarketType;
import com.example.democoin.upbit.enums.MinuteType;
import com.example.democoin.upbit.enums.OrdSideType;
import com.example.democoin.upbit.enums.OrderStateType;
import com.example.democoin.upbit.request.MarketOrderableRequest;
import com.example.democoin.upbit.request.OrderCancelRequest;
import com.example.democoin.upbit.request.OrderListRequest;
import com.example.democoin.upbit.request.SingleOrderRequest;
import com.example.democoin.upbit.result.accounts.AccountsResult;
import com.example.democoin.upbit.result.candles.MinuteCandle;
import com.example.democoin.upbit.result.market.MarketResult;
import com.example.democoin.upbit.result.orders.MarketOrderableResult;
import com.example.democoin.upbit.result.orders.OrderCancelResult;
import com.example.democoin.upbit.result.orders.OrderResult;
import com.example.democoin.upbit.result.orders.SingleOrderResult;
import com.example.democoin.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static com.example.democoin.upbit.enums.MarketUnit.KRW;
import static com.example.democoin.upbit.enums.OrdSideType.ASK;
import static com.example.democoin.upbit.enums.OrdSideType.BID;

@Slf4j
@SpringBootTest
class DemoCoinApplicationTests {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UpbitProperties upbitProperties;

    @Autowired
    private UpbitAssetClient upbitAssetClient;

    @Autowired
    private UpbitOrderClient upbitOrderClient;

    @Autowired
    private UpbitAllMarketClient upbitAllMarketClient;

    @Autowired
    private UpbitCandleClient upbitCandleClient;

    @Autowired
    private FiveMinutesCandleRepository fiveMinutesCandleRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountCoinWalletRepository accountCoinWalletRepository;

    String accessKey;
    String secretKey;
    String serverUrl;

    @BeforeEach
    void setup() {
        accessKey = upbitProperties.getAccessKey();
        secretKey = upbitProperties.getSecretKey();
        serverUrl = upbitProperties.getServerUrl();
    }

    @Autowired
    private SlackMessageService slackMessageService;

    @Autowired
    private ResultInfoJdbcTemplate resultInfoJdbcTemplate;
    @Autowired
    private ResultInfoRepository resultInfoRepository;

//    @Transactional
    @Test
    void contextLoads() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        List<AccountsResult> accountsResults = ??????????????????();
//        MarketOrderableResult marketOrderableResult = ??????????????????();
//        System.out.println(JsonUtil.toJson(marketOrderableResult));
//        ??????????????????();
//        ??????????????????();
//        ????????????();
//        List<OrderResult> orderList = ??????????????????();
//        ????????????(orderList.get(0).getUuid());

        log.info("======= ???????????? ?????? ?????? =====");
        for (MarketType marketType : MarketType.marketTypeList) {
            log.info("======= {} ?????? =====",marketType.getName());
            ??????_???????????????????????????_??????(MinuteType.FIVE, marketType);
            log.info("======= {} ?????? =====", marketType.getName());
        };
        log.info("======= ???????????? ?????? ?????? =====");


/*
        log.info("======= ???????????? ?????? ?????? =====");
        for (MarketType marketType : MarketType.marketTypeList) {
            log.info("======= {} ?????? =====",marketType.getName());
            ??????_????????????????????????_??????(MinuteType.FIVE, marketType);
            log.info("======= {} ?????? =====", marketType.getName());
        }

        log.info("======= ???????????? ?????? ?????? =====");*/

/*
        double[] bitcoins = {4600000}; //fiveMinutesCandleRepository.findFiveMinutesCandlesByLimit(500);
        RSI rsi = new RSI(14);
        double[] count = rsi.count(bitcoins); //
        System.out.println();
*/

//        List<MinuteCandle> minuteCandles = upbitCandleClient.getMinuteCandles(5, KRW_BTC, 200, LocalDateTime.now().minusMinutes(5));
//        List<Double> prices = minuteCandles.stream().map(MinuteCandle::getTradePrice).collect(Collectors.toList());
//        BollingerBands bollingerBands = Indicator.getBollingerBands(prices);

//        System.out.println();
//        backTesting2();

//        List<FiveMinutesCandle> candles = fiveMinutesCandleRepository.findFiveMinutesCandlesUnderByTimestamp(KRW_BTC.getType(), 1647146998423L);

//        double cci = IndicatorUtil.getCCI(candles, 20);
//        System.out.println();

//        List<MinuteCandle> minuteCandles = upbitCandleClient.getMinuteCandles(5, KRW_BTC, 26, LocalDateTime.now().minusMinutes(5));

//        List<FiveMinutesCandle> candles = fiveMinutesCandleRepository.findFiveMinutesCandlesLimitOffset(KRW_BTC.getType(), LocalDateTime.of(2022,3,1,0,0,0), 100, 0);


//        System.out.println();
//        ????????????();
    }

    @Autowired
    private BackTest2 backTest2;

    private void backTesting2() {
        backTest2.start();
    }

    private void ??????_???????????????????????????_??????(MinuteType minute, MarketType market) throws Exception {
        scheduleService.collectGetCoinCandles(minute, market);
    }

    private void ??????_????????????????????????_??????(MinuteType minuteType, MarketType market) throws IOException, InterruptedException {
        int size = 0;
        LocalDateTime nextTo = LocalDateTime.now().minusMinutes(minuteType.getMinute());
        while (true) {
            long start = System.currentTimeMillis();
            List<MinuteCandle> minuteCandles = ??????(minuteType.getMinute(), market, 200, nextTo);
            size += minuteCandles.size();
            nextTo = minuteCandles.get(minuteCandles.size() - 1).getCandleDateTimeUtc();
            switch (minuteType) {
                case FIVE -> fiveMinutesCandleRepository.saveAll(minuteCandles.stream().map(FiveMinutesCandle::of).toList());
                default -> throw new RuntimeException("There isn`t type : " + minuteType.name());
            }
            long end = System.currentTimeMillis();
            System.out.printf("========== %s??? =========== ????????? : %s\r\n", (end - start) / 1000.0, size);
            Thread.sleep(100);
            if (minuteCandles.size() < 200) {
//            if (size >= 10000) {
                break;
            }
        }
    }

    private List<MinuteCandle> ??????(int minutes, MarketType market, int count, LocalDateTime to) throws IOException {
        return upbitCandleClient.getMinuteCandles(minutes, market, count, to);
    }

    private void ??????????????????() {
        List<MarketResult> allMarketInfo = upbitAllMarketClient.getAllMarketInfo(KRW);
        System.out.println(JsonUtil.toJson(allMarketInfo));
    }

    private void ????????????() throws Exception {
        String market = "KRW-BTC";
        OrderResult orderResultBid = ????????????(market, null, "6000", BID, 1d);// 6000KRW ?????? ???
        SingleOrderResult singleOrderResultBid = ??????????????????(orderResultBid.getUuid());
        System.out.println(JsonUtil.toJson(singleOrderResultBid));
        ordersRepository.save(Orders.of(singleOrderResultBid));
        List<AccountsResult> accountsResults = ??????????????????();
        Optional<String> btcBalance = accountsResults.stream()
                .filter(result -> result.getCurrency().equals("BTC"))
                .map(AccountsResult::getBalance)
                .findFirst();

        if (btcBalance.isPresent()) {
            OrderResult orderResultAsk = ????????????(market, btcBalance.get(), null, ASK, 1d);// ?????? ??????
            SingleOrderResult singleOrderResultAsk = ??????????????????(orderResultAsk.getUuid());
            System.out.println(JsonUtil.toJson(singleOrderResultAsk));
            ordersRepository.save(Orders.of(singleOrderResultAsk));
        }
    }

    private OrderResult ????????????(String market, String volume, String price, OrdSideType ordSideType, Double percent) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return upbitOrderClient.order(market, ordSideType, volume, price);
    }

    private List<AccountsResult> ??????????????????() {
        List<AccountsResult> results = upbitAssetClient.getAllAssets();
        System.out.println(JsonUtil.toJson(results));
        return results;
    }

    private MarketOrderableResult ??????????????????() {
        MarketOrderableRequest request = MarketOrderableRequest.builder()
                .market("KRW-BTC")
                .build();
        return upbitOrderClient.getMargetOrderableInfo(request);
    }

    private List<OrderResult> ??????????????????() {
        OrderListRequest orderRequest = OrderListRequest.builder()
                .state(OrderStateType.WAIT)
                .build();

        List<OrderResult> orderList = upbitOrderClient.getOrderListInfo(orderRequest);
        return orderList;
    }

    private void ????????????(String uuid) throws Exception {
        OrderCancelRequest orderCancelRequest = OrderCancelRequest.builder()
                .uuid(uuid)
                .build();
        OrderCancelResult orderCancelResult = upbitOrderClient.orderCancel(orderCancelRequest);
        System.out.println(orderCancelResult);
    }

    // e9b45c36-2f1a-498d-aaaa-f227ba503f01 ????????????
    // a56d8e29-a739-4395-ab48-64dc8d71d580 ????????????
    private SingleOrderResult ??????????????????(String uuid) {
        SingleOrderRequest request = SingleOrderRequest.builder().uuid(uuid).build();
        return upbitOrderClient.singleOrderRequest(request);
    }

}
