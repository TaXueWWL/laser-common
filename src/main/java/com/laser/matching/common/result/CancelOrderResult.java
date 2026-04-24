package com.laser.matching.common.result;

import com.laser.matching.common.codec.CancelOrderResultEncoder;
import com.laser.matching.common.codec.CancelReason;
import com.laser.matching.common.codec.MessageHeaderEncoder;
import com.laser.matching.common.codec.OrderStatus;
import com.laser.matching.common.enums.CancelReasonEnum;
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
 * 撤单结果, resultBizType=CANCEL, orderStatus=CANCELLED
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CancelOrderResult extends MatchResult {

    private long orderId;
    private int symbolCode;
    private OrderStatusEnum orderStatus;
    private CancelReasonEnum cancelReason;
    private String symbolId;
    private BigDecimal delegatePrice;
    private BigDecimal delegateCount;
    private BigDecimal remainingAmount;

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        MessageHeaderEncoder header = new MessageHeaderEncoder();
        CancelOrderResultEncoder enc = new CancelOrderResultEncoder();
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
        enc.cancelReason(cancelReason != null
                ? CancelReason.get((short) cancelReason.getCode())
                : CancelReason.NONE);
        enc.symbolId(symbolId != null ? symbolId : "");
        enc.delegatePrice(BigDecimalUtil.defaultToString(delegatePrice));
        enc.delegateCount(BigDecimalUtil.defaultToString(delegateCount));
        enc.remainingAmount(BigDecimalUtil.defaultToString(remainingAmount));

        return MessageHeaderEncoder.ENCODED_LENGTH + enc.encodedLength();
    }
}
