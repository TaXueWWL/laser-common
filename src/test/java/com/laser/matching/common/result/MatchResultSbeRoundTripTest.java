package com.laser.matching.common.result;

import com.laser.matching.common.codec.MatchOrderResultDecoder;
import com.laser.matching.common.codec.MessageHeaderDecoder;
import com.laser.matching.common.codec.PlaceOrderResultDecoder;
import com.laser.matching.common.codec.TradeSwitchResultDecoder;
import com.laser.matching.common.codec.UpDownSymbolResultDecoder;
import com.laser.matching.common.enums.CancelReasonEnum;
import com.laser.matching.common.enums.OrderStatusEnum;
import com.laser.matching.common.enums.ResultBizTypeEnum;
import com.laser.matching.common.enums.SystemErrorCodeEnum;
import com.laser.matching.common.enums.SystemTypeEnum;
import com.laser.matching.common.codec.CancelOrderResultDecoder;
import com.laser.matching.utils.BigDecimalUtil;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证 5 个 MatchResult SBE 编码后能被对应 Decoder 还原。
 */
class MatchResultSbeRoundTripTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer(512);

    @Test
    void placeOrderResultRoundTrip() {
        PlaceOrderResult original = PlaceOrderResult.builder()
                .systemType(SystemTypeEnum.NORMAL)
                .systemErrorCode(SystemErrorCodeEnum.NONE)
                .resultBizType(ResultBizTypeEnum.PLACE_ORDER)
                .resultSerialNum(42L)
                .requestSerialNum(7L)
                .createTime(1700000000000L)
                .orderId(1234567L)
                .symbolCode(101)
                .symbolId("BTC_USDT")
                .orderStatus(OrderStatusEnum.NEW)
                .delegatePrice(new BigDecimal("50000.5"))
                .delegateCount(new BigDecimal("0.001"))
                .build();

        original.encode(buffer, 0);

        MessageHeaderDecoder header = new MessageHeaderDecoder();
        PlaceOrderResultDecoder dec = new PlaceOrderResultDecoder();
        dec.wrapAndApplyHeader(buffer, 0, header);

        assertEquals((short) SystemTypeEnum.NORMAL.getCode(), dec.header().systemType());
        assertEquals((short) ResultBizTypeEnum.PLACE_ORDER.getCode(), dec.header().resultBizType());
        assertEquals(42L, dec.header().resultSerialNum());
        assertEquals(7L, dec.header().requestSerialNum());
        assertEquals(1700000000000L, dec.header().createTime());
        assertEquals(1234567L, dec.orderId());
        assertEquals(101, dec.symbolCode());
        assertEquals("BTC_USDT", dec.symbolId());
        assertEquals(new BigDecimal("50000.5"), BigDecimalUtil.stringToBigDecimal(dec.delegatePrice()));
        assertEquals(new BigDecimal("0.001"), BigDecimalUtil.stringToBigDecimal(dec.delegateCount()));
    }

    @Test
    void matchOrderResultRoundTrip() {
        MatchOrderResult original = MatchOrderResult.builder()
                .systemType(SystemTypeEnum.NORMAL)
                .systemErrorCode(SystemErrorCodeEnum.NONE)
                .resultBizType(ResultBizTypeEnum.MATCH)
                .resultSerialNum(100L)
                .requestSerialNum(20L)
                .createTime(1700000001000L)
                .orderId(1L)
                .oppositeOrderId(2L)
                .symbolCode(101)
                .orderStatus(OrderStatusEnum.PARTIALLY_FILLED)
                .symbolId("ETH_USDT")
                .tradePrice(new BigDecimal("3000"))
                .counterTradePrice(new BigDecimal("3000"))
                .tradeAmount(new BigDecimal("0.5"))
                .counterTradeAmount(new BigDecimal("0.5"))
                .remainingAmount(new BigDecimal("0.5"))
                .build();

        original.encode(buffer, 0);

        MessageHeaderDecoder header = new MessageHeaderDecoder();
        MatchOrderResultDecoder dec = new MatchOrderResultDecoder();
        dec.wrapAndApplyHeader(buffer, 0, header);

        assertEquals(100L, dec.header().resultSerialNum());
        assertEquals(1L, dec.orderId());
        assertEquals(2L, dec.oppositeOrderId());
        // 必须按 SBE schema 声明顺序读取所有 varData，否则游标错位
        assertEquals("ETH_USDT", dec.symbolId());
        assertEquals(new BigDecimal("3000"), BigDecimalUtil.stringToBigDecimal(dec.tradePrice()));
        assertEquals(new BigDecimal("3000"), BigDecimalUtil.stringToBigDecimal(dec.counterTradePrice()));
        assertEquals(new BigDecimal("0.5"), BigDecimalUtil.stringToBigDecimal(dec.tradeAmount()));
        assertEquals(new BigDecimal("0.5"), BigDecimalUtil.stringToBigDecimal(dec.counterTradeAmount()));
        assertEquals(new BigDecimal("0.5"), BigDecimalUtil.stringToBigDecimal(dec.remainingAmount()));
    }

    @Test
    void cancelOrderResultRoundTrip() {
        CancelOrderResult original = CancelOrderResult.builder()
                .systemType(SystemTypeEnum.NORMAL)
                .systemErrorCode(SystemErrorCodeEnum.NONE)
                .resultBizType(ResultBizTypeEnum.CANCEL)
                .resultSerialNum(55L)
                .requestSerialNum(11L)
                .createTime(1700000002000L)
                .orderId(7L)
                .symbolCode(200)
                .orderStatus(OrderStatusEnum.CANCELLED)
                .cancelReason(CancelReasonEnum.STP_CANCEL)
                .symbolId("BTC_USDT")
                .delegatePrice(new BigDecimal("100"))
                .delegateCount(new BigDecimal("1"))
                .remainingAmount(new BigDecimal("0.3"))
                .build();

        original.encode(buffer, 0);

        MessageHeaderDecoder header = new MessageHeaderDecoder();
        CancelOrderResultDecoder dec = new CancelOrderResultDecoder();
        dec.wrapAndApplyHeader(buffer, 0, header);

        assertEquals(55L, dec.header().resultSerialNum());
        assertEquals(7L, dec.orderId());
        assertEquals((short) CancelReasonEnum.STP_CANCEL.getCode(), dec.cancelReason().value());
        // 按 SBE schema 顺序读取 varData
        assertEquals("BTC_USDT", dec.symbolId());
        assertEquals(new BigDecimal("100"), BigDecimalUtil.stringToBigDecimal(dec.delegatePrice()));
        assertEquals(new BigDecimal("1"), BigDecimalUtil.stringToBigDecimal(dec.delegateCount()));
        assertEquals(new BigDecimal("0.3"), BigDecimalUtil.stringToBigDecimal(dec.remainingAmount()));
    }

    @Test
    void upDownSymbolResultRoundTrip() {
        UpDownSymbolResult original = UpDownSymbolResult.builder()
                .systemType(SystemTypeEnum.NORMAL)
                .systemErrorCode(SystemErrorCodeEnum.NONE)
                .resultBizType(ResultBizTypeEnum.SYMBOL_UP)
                .resultSerialNum(33L)
                .requestSerialNum(5L)
                .createTime(1700000003000L)
                .op(com.laser.matching.common.enums.SymbolOpEnum.LIST)
                .symbolCode(300)
                .baseCoinId(1L)
                .quoteCoinId(2L)
                .symbolName("ABC/USDT")
                .build();

        original.encode(buffer, 0);

        MessageHeaderDecoder header = new MessageHeaderDecoder();
        UpDownSymbolResultDecoder dec = new UpDownSymbolResultDecoder();
        dec.wrapAndApplyHeader(buffer, 0, header);

        assertEquals(33L, dec.header().resultSerialNum());
        assertEquals(300, dec.symbolCode());
        assertEquals(1L, dec.baseCoinId());
        assertEquals(2L, dec.quoteCoinId());
        assertEquals("ABC/USDT", dec.symbolName());
    }

    @Test
    void tradeSwitchResultRoundTrip() {
        TradeSwitchResult original = TradeSwitchResult.builder()
                .systemType(SystemTypeEnum.NORMAL)
                .systemErrorCode(SystemErrorCodeEnum.NONE)
                .resultBizType(ResultBizTypeEnum.TRADE_SWITCH)
                .resultSerialNum(77L)
                .requestSerialNum(12L)
                .createTime(1700000004000L)
                .symbolCode(400)
                .switchOn(true)
                .symbolId("XYZ_USDT")
                .build();

        original.encode(buffer, 0);

        MessageHeaderDecoder header = new MessageHeaderDecoder();
        TradeSwitchResultDecoder dec = new TradeSwitchResultDecoder();
        dec.wrapAndApplyHeader(buffer, 0, header);

        assertEquals(77L, dec.header().resultSerialNum());
        assertEquals(400, dec.symbolCode());
        assertEquals((short) 1, dec.switchOn().value());  // BooleanType.TRUE = 1
        assertEquals("XYZ_USDT", dec.symbolId());
    }
}
