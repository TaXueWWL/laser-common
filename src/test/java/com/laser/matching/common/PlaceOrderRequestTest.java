package com.laser.matching.common;

import com.laser.matching.common.enums.OrderSideEnum;
import com.laser.matching.common.enums.OrderType;
import com.laser.matching.common.enums.StpStrategyEnum;
import com.laser.matching.common.enums.TimeInForceEnum;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;


class PlaceOrderRequestTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer(512);

    @Test
    void encodeAndDecodeFullFields() {
        PlaceOrderRequest original = PlaceOrderRequest.builder()
                .orderId(1234567890L)
                .clientOid("client-order-001")
                .accountId(88001L)
                .symbolCode(1)
                .orderType(OrderType.LIMIT)
                .orderSide(OrderSideEnum.BUY)
                .timeInForce(TimeInForceEnum.GTC)
                .delegatePrice(new BigDecimal("50123.45"))
                .delegateCount(new BigDecimal("3.50"))
                .stpAccountId(88001L)
                .stpStrategyEnum(StpStrategyEnum.CANCEL_MAKER)
                .build();

        int length = original.encode(buffer, 0);
        Assertions.assertTrue(length > 0, "Encoded length should be positive");

        PlaceOrderRequest decoded = new PlaceOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(original.getOrderId(), decoded.getOrderId());
        Assertions.assertEquals(original.getClientOid(), decoded.getClientOid());
        Assertions.assertEquals(original.getAccountId(), decoded.getAccountId());
        Assertions.assertEquals(original.getSymbolCode(), decoded.getSymbolCode());
        Assertions.assertEquals(original.getOrderType(), decoded.getOrderType());
        Assertions.assertEquals(original.getOrderSide(), decoded.getOrderSide());
        Assertions.assertEquals(original.getTimeInForce(), decoded.getTimeInForce());
        Assertions.assertEquals(0, original.getDelegatePrice().compareTo(decoded.getDelegatePrice()));
        Assertions.assertEquals(0, original.getDelegateCount().compareTo(decoded.getDelegateCount()));
        Assertions.assertEquals(original.getStpAccountId(), decoded.getStpAccountId());
        Assertions.assertEquals(original.getStpStrategyEnum(), decoded.getStpStrategyEnum());
    }

    @Test
    void encodeSellOrderWithMarketType() {
        PlaceOrderRequest original = PlaceOrderRequest.builder()
                .orderId(9999L)
                .clientOid("market-sell-01")
                .accountId(100L)
                .symbolCode(2)
                .orderType(OrderType.MARKET)
                .orderSide(OrderSideEnum.SELL)
                .timeInForce(TimeInForceEnum.IOC)
                .delegatePrice(BigDecimal.ZERO)
                .delegateCount(new BigDecimal("10.0"))
                .stpAccountId(100L)
                .stpStrategyEnum(StpStrategyEnum.CANCEL_BOTH)
                .build();

        int length = original.encode(buffer, 0);
        Assertions.assertTrue(length > 0);

        PlaceOrderRequest decoded = new PlaceOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(OrderType.MARKET, decoded.getOrderType());
        Assertions.assertEquals(OrderSideEnum.SELL, decoded.getOrderSide());
        Assertions.assertEquals(TimeInForceEnum.IOC, decoded.getTimeInForce());
        Assertions.assertEquals(StpStrategyEnum.CANCEL_BOTH, decoded.getStpStrategyEnum());
    }

    @Test
    void encodeFokAndPostOnlyTimeInForce() {
        PlaceOrderRequest fokOrder = PlaceOrderRequest.builder()
                .orderId(1001L)
                .clientOid("fok-01")
                .accountId(200L)
                .symbolCode(1)
                .orderType(OrderType.LIMIT)
                .orderSide(OrderSideEnum.BUY)
                .timeInForce(TimeInForceEnum.FOK)
                .delegatePrice(new BigDecimal("100.00"))
                .delegateCount(new BigDecimal("5.0"))
                .stpAccountId(200L)
                .stpStrategyEnum(StpStrategyEnum.DEFAULT)
                .build();

        fokOrder.encode(buffer, 0);
        PlaceOrderRequest decodedFok = new PlaceOrderRequest().decode(buffer, 0);
        Assertions.assertEquals(TimeInForceEnum.FOK, decodedFok.getTimeInForce());

        PlaceOrderRequest postOnly = PlaceOrderRequest.builder()
                .orderId(1002L)
                .clientOid("post-only-01")
                .accountId(200L)
                .symbolCode(1)
                .orderType(OrderType.LIMIT)
                .orderSide(OrderSideEnum.SELL)
                .timeInForce(TimeInForceEnum.POST_ONLY)
                .delegatePrice(new BigDecimal("101.00"))
                .delegateCount(new BigDecimal("2.0"))
                .stpAccountId(200L)
                .stpStrategyEnum(StpStrategyEnum.CANCEL_TAKER)
                .build();

        postOnly.encode(buffer, 0);
        PlaceOrderRequest decodedPostOnly = new PlaceOrderRequest().decode(buffer, 0);
        Assertions.assertEquals(TimeInForceEnum.POST_ONLY, decodedPostOnly.getTimeInForce());
        Assertions.assertEquals(StpStrategyEnum.CANCEL_TAKER, decodedPostOnly.getStpStrategyEnum());
    }

    @Test
    void encodeBoundaryValues() {
        PlaceOrderRequest original = PlaceOrderRequest.builder()
                .orderId(Long.MAX_VALUE)
                .clientOid("")
                .accountId(0L)
                .symbolCode(0)
                .orderType(OrderType.LIMIT)
                .orderSide(OrderSideEnum.BUY)
                .timeInForce(TimeInForceEnum.GTC)
                .delegatePrice(new BigDecimal("0.01"))
                .delegateCount(new BigDecimal("0.001"))
                .stpAccountId(Long.MAX_VALUE)
                .stpStrategyEnum(StpStrategyEnum.DEFAULT)
                .build();

        int length = original.encode(buffer, 0);
        Assertions.assertTrue(length > 0);

        PlaceOrderRequest decoded = new PlaceOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(Long.MAX_VALUE, decoded.getOrderId());
        Assertions.assertEquals("", decoded.getClientOid());
        Assertions.assertEquals(0L, decoded.getAccountId());
        Assertions.assertEquals(0, decoded.getSymbolCode());
        Assertions.assertEquals(0, new BigDecimal("0.01").compareTo(decoded.getDelegatePrice()));
        Assertions.assertEquals(0, new BigDecimal("0.001").compareTo(decoded.getDelegateCount()));
        Assertions.assertEquals(Long.MAX_VALUE, decoded.getStpAccountId());
    }

    @Test
    void encodeHighPrecisionBigDecimal() {
        PlaceOrderRequest original = PlaceOrderRequest.builder()
                .orderId(555L)
                .clientOid("precision-test")
                .accountId(300L)
                .symbolCode(3)
                .orderType(OrderType.LIMIT)
                .orderSide(OrderSideEnum.BUY)
                .timeInForce(TimeInForceEnum.GTC)
                .delegatePrice(new BigDecimal("99999999.12345678"))
                .delegateCount(new BigDecimal("123456.789"))
                .stpAccountId(300L)
                .stpStrategyEnum(StpStrategyEnum.DEFAULT)
                .build();

        original.encode(buffer, 0);
        PlaceOrderRequest decoded = new PlaceOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(0, original.getDelegatePrice().compareTo(decoded.getDelegatePrice()));
        Assertions.assertEquals(0, original.getDelegateCount().compareTo(decoded.getDelegateCount()));
    }
}
