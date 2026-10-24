package cb.slib;

import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.text.PluralRules.FixedDecimal;
import com.ibm.icu.text.PluralRules.FixedDecimalRange;
import com.ibm.icu.text.PluralRules.FixedDecimalSamples;
import com.ibm.icu.text.PluralRules.KeywordStatus;
import com.ibm.icu.text.PluralRules.SampleType;
import com.ibm.icu.text.PluralSamples;
import com.ibm.icu.util.NounClass;
import com.ibm.icu.util.ULocale;
import java.util.TreeSet;

/** ICU 71.1-era uses, converted 1.20.1 -> 1.20.2 and run on icu4j 73.2. Prints "CB-SLIB ok|FAIL ..." lines. */
public class IcuHarness {
    static int checks, fails;

    static void check(boolean ok, String what) {
        checks++;
        if (!ok) fails++;
        System.out.println("CB-SLIB " + (ok ? "ok   " : "FAIL ") + what);
    }

    static class Shifted extends FixedDecimal {
        Shifted(String s) {
            super(s);
        }

        @Override
        public long getShiftedValue() {
            return super.getShiftedValue() + 1000;
        }
    }

    static class MyRange extends FixedDecimalRange {
        MyRange(FixedDecimal a, FixedDecimal b) {
            super(a, b);
        }
    }

    public static void main(String[] args) {
        check(NounClass.values().length == 8 && NounClass.valueOf("COMMON").ordinal() == 7, "NounClass constants");
        check(NumberFormatter.withLocale(ULocale.ENGLISH).format(5).getNounClass() == NounClass.OTHER, "FormattedNumber.getNounClass");
        FixedDecimal fd = new FixedDecimal("1.5");
        check(fd.getShiftedValue() == 15 && fd.getVisibleDecimalDigitCount() == 1, "new FixedDecimal(\"1.5\").getShiftedValue()");
        check(new FixedDecimal("1.2c3").getShiftedValue() == 12, "exponent sample 1.2c3 (71.1: v=1, so integer*10+f)");
        Shifted s = new Shifted("2.25");
        check(s.getVisibleDecimalDigitCount() == 2 && s.getShiftedValue() == 1225, "subclass super(String) and super.getShiftedValue()");
        PluralRules rules = PluralRules.forLocale(ULocale.ENGLISH);
        FixedDecimalSamples one = rules.getDecimalSamples("one", SampleType.INTEGER);
        check("@integer 1".equals(one.toString()), "getDecimalSamples(one, INTEGER) = " + one);
        check(one == rules.getDecimalSamples("one", SampleType.INTEGER), "the same samples object each time");
        FixedDecimalSamples other = rules.getDecimalSamples("other", SampleType.DECIMAL);
        check(other != null && !other.bounded && other.addSamples(new TreeSet<>()).contains(0.5), "other DECIMAL samples " + other);
        TreeSet<FixedDecimal> ends = new TreeSet<>();
        one.getStartEndSamples(ends);
        check(ends.size() == 1 && one.getSamples().iterator().next().start.intValue() == 1, "getStartEndSamples / getSamples / start");
        MyRange r = new MyRange(new FixedDecimal("1.0"), new FixedDecimal("2.0"));
        check("1.0~2.0".equals(r.toString()) && r.end.doubleValue() == 2.0, "FixedDecimalRange subclass " + r);
        PluralSamples ps = new PluralSamples(rules);
        check(ps.getStatus("one", 0, null, null) == KeywordStatus.UNIQUE && ps._keyLimitedMap.get("one"), "PluralSamples.getStatus / _keyLimitedMap");
        System.out.println("CB-SLIB DONE " + (checks - fails) + "/" + checks);
    }
}
