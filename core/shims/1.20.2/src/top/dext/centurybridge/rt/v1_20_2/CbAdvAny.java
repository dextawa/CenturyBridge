package top.dext.centurybridge.rt.v1_20_2;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 1.20.1's "matches anything" predicates on 1.20.2.
 *
 * A 1.20.1 predicate type kept one ANY instance that matched everything and serialized to nothing
 * (JsonNull, which every writer dropped); a field holding it meant "no condition". 1.20.2 turned
 * the predicates into records and says "no condition" with Optional.empty() wherever one is optional.
 * A 1.20.1 mod still reads X.ANY (fieldRedirects point it here) and hands it around as a value of
 * the type, so each type keeps one distinct ANY object (a sentinel, compared by identity): every
 * bridge that hands a 1.20.1 predicate to a 1.20.2 Optional maps that object to Optional.empty(),
 * and every bridge that hands an absent 1.20.2 value back to 1.20.1 code answers it. The sentinels
 * themselves behave as 1.20.1's ANY wherever 1.20.2 code could still meet one (CbAdvAnyMatches*:
 * matches answers true, serializeToJson JsonNull).
 *
 * ContextAwarePredicate.ANY was a predicate with no conditions; 1.20.2 refuses one (at least one
 * condition), so the sentinel carries a single always-true condition (random chance 1.0) that is
 * never evaluated or written: matches / toJson answer for it first.
 */
public final class CbAdvAny {

    /** ContextAwarePredicate.ANY */
    public static final net.minecraft.class_5258 field_24388 =
        net.minecraft.class_5258.method_27973(new net.minecraft.class_219(1.0f));

