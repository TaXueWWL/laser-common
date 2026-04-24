package com.laser.matching.common;

import com.laser.matching.common.enums.SymbolOpEnum;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 控制面命令 SBE 编解码往返测试。
 *
 * <p>注意：UpDownSymbolRequest / TradeSwitchRequest <b>不继承</b> AbstractRequest，无 serialNum 字段。
 */
class ControlCommandRoundTripTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer(256);

    @Test
    void upDownSymbol_listRoundTrip() {
        UpDownSymbolRequest original = UpDownSymbolRequest.builder()
                .op(SymbolOpEnum.LIST)
                .symbolCode(3)
                .symbolName("doge-usdt")
                .baseCoinId(100L)
                .quoteCoinId(200L)
                .build();
        original.encode(buffer, 0);

        UpDownSymbolRequest decoded = new UpDownSymbolRequest().decode(buffer, 0);
        assertEquals(SymbolOpEnum.LIST, decoded.getOp());
        assertEquals(3, decoded.getSymbolCode());
        assertEquals("doge-usdt", decoded.getSymbolName());
        assertEquals(100L, decoded.getBaseCoinId());
        assertEquals(200L, decoded.getQuoteCoinId());
    }

    @Test
    void upDownSymbol_delistRoundTrip() {
        UpDownSymbolRequest original = UpDownSymbolRequest.builder()
                .op(SymbolOpEnum.DELIST)
                .symbolCode(3)
                .symbolName("")
                .build();
        original.encode(buffer, 0);

        UpDownSymbolRequest decoded = new UpDownSymbolRequest().decode(buffer, 0);
        assertEquals(SymbolOpEnum.DELIST, decoded.getOp());
        assertEquals(3, decoded.getSymbolCode());
    }

    @Test
    void tradeSwitch_onRoundTrip() {
        TradeSwitchRequest original = TradeSwitchRequest.builder()
                .symbolCode(3)
                .switchOn(true)
                .build();
        original.encode(buffer, 0);

        TradeSwitchRequest decoded = new TradeSwitchRequest().decode(buffer, 0);
        assertEquals(3, decoded.getSymbolCode());
        assertTrue(decoded.isSwitchOn());
    }

    @Test
    void tradeSwitch_offRoundTrip() {
        TradeSwitchRequest original = TradeSwitchRequest.builder()
                .symbolCode(3)
                .switchOn(false)
                .build();
        original.encode(buffer, 0);

        TradeSwitchRequest decoded = new TradeSwitchRequest().decode(buffer, 0);
        assertFalse(decoded.isSwitchOn());
    }
}
