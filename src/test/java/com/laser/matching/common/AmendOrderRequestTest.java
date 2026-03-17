package com.laser.matching.common;

import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class AmendOrderRequestTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer(256);

    @Test
    void encodeAndDecodeFullFields() {
        AmendOrderRequest original = AmendOrderRequest.builder()
                .orderId(987654321L)
                .symbolCode(1)
                .newDelegatePrice(new BigDecimal("51000.50"))
                .newDelegateCount(new BigDecimal("2.75"))
                .build();

        int length = original.encode(buffer, 0);
        Assertions.assertTrue(length > 0, "Encoded length should be positive");

        AmendOrderRequest decoded = new AmendOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(original.getOrderId(), decoded.getOrderId());
        Assertions.assertEquals(original.getSymbolCode(), decoded.getSymbolCode());
        Assertions.assertEquals(0, original.getNewDelegatePrice().compareTo(decoded.getNewDelegatePrice()));
        Assertions.assertEquals(0, original.getNewDelegateCount().compareTo(decoded.getNewDelegateCount()));
    }

    @Test
    void encodeBoundaryValues() {
        AmendOrderRequest original = AmendOrderRequest.builder()
                .orderId(1L)
                .symbolCode(0)
                .newDelegatePrice(new BigDecimal("0.01"))
                .newDelegateCount(new BigDecimal("0.001"))
                .build();

        original.encode(buffer, 0);
        AmendOrderRequest decoded = new AmendOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(1L, decoded.getOrderId());
        Assertions.assertEquals(0, decoded.getSymbolCode());
        Assertions.assertEquals(0, new BigDecimal("0.01").compareTo(decoded.getNewDelegatePrice()));
        Assertions.assertEquals(0, new BigDecimal("0.001").compareTo(decoded.getNewDelegateCount()));
    }

    @Test
    void encodeHighPrecisionBigDecimal() {
        AmendOrderRequest original = AmendOrderRequest.builder()
                .orderId(42L)
                .symbolCode(5)
                .newDelegatePrice(new BigDecimal("88888888.99999999"))
                .newDelegateCount(new BigDecimal("999999.12345"))
                .build();

        original.encode(buffer, 0);
        AmendOrderRequest decoded = new AmendOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(0, original.getNewDelegatePrice().compareTo(decoded.getNewDelegatePrice()));
        Assertions.assertEquals(0, original.getNewDelegateCount().compareTo(decoded.getNewDelegateCount()));
    }

    @Test
    void encodeLargeOrderId() {
        AmendOrderRequest original = AmendOrderRequest.builder()
                .orderId(Long.MAX_VALUE)
                .symbolCode(65535)  // uint16 max in SBE schema
                .newDelegatePrice(new BigDecimal("1.0"))
                .newDelegateCount(new BigDecimal("1.0"))
                .build();

        original.encode(buffer, 0);
        AmendOrderRequest decoded = new AmendOrderRequest().decode(buffer, 0);

        Assertions.assertEquals(Long.MAX_VALUE, decoded.getOrderId());
        Assertions.assertEquals(65535, decoded.getSymbolCode());
    }
}
