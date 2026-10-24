// Ported from ICU (release-71-1): © 2016 and later: Unicode, Inc. and others.
// License & terms of use: http://www.unicode.org/copyright.html
package top.dext.centurybridge.rt.v1_20_2;

import com.ibm.icu.number.FormattedNumber;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.text.PluralRules.FixedDecimal;
import com.ibm.icu.text.PluralRules.Operand;
import top.dext.centurybridge.facade.v1_20_2.IcuFixedDecimalSamples;
import top.dext.centurybridge.facade.v1_20_2.IcuNounClass;

/**
 * ICU 71.1 (1.20.1) -> 73.2 (1.20.2) members a mod can reach that 73.2 dropped (core/segments/damage-lib/1.20.1__1.20.2.tsv),
 * ported from ICU release-71-1 (PluralRules.java, FormattedNumber.java) over 73.2's public API. The 71.1 classes 73.2 dropped
 * are facades: IcuNounClass, IcuFixedDecimalRange, IcuFixedDecimalSamples, IcuPluralSamples.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT) // icu4j is on the client only (artifacts.tsv)
public final class CbLibIcu {
    private CbLibIcu() {
    }

    /** FormattedNumber.getNounClass(): 71.1's mapping of the formatted number's gender (73.2 keeps getGender) */
    public static IcuNounClass getNounClass(FormattedNumber number) {
        String gender = number.getGender();
        if (gender == null || gender.isEmpty()) {
            return IcuNounClass.OTHER;
        }
        switch (gender) {
            case "neuter": return IcuNounClass.NEUTER;
            case "feminine": return IcuNounClass.FEMININE;
            case "masculine": return IcuNounClass.MASCULINE;
            case "animate": return IcuNounClass.ANIMATE;
            case "inanimate": return IcuNounClass.INANIMATE;
            case "personal": return IcuNounClass.PERSONAL;
            case "common": return IcuNounClass.COMMON;
            default: throw new com.ibm.icu.util.UResourceTypeMismatchException("there are noun classes that are not supported yet");
        }
    }

    private static final ClassValue<java.lang.invoke.MethodHandle> SHIFTED_OVERRIDE = new ClassValue<>() {
        @Override
        protected java.lang.invoke.MethodHandle computeValue(Class<?> type) {
            for (Class<?> c = type; c != FixedDecimal.class && c != null; c = c.getSuperclass()) {
                try {
                    java.lang.reflect.Method m = c.getDeclaredMethod("getShiftedValue");
                    if (m.getReturnType() == long.class && !java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                        m.setAccessible(true);
                        return java.lang.invoke.MethodHandles.lookup().unreflect(m);
                    }
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    // not declared here
                }
            }
            return null;
        }
    };

    /**
     * fd.getShiftedValue(): a virtual call in 71.1, so a mod subclass's override of it answers (73.2's FixedDecimal has no
     * such method for the JVM to dispatch to: the override is found by name); otherwise 71.1's body
     */
    public static long getShiftedValue(FixedDecimal fd) {
        java.lang.invoke.MethodHandle override = fd.getClass() == FixedDecimal.class ? null : SHIFTED_OVERRIDE.get(fd.getClass());
        if (override != null) {
            try {
                return (long) override.invoke(fd);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new java.lang.reflect.UndeclaredThrowableException(e);
            }
        }
        return superGetShiftedValue(fd);
    }

    /**
     * super.getShiftedValue() / FixedDecimal's own: 71.1's body; the exponent is the value's e operand (73.2's
     * getPluralOperand(e) answers the exponent field 71.1 read), baseFactor and the digits their 73.2 getters
     */
    public static long superGetShiftedValue(FixedDecimal fd) {
        int exponent = (int) fd.getPluralOperand(Operand.e);
        if (exponent != 0 && fd.getVisibleDecimalDigitCount() == 0 && fd.getDecimalDigits() == 0) {
            // Need to take exponent into account if we have it
            return (long) (fd.getSource() * Math.pow(10, exponent));
        }
        return fd.getIntegerValue() * fd.getBaseFactor() + fd.getDecimalDigits();
    }

    // a mod subclass's super(String): 73.2's FixedDecimal(n, v, f, e, c) with the values 71.1's parse gave (the 5-argument
    // constructor derives every other field from them, as 71.1's copy constructor copied them from the parsed value)

    public static double sampleN(String num) {
        FixedDecimal fd = fixedDecimal(num);
        return fd.isNegative() ? -fd.getSource() : fd.getSource();
    }

    public static int sampleV(String num) {
        return fixedDecimal(num).getVisibleDecimalDigitCount();
    }

    public static long sampleF(String num) {
        return fixedDecimal(num).getDecimalDigits();
    }

    public static int sampleE(String num) {
        return (int) fixedDecimal(num).getPluralOperand(Operand.e);
    }

    /** new FixedDecimal(String): 71.1's parseDecimalSampleRangeNumString, the CLDR sample syntax ("1.5", "1.2c3") */
    public static FixedDecimal fixedDecimal(String num) {
        if (num.contains("e") || num.contains("c")) {
            int ePos = num.lastIndexOf('e');
            if (ePos < 0) {
                ePos = num.lastIndexOf('c');
            }
            int exponent = Integer.parseInt(num.substring(ePos + 1));
            String fractionStr = num.substring(0, ePos);
            return FixedDecimal.createWithExponent(Double.parseDouble(fractionStr), visibleFractionCount(fractionStr), exponent);
        }
        return new FixedDecimal(Double.parseDouble(num), visibleFractionCount(num));
    }

    private static int visibleFractionCount(String value) {
        value = value.trim();
        int decimalPos = value.indexOf('.') + 1;
        return decimalPos == 0 ? 0 : value.length() - decimalPos;
    }

    /** PluralRules.getDecimalSamples(keyword, type): the rule's samples (IcuFixedDecimalSamples.of) */
    public static IcuFixedDecimalSamples getDecimalSamples(PluralRules rules, String keyword, PluralRules.SampleType type) {
        return IcuFixedDecimalSamples.of(rules.getDecimalSamples(keyword, type));
    }
}
