package com.laser.matching.common.result;

import com.laser.matching.common.codec.MessageHeaderEncoder;
import com.laser.matching.common.codec.SymbolOp;
import com.laser.matching.common.codec.UpDownSymbolResultEncoder;
import com.laser.matching.common.enums.SymbolOpEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.agrona.MutableDirectBuffer;

/**
 * 上币 / 下币 结果, resultBizType=SYMBOL_UP|SYMBOL_DOWN
 *
 * <p>{@link #op} 区分本次是上币 (LIST) 还是下币 (DELIST)；下游可以仅订阅一种或两种。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UpDownSymbolResult extends MatchResult {

    private SymbolOpEnum op;
    private int symbolCode;
    private long baseCoinId;
    private long quoteCoinId;
    /** 币对名称 (唯一)，与 symbolCode 一一对应。例: "btc-usdt", "doge-usdt" */
    private String symbolName;

    @Override
    public int encode(MutableDirectBuffer buffer, int offset) {
        MessageHeaderEncoder header = new MessageHeaderEncoder();
        UpDownSymbolResultEncoder enc = new UpDownSymbolResultEncoder();
        enc.wrapAndApplyHeader(buffer, offset, header);

        enc.header()
                .systemType(systemType.getCode())
                .systemErrorCode(systemErrorCode.getCode())
                .resultBizType(resultBizType.getCode())
                .resultSerialNum(resultSerialNum)
                .requestSerialNum(requestSerialNum)
                .createTime(createTime);

        enc.op(op != null ? SymbolOp.get((short) op.getCode()) : SymbolOp.NULL_VAL);
        enc.symbolCode(symbolCode);
        enc.baseCoinId((int) baseCoinId);
        enc.quoteCoinId((int) quoteCoinId);
        enc.symbolName(symbolName != null ? symbolName : "");

        return MessageHeaderEncoder.ENCODED_LENGTH + enc.encodedLength();
    }
}

