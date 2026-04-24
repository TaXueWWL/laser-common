package com.laser.matching.common;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证 serialNum 在 SBE 编解码往返中保持完整。
 */
class RequestSerialNumRoundTripTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer(256);

    @Test
    void cancelRequestRoundTripPreservesSerialNum() {
        CancelOrderRequest original = CancelOrderRequest.builder()
                .serialNum(12345L)
                .orderId(999L)
                .symbolCode(7)
                .build();

        original.encode(buffer, 0);

        CancelOrderRequest decoded = new CancelOrderRequest().decode(buffer, 0);

        assertEquals(12345L, decoded.getSerialNum());
        assertEquals(999L, decoded.getOrderId());
        assertEquals(7, decoded.getSymbolCode());
    }

    @Test
    void serialNumDefaultsToZero() {
        CancelOrderRequest req = CancelOrderRequest.builder().orderId(1L).symbolCode(1).build();
        req.encode(buffer, 0);
        CancelOrderRequest decoded = new CancelOrderRequest().decode(buffer, 0);
        assertEquals(0L, decoded.getSerialNum(),
                "未设置 serialNum 时应为 0 (服务端会拒绝，因为预期从 1 开始)");
    }
}
