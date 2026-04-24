package com.laser.matching.common.result;

import com.laser.matching.common.codec.MessageHeaderEncoder;
import com.laser.matching.common.codec.UpDownSymbolResultEncoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.agrona.MutableDirectBuffer;

/**
 * 上币 / 下币 结果, resultBizType=SYMBOL_UP|SYMBOL_DOWN
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UpDownSymbolResult extends MatchResult {

    private int symbolCode;
    private long baseCoinId;
    private long quoteCoinId;
    private String symbolId;
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

        enc.symbolCode(symbolCode);
        enc.baseCoinId((int) baseCoinId);
        enc.quoteCoinId((int) quoteCoinId);
        enc.symbolId(symbolId != null ? symbolId : "");
        enc.symbolName(symbolName != null ? symbolName : "");

        return MessageHeaderEncoder.ENCODED_LENGTH + enc.encodedLength();
    }
}
