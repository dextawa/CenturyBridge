package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.Optional;
import net.minecraft.class_10319;
import net.minecraft.class_10597;
import net.minecraft.class_2680;
import net.minecraft.class_2746;
import net.minecraft.class_2769;

/**
 * 1.21.4's CreakingHeartBlock.ACTIVE / BlockStateProperties.ACTIVE (one BooleanProperty "active", class_2741.field_55087
 * stored into class_10319.field_55064) over 1.21.5's CreakingHeartBlock.STATE (the same field, now an
 * EnumProperty&lt;CreakingHeartState&gt; "creaking_heart_state": UPROOTED / DORMANT / AWAKE). javap -c both releases, each
 * use of the field side by side:
 * <ul>
 * <li>constructor default: ACTIVE=false / STATE=UPROOTED;</li>
 * <li>getTicker (method_31645), animateTick (method_9496), getAnalogOutputSignal (method_9572): {@code !ACTIVE} /
 *     {@code STATE == UPROOTED};</li>
 * <li>updateState (method_64785): {@code canSummon && !ACTIVE -> ACTIVE=true} / {@code canSummon && STATE == UPROOTED ->
 *     isNaturalNight ? AWAKE : DORMANT}; {@code !canSummon -> ACTIVE=false} / {@code -> UPROOTED}.</li>
 * </ul>
 * So ACTIVE is {@code STATE != UPROOTED}. Reading answers exactly that. Writing false selects UPROOTED; writing true keeps
 * a heart that is already DORMANT or AWAKE as it is, and wakes an UPROOTED one as DORMANT (1.21.5's tick,
 * CreakingHeartBlockEntity.method_66475, then picks AWAKE or DORMANT by night each tick, as it does for every heart; a
 * state write has no level to ask). The property is not part of 1.21.5's state definition: the StateHolder route
 * (mixin/CbCreakingHeartActiveRoute) answers it for creaking-heart states only, and every other state answers as 1.21.5
 * does for a property it lacks. Known loss: StateHolder.getProperties() / getValues() list STATE, not ACTIVE.
 */
public final class CbCreakingHeartActive {

    /** BooleanProperty.create("active"): the 1.21.4 constant, held by the layer */
    public static final class_2746 ACTIVE = class_2746.method_11825("active");

    private CbCreakingHeartActive() {
    }

    /** CreakingHeartBlock.ACTIVE (fieldGetters) */
    public static class_2746 field_55064() {
        return ACTIVE;
    }

    /** BlockStateProperties.ACTIVE (fieldGetters): the same constant, as in 1.21.4 */
    public static class_2746 field_55087() {
        return ACTIVE;
    }

    /** true when {@code property} is the given-back ACTIVE and {@code holder} a creaking-heart state */
    public static boolean applies(Object holder, class_2769<?> property) {
        return property == ACTIVE && holder instanceof class_2680 state && state.method_26204() instanceof class_10319;
    }

    /** ACTIVE of a creaking-heart state */
    public static boolean get(class_2680 state) {
        return state.method_11654(class_10319.field_55064) != class_10597.field_55831;
    }

    /** the state with ACTIVE set */
    public static class_2680 with(class_2680 state, Comparable<?> value) {
        if (!(value instanceof Boolean active)) {
            throw new IllegalArgumentException("Cannot set property " + ACTIVE + " to " + value + " on " + state);
        }
        class_10597 now = state.method_11654(class_10319.field_55064);
        class_10597 wanted = !active ? class_10597.field_55831
            : now == class_10597.field_55831 ? class_10597.field_55832 : now;
        return wanted == now ? state : state.method_11657(class_10319.field_55064, wanted);
    }

    /** getOptionalValue(ACTIVE) */
    public static Optional<Boolean> optional(class_2680 state) {
        return Optional.of(get(state));
    }
}
