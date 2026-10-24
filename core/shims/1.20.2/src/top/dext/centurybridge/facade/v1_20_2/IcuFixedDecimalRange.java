// Ported from ICU (release-71-1): © 2016 and later: Unicode, Inc. and others.
// License & terms of use: http://www.unicode.org/copyright.html
package top.dext.centurybridge.facade.v1_20_2;

import com.ibm.icu.text.PluralRules.FixedDecimal;

/** ICU 71.1's PluralRules.FixedDecimalRange (gone in 73.2), its code over 73.2's FixedDecimal */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT) // icu4j is on the client only (artifacts.tsv)
public class IcuFixedDecimalRange {
    public final FixedDecimal start;
    public final FixedDecimal end;

    public IcuFixedDecimalRange(FixedDecimal start, FixedDecimal end) {
        if (start.getVisibleDecimalDigitCount() != end.getVisibleDecimalDigitCount()) {
            throw new IllegalArgumentException("Ranges must have the same number of visible decimals: " + start + "~" + end);
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return start + (end == start ? "" : "~" + end);
    }
}
