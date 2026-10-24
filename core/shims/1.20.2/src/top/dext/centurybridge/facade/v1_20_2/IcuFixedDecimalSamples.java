// Ported from ICU (release-71-1): © 2016 and later: Unicode, Inc. and others.
// License & terms of use: http://www.unicode.org/copyright.html
package top.dext.centurybridge.facade.v1_20_2;

import com.ibm.icu.text.PluralRules;
import com.ibm.icu.text.PluralRules.FixedDecimal;
import com.ibm.icu.text.PluralRules.SampleType;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import top.dext.centurybridge.rt.v1_20_2.CbLibIcu;

/**
 * ICU 71.1's PluralRules.FixedDecimalSamples (gone in 73.2, which keeps a rule's samples as DecimalQuantitySamples), its code
 * over 73.2's FixedDecimal. A rule's samples are its 73.2 samples, each written as 73.2 writes the sample
 * (DecimalQuantity.toExponentString, the CLDR sample syntax) and read as 71.1's FixedDecimal(String) read it; one object per
 * 73.2 samples object, as 71.1 answered the rule's own object every time.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT) // icu4j is on the client only (artifacts.tsv)
public class IcuFixedDecimalSamples {
    public final SampleType sampleType;
    public final Set<IcuFixedDecimalRange> samples;
    public final boolean bounded;

    private IcuFixedDecimalSamples(SampleType sampleType, Set<IcuFixedDecimalRange> samples, boolean bounded) {
        this.sampleType = sampleType;
        this.samples = samples;
        this.bounded = bounded;
    }

    private static final Map<PluralRules.DecimalQuantitySamples, IcuFixedDecimalSamples> OF = new WeakHashMap<>();

    /** 73.2's samples of a rule as 71.1's (null stays null: no rule with that keyword) */
    public static IcuFixedDecimalSamples of(PluralRules.DecimalQuantitySamples dq) {
        if (dq == null) {
            return null;
        }
        synchronized (OF) {
            return OF.computeIfAbsent(dq, d -> {
                Set<IcuFixedDecimalRange> samples = new LinkedHashSet<>();
                for (PluralRules.DecimalQuantitySamplesRange r : d.samples) {
                    FixedDecimal start = CbLibIcu.fixedDecimal(r.start.toExponentString());
                    FixedDecimal end = r.end == r.start ? start : CbLibIcu.fixedDecimal(r.end.toExponentString());
                    samples.add(new IcuFixedDecimalRange(start, end));
                }
                return new IcuFixedDecimalSamples(d.sampleType, Collections.unmodifiableSet(samples), d.bounded);
            });
        }
    }

    public Set<Double> addSamples(Set<Double> result) {
        for (IcuFixedDecimalRange item : samples) {
            // we have to convert to longs so we don't get strange double issues
            long startDouble = CbLibIcu.getShiftedValue(item.start);
            long endDouble = CbLibIcu.getShiftedValue(item.end);
            for (long d = startDouble; d <= endDouble; d += 1) {
                result.add(d / (double) item.start.getBaseFactor());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("@").append(sampleType.toString().toLowerCase(Locale.ENGLISH));
        boolean first = true;
        for (IcuFixedDecimalRange item : samples) {
            if (first) {
                first = false;
            } else {
                b.append(",");
            }
            b.append(' ').append(item);
        }
        if (!bounded) {
            b.append(", …");
        }
        return b.toString();
    }

    public Set<IcuFixedDecimalRange> getSamples() {
        return samples;
    }

    public void getStartEndSamples(Set<FixedDecimal> target) {
        for (IcuFixedDecimalRange item : samples) {
            target.add(item.start);
            target.add(item.end);
        }
    }
}
