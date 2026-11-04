package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.List;
import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2754;
import net.minecraft.class_2769;
import net.minecraft.class_10319;
import top.dext.centurybridge.step.v1_21_4.facade.CreakingHeartState;

/**
 * 1.21.3's creaking-heart state property on 1.21.4's real block states.
 *
 * <p>V's CreakingHeartBlock.CREAKING and BlockStateProperties.CREAKING were the same EnumProperty, named
 * {@code creaking}. W replaced it with ACTIVE and NATURAL. The restored property is appended to the native state
 * definition, not merely returned as a detached constant. DISABLED maps to native ACTIVE=false; DORMANT and ACTIVE
 * both map to ACTIVE=true so W still supplies a ticker and performs its native night checks. NATURAL is independent
 * and is never inferred from the old value.</p>
 *
 * <p>StateHolder.with and withIfNotDefault are synchronized in both directions by
 * {@code CbRestCreakingHeartStateMixin}. An old-property
 * write wins over the native ACTIVE bit; a native ACTIVE write preserves an existing DORMANT/ACTIVE distinction and
 * enables a previously disabled heart as DORMANT, exactly as V's placement/updateShape path did. The second night
 * check in the native block-entity tick then performs V's DORMANT-to-ACTIVE and ACTIVE-to-DORMANT transitions.</p>
 */
public final class CbRestCreakingHeart {

    private static final class_2754<CreakingHeartState> CREAKING = class_2754.method_11847(
        "creaking", CreakingHeartState.class, List.of(CreakingHeartState.values()));

    private CbRestCreakingHeart() {
    }

    /** CreakingHeartBlock.CREAKING / BlockStateProperties.CREAKING (one shared constant in V). */
    public static class_2754<CreakingHeartState> field_54740() {
        return CREAKING;
    }

    /** The same constant under BlockStateProperties' old field name. */
    public static class_2754<CreakingHeartState> field_54793() {
        return CREAKING;
    }

    /** Called after W's createBlockStateDefinition added AXIS, ACTIVE and NATURAL. */
    public static void appendProperty(class_2689.class_2690<class_2248, class_2680> builder) {
        builder.method_11667(CREAKING);
    }

    /**
     * Reconcile a newly selected creaking-heart state after a StateHolder state-selection call. A write of the
     * restored property pushes it into the native ACTIVE bit; any other write (native ACTIVE, AXIS, NATURAL) pulls the
     * native bit into the restored property, which leaves an already coherent state unchanged. The write is told
     * apart only by the restored property's identity (the layer's own constant), never by the native property's: past
     * 1.21.4 the native ACTIVE bit is given back over another property, so its identity is not the written one.
     * Recursive with calls terminate because the second property already has the required value; all other blocks
     * return unchanged.
     */
    public static class_2680 convertState(class_2680 state, class_2769<?> written) {
        if (!(state.method_26204() instanceof class_10319)) {
            return state;
        }
        boolean nativeActive = state.method_11654(class_10319.field_55064).booleanValue();
        CreakingHeartState oldState = state.method_11654(CREAKING);
        if (written == CREAKING) {
            boolean wanted = oldState != CreakingHeartState.DISABLED;
            if (nativeActive != wanted) {
                return state.method_11657(class_10319.field_55064, Boolean.valueOf(wanted));
            }
        } else if (!nativeActive && oldState != CreakingHeartState.DISABLED) {
            return state.method_11657(CREAKING, CreakingHeartState.DISABLED);
        } else if (nativeActive && oldState == CreakingHeartState.DISABLED) {
            return state.method_11657(CREAKING, CreakingHeartState.DORMANT);
        }
        return state;
    }

    /**
     * V's transitions immediately before CreakingHeartBlockEntity.tick's second canSummonCreaking check. Reaching
     * this point means the heart still has valid pale-oak support and no live bound creaking made the tick return.
     */
    public static void updateDayNight(class_1937 level, class_2338 pos, class_2680 state) {
        if (!(state.method_26204() instanceof class_10319)
                || !state.method_11654(class_10319.field_55064).booleanValue()) {
            return;
        }
        CreakingHeartState wanted = isNaturalNight(level)
            ? CreakingHeartState.ACTIVE : CreakingHeartState.DORMANT;
        if (state.method_11654(CREAKING) != wanted) {
            level.method_8652(pos, state.method_11657(CREAKING, wanted), 3);
        }
    }

    /**
     * CreakingHeartBlock.isNaturalNight(level), 1.21.4's body (javap -c): a natural dimension at night. Asked through
     * the dimension type and Level.isNight, which later releases keep; the static itself is gone at 1.21.11.
     */
    static boolean isNaturalNight(class_1937 level) {
        return level.method_8597().comp_645() && level.method_23886();
    }

    /** V's comparator gate: DORMANT ticks but emits no comparator signal; only ACTIVE does. */
    public static boolean emitsComparatorSignal(class_2680 state) {
        return state.method_11654(CREAKING) == CreakingHeartState.ACTIVE;
    }
}
