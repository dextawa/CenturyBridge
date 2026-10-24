package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

/**
 * The libraries' 26.1.2 -> 26.2 members a mod can reach that changed (core/segments/damage-lib/26.1.2__26.2.tsv; rows the
 * library itself declares internal are ledgered in lib-internal-gen.json), with the 26.1.2 versions' semantics read off
 * their bytecode.
 * <ul>
 * <li>DFU 9.0.19 -> 10.0.21: DynamicOps.createBoolean / getBooleanValue were defaults (a byte 1/0; a number whose byte
 * value is non-zero) and are abstract now: a 9.0.19-era mod DynamicOps gets the defaults it had.</li>
 * <li>authlib: UserAttributesResponse(privileges, profanityFilterPreferences, banStatus) gained friends and chat preferences,
 * which a 26.1.2 response did not carry: absent (null, as 26.2's own response reads them from JSON that lacks them).</li>
 * </ul>
 */
public final class CbLibMisc {
    private CbLibMisc() {
    }

    public static <T> T createBoolean(DynamicOps<T> self, boolean value) {
        return self.createByte((byte) (value ? 1 : 0));
    }

    public static <T> DataResult<Boolean> getBooleanValue(DynamicOps<T> self, T input) {
        return self.getNumberValue(input).map(number -> number.byteValue() != 0);
    }

    public static UserAttributesResponse userAttributesResponse(UserAttributesResponse.Privileges privileges,
            UserAttributesResponse.ProfanityFilterPreferences profanityFilterPreferences, UserAttributesResponse.BanStatus banStatus) {
        return new UserAttributesResponse(privileges, profanityFilterPreferences, null, null, banStatus);
    }
}
