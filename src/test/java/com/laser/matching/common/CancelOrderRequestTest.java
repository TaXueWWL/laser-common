package com.laser.matching.common;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class CancelOrderRequestTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer(128);

    @Test
    void encodeAndDecodeFullFields() {
        CancelOrderRequest original = CancelOrderRequest.builder()
                .orderId(1234567890L)
                .symbolCode(1)
                .build();

        int length = original.encode(buffer, 0);
        Assertions.assertTrue(length > 0, "Encoded length should be positive");

        CancelOrderRequest decoded = new CancelOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(original.getOrderId(), decoded.getOrderId());
        Assertions.assertEquals(original.getSymbolCode(), decoded.getSymbolCode());
    }

    @Test
    void encodeBoundaryValues() {
        CancelOrderRequest original = CancelOrderRequest.builder()
                .orderId(0L)
                .symbolCode(0)
                .build();

        original.encode(buffer, 0);
        CancelOrderRequest decoded = new CancelOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(0L, decoded.getOrderId());
        Assertions.assertEquals(0, decoded.getSymbolCode());
    }

    @Test
    void encodeLargeValues() {
        CancelOrderRequest original = CancelOrderRequest.builder()
                .orderId(Long.MAX_VALUE)
                .symbolCode(65535)  // uint16 max in SBE schema
                .build();

        original.encode(buffer, 0);
        CancelOrderRequest decoded = new CancelOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(Long.MAX_VALUE, decoded.getOrderId());
        Assertions.assertEquals(65535, decoded.getSymbolCode());
    }

    @Test
    void encodeMultipleTimesReusesBuffer() {
        CancelOrderRequest first = CancelOrderRequest.builder()
                .orderId(111L)
                .symbolCode(1)
                .build();

        first.encode(buffer, 0);

        CancelOrderRequest second = CancelOrderRequest.builder()
                .orderId(222L)
                .symbolCode(2)
                .build();

        second.encode(buffer, 0);

        // Decode should return the second (latest) values
        CancelOrderRequest decoded = new CancelOrderRequest().decode(buffer, 0);
        Assertions.assertEquals(222L, decoded.getOrderId());
        Assertions.assertEquals(2, decoded.getSymbolCode());
    }
}
