package com.laser.matching.common.result;

import com.laser.matching.common.codec.MessageHeaderEncoder;
import com.laser.matching.common.codec.OrderStatus;
import com.laser.matching.common.codec.PlaceOrderResultEncoder;
import com.laser.matching.common.enums.OrderStatusEnum;
import com.laser.matching.utils.BigDecimalUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.agrona.MutableDirectBuffer;

import java.math.BigDecimal;

/**
 * 挂单成功结果, resultBizType=PLACE_ORDER, orderStatus=NEW
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PlaceOrderResult extends MatchResult {

    private long orderId;
    private int symbolCode;
    private OrderStatusEnum orderStatus;
    private String symbolId;
    private BigDecimal delegatePrice;
    private BigDecimal delegateCount;

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        MessageHeaderEncoder header = new MessageHeaderEncoder();
        PlaceOrderResultEncoder enc = new PlaceOrderResultEncoder();
        enc.wrapAndApplyHeader(buffer, offset, header);

        enc.header()
                .systemType(systemType.getCode())
                .systemErrorCode(systemErrorCode.getCode())
                .resultBizType(resultBizType.getCode())
                .resultSerialNum(resultSerialNum)
                .requestSerialNum(requestSerialNum)
                .createTime(createTime);

        enc.orderId(orderId);
        enc.symbolCode(symbolCode);
        enc.orderStatus(orderStatus != null
                ? OrderStatus.get((short) orderStatus.getCode())
                : OrderStatus.NULL_VAL);
        enc.symbolId(symbolId != null ? symbolId : "");
        enc.delegatePrice(BigDecimalUtil.defaultToString(delegatePrice));
        enc.delegateCount(BigDecimalUtil.defaultToString(delegateCount));

        return MessageHeaderEncoder.ENCODED_LENGTH + enc.encodedLength();
    }
}
