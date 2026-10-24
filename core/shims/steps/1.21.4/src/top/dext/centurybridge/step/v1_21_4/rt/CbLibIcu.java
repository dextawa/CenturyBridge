package top.dext.centurybridge.step.v1_21_4.rt;

import com.ibm.icu.text.PersonNameFormatter;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * ICU 73.2 (1.21.3) -> 76.1 (1.21.4), a client-only library: PersonNameFormatter(locale, patterns) became the 5-argument
 * constructor (locale, given-first patterns, surname-first patterns, given-first locales, surname-first locales). 73.2's
 * built its formatter with the patterns as the given-first ones and left the surname-first patterns and both locale sets
 * null, which is what 76.1's does with nulls there (PersonNameFormatterImpl constructors, bytecode of both).
 */
@Environment(EnvType.CLIENT)
public final class CbLibIcu {
    private CbLibIcu() {
    }

    /** new PersonNameFormatter(locale, patterns) */
    public static PersonNameFormatter personNameFormatter(Locale locale, String[] patterns) {
        return new PersonNameFormatter(locale, patterns, null, null, null);
    }

    /** the 76.1 constructor's arguments 73.2's (locale, patterns) left unset (a subclass's super call) */
    public static String[] noPatterns() {
        return null;
    }
}
