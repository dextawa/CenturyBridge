package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.Optional;

/**
 * Advancement predicates whose records 1.21 widened, each 1.20.6 value carried over exactly:
 * <ul>
 * <li>DamageSourcePredicate gained isDirect, EntityFlagsPredicate isOnGround and isFlying,
 *     LocationPredicate canSeeSky, EntityPredicate movement and periodicTick: 1.20.6's predicate required
 *     none of them ({@link #none});</li>
 * <li>EntityPredicate's location and steppingOnLocation became one LocationWrapper(located, steppingOn,
 *     affectsMovement) -- 1.20.6 had no affectsMovement;</li>
 * <li>PlayerPredicate's Optional&lt;GameType&gt; became a GameTypePredicate (a list of game types): none is
 *     every game type, one is that one;</li>
 * <li>AttributeModifiersPredicate.EntryPredicate's modifier uuid is 1.21's id (CbEntityAttributes'
 *     mapping); 1.21's modifiers have no name and its predicate cannot require one, so a 1.20.6 name
 *     requirement is not carried (the entry matches by attribute, id, amount, operation and slot).</li>
 * </ul>
 */
public final class CbItemPredicates {

    private CbItemPredicates() {
    }

    /** a requirement 1.20.6's predicate did not have (superCtorReshapes push) */
    public static <T> Optional<T> none() {
        return Optional.empty();
    }

    /** EntityPredicate's 1.20.6 location and steppingOnLocation as 1.21's LocationWrapper (superCtorReshapes conv) */
    public static net.minecraft.class_2048.class_9777 locationWrapper(Optional<net.minecraft.class_2090> located,
            Optional<net.minecraft.class_2090> steppingOn) {
        return new net.minecraft.class_2048.class_9777(located, steppingOn, Optional.empty());
    }

    /** EntityPredicate.location() of 1.20.6 (callReshapes ret): the wrapper's located */
    public static Optional<net.minecraft.class_2090> located(net.minecraft.class_2048.class_9777 wrapper) {
        return wrapper.comp_2813();
    }

    /** EntityPredicate.steppingOnLocation() of 1.20.6 (instanceRedirect): the wrapper's steppingOn */
    public static Optional<net.minecraft.class_2090> comp_1764(net.minecraft.class_2048 predicate) {
        return predicate.comp_1763().comp_2814();
    }

    /** PlayerPredicate's 1.20.6 Optional&lt;GameType&gt; as 1.21's GameTypePredicate (superCtorReshapes conv) */
    public static net.minecraft.class_9789 gameTypes(Optional<net.minecraft.class_1934> gameType) {
        return gameType.map(g -> net.minecraft.class_9789.method_60724(g)).orElse(net.minecraft.class_9789.field_52012);
    }

    /** PlayerPredicate.Builder.setGameType(gameType) of 1.20.6 (callReshapes conv) */
    public static net.minecraft.class_9789 gameTypeOf(net.minecraft.class_1934 gameType) {
        return net.minecraft.class_9789.method_60724(gameType);
    }

    /**
     * PlayerPredicate.gameType() of 1.20.6 (callReshapes ret): every game type is no requirement, one is that
     * one; a 1.21 predicate of several (not one 1.20.6 could make) has no single game type to answer, and
     * answers none.
     */
    public static Optional<net.minecraft.class_1934> gameTypeOf(net.minecraft.class_9789 predicate) {
        List<net.minecraft.class_1934> types = predicate.comp_2829();
        return types.size() == 1 ? Optional.of(types.get(0)) : Optional.empty();
    }

    /**
     * The game type a 1.20.6 PlayerPredicate.Builder.setGameType override takes (hook wrap): the one 1.21's
     * predicate names; a 1.20.6 builder cannot take several.
     */
    public static net.minecraft.class_1934 singleGameType(net.minecraft.class_9789 predicate) {
        List<net.minecraft.class_1934> types = predicate.comp_2829();
        if (types.size() != 1) {
            throw new IllegalArgumentException("a 1.20.6 PlayerPredicate.Builder takes one game type; 1.21 asked for " + types);
        }
        return types.get(0);
    }

    /** EntryPredicate's 1.20.6 modifier uuid as 1.21's id (superCtorReshapes conv) */
    public static Optional<net.minecraft.class_2960> modifierId(Optional<java.util.UUID> uuid) {
        return uuid.map(CbEntityAttributes::idOf);
    }

    /** EntryPredicate.id() of 1.20.6 (callReshapes ret, same descriptor): 1.21's id as the uuid */
    public static Optional<java.util.UUID> modifierUuid(Optional<net.minecraft.class_2960> id) {
        return id.map(CbEntityAttributes::uuidOf);
    }

    /** EntryPredicate.name() of 1.20.6 (instanceRedirect): 1.21's predicate requires no name */
    public static Optional<String> comp_2626(net.minecraft.class_9653.class_9654 predicate) {
        return Optional.empty();
    }
}