    /** EntityPredicate.ANY */
    public static final net.minecraft.class_2048 field_9599 = new net.minecraft.class_2048(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    /** ItemPredicate.ANY */
    public static final net.minecraft.class_2073 field_9640 = new net.minecraft.class_2073(Optional.empty(), Optional.empty(),
        net.minecraft.class_2096.class_2100.field_9708, net.minecraft.class_2096.class_2100.field_9708, List.of(), List.of(),
        Optional.empty(), Optional.empty());

    /** LocationPredicate.ANY */
    public static final net.minecraft.class_2090 field_9685 = new net.minecraft.class_2090(Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    /** DamagePredicate.ANY */
    public static final net.minecraft.class_2019 field_9520 = new net.minecraft.class_2019(
        net.minecraft.class_2096.class_2099.field_9705, net.minecraft.class_2096.class_2099.field_9705,
        Optional.empty(), Optional.empty(), Optional.empty());

    /** DamageSourcePredicate.ANY */
    public static final net.minecraft.class_2022 field_9533 =
        new net.minecraft.class_2022(List.of(), Optional.empty(), Optional.empty());

    /** DistancePredicate.ANY */
    public static final net.minecraft.class_2025 field_9553 = new net.minecraft.class_2025(
        net.minecraft.class_2096.class_2099.field_9705, net.minecraft.class_2096.class_2099.field_9705,
        net.minecraft.class_2096.class_2099.field_9705, net.minecraft.class_2096.class_2099.field_9705,
        net.minecraft.class_2096.class_2099.field_9705);

    /** MobEffectsPredicate.ANY */
    public static final net.minecraft.class_2102 field_9709 = new net.minecraft.class_2102(java.util.Map.of());

    /** StatePropertiesPredicate.ANY */
    public static final net.minecraft.class_4559 field_20736 = new net.minecraft.class_4559(List.of());

    /** NbtPredicate.ANY: 1.20.1's held no tag (null), which compares equal to anything */
    public static final net.minecraft.class_2105 field_9716 = new net.minecraft.class_2105((net.minecraft.class_2487) null);

    /** EntityFlagsPredicate.ANY */
    public static final net.minecraft.class_2040 field_9581 = new net.minecraft.class_2040(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    /** EntityEquipmentPredicate.ANY */
    public static final net.minecraft.class_3735 field_16485 = new net.minecraft.class_3735(Optional.empty(),
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    /** BlockPredicate.ANY */
    public static final net.minecraft.class_4550 field_20692 =
        new net.minecraft.class_4550(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    /** FluidPredicate.ANY */
    public static final net.minecraft.class_4551 field_20708 =
        new net.minecraft.class_4551(Optional.empty(), Optional.empty(), Optional.empty());

    /** LightPredicate.ANY */
    public static final net.minecraft.class_4552 field_20712 = new net.minecraft.class_4552(net.minecraft.class_2096.class_2100.field_9708);

    /** EnchantmentPredicate.ANY: no enchantment named, any level */
    public static final net.minecraft.class_2035 field_9571 =
        new net.minecraft.class_2035(Optional.empty(), net.minecraft.class_2096.class_2100.field_9708);

    // ------------------------------------------------------------------ 1.20.1 value -> 1.20.2 Optional

    /** a ContextAwarePredicate where 1.20.2 takes an Optional: ANY (and null) is absent */
    public static Optional<net.minecraft.class_5258> context(net.minecraft.class_5258 p) {
        return p == null || p == field_24388 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2048> entity(net.minecraft.class_2048 p) {
        return p == null || p == field_9599 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2073> item(net.minecraft.class_2073 p) {
        return p == null || p == field_9640 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2090> location(net.minecraft.class_2090 p) {
        return p == null || p == field_9685 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2019> damage(net.minecraft.class_2019 p) {
        return p == null || p == field_9520 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2022> damageSource(net.minecraft.class_2022 p) {
        return p == null || p == field_9533 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2025> distance(net.minecraft.class_2025 p) {
        return p == null || p == field_9553 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2102> effects(net.minecraft.class_2102 p) {
        return p == null || p == field_9709 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_4559> state(net.minecraft.class_4559 p) {
        return p == null || p == field_20736 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2105> nbt(net.minecraft.class_2105 p) {
        return p == null || p == field_9716 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_2040> flags(net.minecraft.class_2040 p) {
        return p == null || p == field_9581 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_3735> equipment(net.minecraft.class_3735 p) {
        return p == null || p == field_16485 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_4550> block(net.minecraft.class_4550 p) {
        return p == null || p == field_20692 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_4551> fluid(net.minecraft.class_4551 p) {
        return p == null || p == field_20708 ? Optional.empty() : Optional.of(p);
    }

    public static Optional<net.minecraft.class_4552> light(net.minecraft.class_4552 p) {
        return p == null || p == field_20712 ? Optional.empty() : Optional.of(p);
    }

    /** a ContextAwarePredicate array where 1.20.2 takes a List (the elements as they are) */
    public static List<net.minecraft.class_5258> contexts(net.minecraft.class_5258[] p) {
        return p == null ? List.of() : List.copyOf(Arrays.asList(p));
    }

    /** an ItemPredicate array where 1.20.2 takes a List */
    public static List<net.minecraft.class_2073> items(net.minecraft.class_2073[] p) {
        return p == null ? List.of() : List.copyOf(Arrays.asList(p));
    }

    // ------------------------------------------------------------------ 1.20.2 Optional -> 1.20.1 value

    /** an absent 1.20.2 player predicate as 1.20.1 code saw it: ContextAwarePredicate.ANY */
    public static net.minecraft.class_5258 contextOrAny(Optional<net.minecraft.class_5258> p) {
        return p.orElse(field_24388);
    }

    public static net.minecraft.class_2048 entityOrAny(Optional<net.minecraft.class_2048> p) {
        return p.orElse(field_9599);
    }

    public static net.minecraft.class_2073 itemOrAny(Optional<net.minecraft.class_2073> p) {
        return p.orElse(field_9640);
    }

    public static net.minecraft.class_2090 locationOrAny(Optional<net.minecraft.class_2090> p) {
        return p.orElse(field_9685);
    }

    public static net.minecraft.class_2019 damageOrAny(Optional<net.minecraft.class_2019> p) {
        return p.orElse(field_9520);
    }

    public static net.minecraft.class_2022 damageSourceOrAny(Optional<net.minecraft.class_2022> p) {
        return p.orElse(field_9533);
    }

    public static net.minecraft.class_2025 distanceOrAny(Optional<net.minecraft.class_2025> p) {
        return p.orElse(field_9553);
    }

    public static net.minecraft.class_2102 effectsOrAny(Optional<net.minecraft.class_2102> p) {
        return p.orElse(field_9709);
    }

    public static net.minecraft.class_4559 stateOrAny(Optional<net.minecraft.class_4559> p) {
        return p.orElse(field_20736);
    }

    public static net.minecraft.class_2105 nbtOrAny(Optional<net.minecraft.class_2105> p) {
        return p.orElse(field_9716);
    }

    public static net.minecraft.class_2040 flagsOrAny(Optional<net.minecraft.class_2040> p) {
        return p.orElse(field_9581);
    }

    public static net.minecraft.class_3735 equipmentOrAny(Optional<net.minecraft.class_3735> p) {
        return p.orElse(field_16485);
    }

    public static net.minecraft.class_4550 blockOrAny(Optional<net.minecraft.class_4550> p) {
        return p.orElse(field_20692);
    }

    public static net.minecraft.class_4551 fluidOrAny(Optional<net.minecraft.class_4551> p) {
        return p.orElse(field_20708);
    }

    public static net.minecraft.class_4552 lightOrAny(Optional<net.minecraft.class_4552> p) {
        return p.orElse(field_20712);
    }

    /** a 1.20.2 List of player predicates as 1.20.1's array */
    public static net.minecraft.class_5258[] contextArray(List<net.minecraft.class_5258> p) {
        return p.toArray(new net.minecraft.class_5258[0]);
    }

    /** a 1.20.2 List of item predicates as 1.20.1's array */
    public static net.minecraft.class_2073[] itemArray(List<net.minecraft.class_2073> p) {
        return p.toArray(new net.minecraft.class_2073[0]);
    }

    // ------------------------------------------------------------------ 1.20.1 operations that special-cased ANY

    /**
     * EntityPredicate.wrap(entity), 1.20.1: ANY is ContextAwarePredicate.ANY; any other predicate a
     * one-condition player predicate (entity_properties of "this"), as 1.20.2's wrap builds it.
     */
    public static net.minecraft.class_5258 method_51704(net.minecraft.class_2048 entity) {
        return entity == field_9599 ? field_24388 : net.minecraft.class_2048.method_51704(entity);
    }

    /**
     * ContextAwarePredicate.create(conditions), 1.20.1: no conditions is a predicate that matches
     * anything and writes nothing -- ANY's behaviour, and 1.20.2 refuses an empty predicate.
     */
    public static net.minecraft.class_5258 method_27973(net.minecraft.class_5341... conditions) {
        return conditions.length == 0 ? field_24388 : net.minecraft.class_5258.method_27973(conditions);
    }

    private CbAdvAny() {
    }
}
