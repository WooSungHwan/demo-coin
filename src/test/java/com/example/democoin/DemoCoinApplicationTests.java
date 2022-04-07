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
//        List<AccountsResult> accountsResults = 전체계좌조회();
//        MarketOrderableResult marketOrderableResult = 주문가능정보();
//        System.out.println(JsonUtil.toJson(marketOrderableResult));
//        개별주문조회();
//        전체종목조회();
//        주문예제();
//        List<OrderResult> orderList = 주문목록조회();
//        주문취소(orderList.get(0).getUuid());

/*
        log.info("======= 최근일자 수집 시작 =====");
        for (MarketType marketType : MarketType.marketTypeList) {
            log.info("======= {} 시작 =====",marketType.getName());
            오늘_가장최근수집된일자_수집(MinuteType.FIVE, marketType);
            log.info("======= {} 종료 =====", marketType.getName());
        };
        log.info("======= 최근일자 수집 종료 =====");
*/


/*
        log.info("======= 최초일자 수집 시작 =====");
        for (MarketType marketType : MarketType.marketTypeList) {
            log.info("======= {} 시작 =====",marketType.getName());
            오늘_최초캔들생성일자_수집(MinuteType.FIVE, marketType);
            log.info("======= {} 종료 =====", marketType.getName());
        }

        log.info("======= 최초일자 수집 종료 =====");*/

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
//        주문예제();
    }

    @Autowired
    private BackTest2 backTest2;

    private void backTesting2() {
        backTest2.start();
    }

    private void 오늘_가장최근수집된일자_수집(MinuteType minute, MarketType market) throws Exception {
        scheduleService.collectGetCoinCandles(minute, market);
    }

    private void 오늘_최초캔들생성일자_수집(MinuteType minuteType, MarketType market) throws IOException, InterruptedException {
        int size = 0;
        LocalDateTime nextTo = LocalDateTime.now().minusMinutes(minuteType.getMinute());
        while (true) {
            long start = System.currentTimeMillis();
            List<MinuteCandle> minuteCandles = 분봉(minuteType.getMinute(), market, 200, nextTo);
            size += minuteCandles.size();
            nextTo = minuteCandles.get(minuteCandles.size() - 1).getCandleDateTimeUtc();
            switch (minuteType) {
                case FIVE -> fiveMinutesCandleRepository.saveAll(minuteCandles.stream().map(FiveMinutesCandle::of).toList());
                default -> throw new RuntimeException("There isn`t type : " + minuteType.name());
            }
            long end = System.currentTimeMillis();
            System.out.printf("========== %s초 =========== 사이즈 : %s\r\n", (end - start) / 1000.0, size);
            Thread.sleep(100);
            if (minuteCandles.size() < 200) {
//            if (size >= 10000) {
                break;
            }
        }
    }

    private List<MinuteCandle> 분봉(int minutes, MarketType market, int count, LocalDateTime to) throws IOException {
        return upbitCandleClient.getMinuteCandles(minutes, market, count, to);
    }

    private void 전체종목조회() {
        List<MarketResult> allMarketInfo = upbitAllMarketClient.getAllMarketInfo(KRW);
        System.out.println(JsonUtil.toJson(allMarketInfo));
    }

    private void 주문예제() throws Exception {
        String market = "KRW-BTC";
        OrderResult orderResultBid = 주문하기(market, null, "6000", BID, 1d);// 6000KRW 매수 후
        SingleOrderResult singleOrderResultBid = 개별주문조회(orderResultBid.getUuid());
        System.out.println(JsonUtil.toJson(singleOrderResultBid));
        ordersRepository.save(Orders.of(singleOrderResultBid));
        List<AccountsResult> accountsResults = 전체계좌조회();
        Optional<String> btcBalance = accountsResults.stream()
                .filter(result -> result.getCurrency().equals("BTC"))
                .map(AccountsResult::getBalance)
                .findFirst();

        if (btcBalance.isPresent()) {
            OrderResult orderResultAsk = 주문하기(market, btcBalance.get(), null, ASK, 1d);// 전량 매도
            SingleOrderResult singleOrderResultAsk = 개별주문조회(orderResultAsk.getUuid());
            System.out.println(JsonUtil.toJson(singleOrderResultAsk));
            ordersRepository.save(Orders.of(singleOrderResultAsk));
        }
    }

    private OrderResult 주문하기(String market, String volume, String price, OrdSideType ordSideType, Double percent) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return upbitOrderClient.order(market, ordSideType, volume, price);
    }

    private List<AccountsResult> 전체계좌조회() {
        List<AccountsResult> results = upbitAssetClient.getAllAssets();
        System.out.println(JsonUtil.toJson(results));
        return results;
    }

    private MarketOrderableResult 주문가능정보() {
        MarketOrderableRequest request = MarketOrderableRequest.builder()
                .market("KRW-BTC")
                .build();
        return upbitOrderClient.getMargetOrderableInfo(request);
    }

    private List<OrderResult> 주문목록조회() {
        OrderListRequest orderRequest = OrderListRequest.builder()
                .state(OrderStateType.WAIT)
                .build();

        List<OrderResult> orderList = upbitOrderClient.getOrderListInfo(orderRequest);
        return orderList;
    }

    private void 주문취소(String uuid) throws Exception {
        OrderCancelRequest orderCancelRequest = OrderCancelRequest.builder()
                .uuid(uuid)
                .build();
        OrderCancelResult orderCancelResult = upbitOrderClient.orderCancel(orderCancelRequest);
        System.out.println(orderCancelResult);
    }

    // e9b45c36-2f1a-498d-aaaa-f227ba503f01 매수주문
    // a56d8e29-a739-4395-ab48-64dc8d71d580 매도주문
    private SingleOrderResult 개별주문조회(String uuid) {
        SingleOrderRequest request = SingleOrderRequest.builder().uuid(uuid).build();
        return upbitOrderClient.singleOrderRequest(request);
    }

}
