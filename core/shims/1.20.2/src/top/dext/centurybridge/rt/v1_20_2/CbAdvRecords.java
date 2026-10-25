package top.dext.centurybridge.rt.v1_20_2;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 1.20.1's advancement predicate constructors on 1.20.2's records.
 *
 * 1.20.1 built each predicate from nullable parts (a tag or a block set that may be null, a Boolean
 * that may be null for "either", a nested predicate that may be ANY); 1.20.2's record takes the same
 * parts as Optionals, sets as HolderSets, arrays as Lists. The converters here take a 1.20.1 part as a
 * 1.20.1 constructor got it and answer what the 1.20.2 constructor takes for the same meaning: null
 * (and a nested ANY, CbAdvAny) is absent. superCtorReshapes run them at a mod's new X(..) and at the
 * super(..) of a mod's subclass, whose object is then a real 1.20.2 record (made extendable by the
 * layer's access widener).
 */
public final class CbAdvRecords {

    // ------------------------------------------------------------------ nullable parts

    /** a Boolean condition of 1.20.1 (null: either) */
    public static Optional<Boolean> bool(Boolean b) {
        return Optional.ofNullable(b);
    }

    /** a tag of 1.20.1 (null: no tag condition) */
    public static Optional<net.minecraft.class_6862<?>> tag(net.minecraft.class_6862<?> tag) {
        return Optional.ofNullable(tag);
    }

    /** a resource key condition of 1.20.1 (null: any) */
    public static Optional<net.minecraft.class_5321<?>> key(net.minecraft.class_5321<?> key) {
        return Optional.ofNullable(key);
    }

    /** BlockPredicate's block set (null: any block) as the HolderSet 1.20.2 tests */
    public static Optional<net.minecraft.class_6885<net.minecraft.class_2248>> blocks(Set<net.minecraft.class_2248> blocks) {
        return blocks == null ? Optional.empty()
            : Optional.of(net.minecraft.class_6885.method_40244(net.minecraft.class_2248::method_40142, blocks));
    }

    /** ItemPredicate's item set (null: any item) */
    public static Optional<net.minecraft.class_6885<net.minecraft.class_1792>> items(Set<net.minecraft.class_1792> items) {
        return items == null ? Optional.empty()
            : Optional.of(net.minecraft.class_6885.method_40244(net.minecraft.class_1792::method_40131, items));
    }

    /** FluidPredicate's fluid (null: any fluid) */
    public static Optional<net.minecraft.class_6880<net.minecraft.class_3611>> fluid(net.minecraft.class_3611 fluid) {
        return fluid == null ? Optional.empty() : Optional.of(fluid.method_40178());
    }

    /** ItemPredicate's potion (null: any potion) */
    public static Optional<net.minecraft.class_6880<net.minecraft.class_1842>> potion(net.minecraft.class_1842 potion) {
        return potion == null ? Optional.empty() : Optional.of(potion.method_53211());
    }

    /** 1.20.1's ItemPredicate(tag, items, count, durability, enchantments, storedEnchantments, potion, nbt), as 1.20.2's record */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2073 itemPredicate(net.minecraft.class_6862<net.minecraft.class_1792> tag, Set<net.minecraft.class_1792> items,
            net.minecraft.class_2096.class_2100 count, net.minecraft.class_2096.class_2100 durability, net.minecraft.class_2035[] enchantments,
            net.minecraft.class_2035[] stored, net.minecraft.class_1842 potion, net.minecraft.class_2105 nbt) {
        return new net.minecraft.class_2073((Optional) tag(tag), items(items), count, durability, enchantments(enchantments),
            enchantments(stored), potion(potion), CbAdvAny.nbt(nbt));
    }

    /** 1.20.1's EnchantmentPredicate(enchantment, levels), as 1.20.2's record: a null enchantment is any enchantment */
    public static net.minecraft.class_2035 enchantmentPredicate(net.minecraft.class_1887 enchantment, net.minecraft.class_2096.class_2100 levels) {
        return enchantment == null ? new net.minecraft.class_2035(Optional.empty(), levels) : new net.minecraft.class_2035(enchantment, levels);
    }

    /**
     * 1.20.1's LocationPredicate(x, y, z, biome, structure, dimension, smokey, light, block, fluid), as 1.20.2's record
     * (the parts as the constructor's superCtorReshape converts them)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2090 locationPredicate(net.minecraft.class_2096.class_2099 x, net.minecraft.class_2096.class_2099 y,
            net.minecraft.class_2096.class_2099 z, net.minecraft.class_5321<net.minecraft.class_1959> biome,
            net.minecraft.class_5321<net.minecraft.class_3195> structure, net.minecraft.class_5321<net.minecraft.class_1937> dimension,
            Boolean smokey, net.minecraft.class_4552 light, net.minecraft.class_4550 block, net.minecraft.class_4551 fluid) {
        return new net.minecraft.class_2090((Optional) position(x, y, z), (Optional) key(biome), (Optional) key(structure),
            (Optional) key(dimension), bool(smokey), CbAdvAny.light(light), CbAdvAny.block(block), CbAdvAny.fluid(fluid));
    }

    /** 1.20.1's BlockPredicate(tag, blocks, state, nbt), as 1.20.2's record */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_4550 blockPredicate(net.minecraft.class_6862<net.minecraft.class_2248> tag, Set<net.minecraft.class_2248> blocks,
            net.minecraft.class_4559 state, net.minecraft.class_2105 nbt) {
        return new net.minecraft.class_4550((Optional) tag(tag), blocks(blocks), CbAdvAny.state(state), CbAdvAny.nbt(nbt));
    }

    /** 1.20.1's FluidPredicate(tag, fluid, state), as 1.20.2's record */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_4551 fluidPredicate(net.minecraft.class_6862<net.minecraft.class_3611> tag, net.minecraft.class_3611 fluid,
            net.minecraft.class_4559 state) {
        return new net.minecraft.class_4551((Optional) tag(tag), fluid(fluid), CbAdvAny.state(state));
    }

    /** 1.20.1's ExactPropertyMatcher(name, value), as 1.20.2's matcher record (the name with an exact value matcher) */
    public static net.minecraft.class_4559.class_4562 exactProperty(String name, String value) {
        return new net.minecraft.class_4559.class_4562(name, new net.minecraft.class_4559.class_4561(value));
    }

    /** 1.20.1's RangedPropertyMatcher(name, min, max) (null: unbounded), as 1.20.2's matcher record */
    public static net.minecraft.class_4559.class_4562 rangedProperty(String name, String min, String max) {
        return new net.minecraft.class_4559.class_4562(name, new net.minecraft.class_4559.class_4563(Optional.ofNullable(min), Optional.ofNullable(max)));
    }

    /**
     * the EntityPredicate 1.20.1's EntityPredicate.Builder built from these parts (fromJson's: every part set, ANY being
     * 1.20.1's "any"), as 1.20.2's record: ANY parts absent (EntityTypePredicate.ANY, EntitySubPredicate.ANY included),
     * a null team absent
     */
    public static net.minecraft.class_2048 entityPredicate(net.minecraft.class_2050 type, net.minecraft.class_2025 distance,
            net.minecraft.class_2090 location, net.minecraft.class_2090 steppingOn, net.minecraft.class_2102 effects, net.minecraft.class_2105 nbt,
            net.minecraft.class_2040 flags, net.minecraft.class_3735 equipment, net.minecraft.class_7376 typeSpecific,
            net.minecraft.class_2048 vehicle, net.minecraft.class_2048 passenger, net.minecraft.class_2048 target, String team) {
        return new net.minecraft.class_2048(
            type == null || type == CbAdvAny.field_9609 ? Optional.empty() : Optional.of(type),
            CbAdvAny.distance(distance), CbAdvAny.location(location), CbAdvAny.location(steppingOn), CbAdvAny.effects(effects),
            CbAdvAny.nbt(nbt), CbAdvAny.flags(flags), CbAdvAny.equipment(equipment),
            typeSpecific == null || typeSpecific == CbAdvJson.field_38723 ? Optional.empty() : Optional.of(typeSpecific),
            CbAdvAny.entity(vehicle), CbAdvAny.entity(passenger), CbAdvAny.entity(target), Optional.ofNullable(team));
    }

    /**
     * 1.20.1's PlayerPredicate(level, gameType, stats, recipes, advancements, lookingAt), as 1.20.2's record: a null game
     * type and an ANY looking_at absent, each stat -> bounds entry a stat matcher (statMatcher), in the map's order
     */
    public static net.minecraft.class_4553 playerPredicate(net.minecraft.class_2096.class_2100 level, net.minecraft.class_1934 gameType,
            java.util.Map<net.minecraft.class_3445<?>, net.minecraft.class_2096.class_2100> stats,
            it.unimi.dsi.fastutil.objects.Object2BooleanMap<net.minecraft.class_2960> recipes,
            java.util.Map<net.minecraft.class_2960, net.minecraft.class_4553.class_4556> advancements, net.minecraft.class_2048 lookingAt) {
        List<net.minecraft.class_4553.class_8749<?>> matchers = new java.util.ArrayList<>(stats.size());
        for (java.util.Map.Entry<net.minecraft.class_3445<?>, net.minecraft.class_2096.class_2100> e : stats.entrySet()) {
            matchers.add(statMatcher(e.getKey(), e.getValue()));
        }
        return new net.minecraft.class_4553(level, Optional.ofNullable(gameType), matchers, recipes, advancements, CbAdvAny.entity(lookingAt));
    }

    /** a 1.20.1 stat condition (the stat, its bounds) as 1.20.2's stat matcher: the stat's type, its value's holder, the bounds */
    public static <T> net.minecraft.class_4553.class_8749<T> statMatcher(net.minecraft.class_3445<T> stat, net.minecraft.class_2096.class_2100 value) {
        net.minecraft.class_3448<T> type = stat.method_14949();
        return new net.minecraft.class_4553.class_8749<>(type, type.method_14959().method_47983(stat.method_14951()), value);
    }

    /** ItemPredicate's enchantment conditions (an array; NONE is empty) */
    public static List<net.minecraft.class_2035> enchantments(net.minecraft.class_2035[] e) {
        return e == null ? List.of() : List.copyOf(Arrays.asList(e));
    }

    /**
     * LocationPredicate's x, y, z bounds as 1.20.2's position predicate: absent when all three are
     * any (what 1.20.1 tested then), else the three bounds (LocationPredicate.Builder builds it the
     * way 1.20.2's own code does). Answered as a plain Optional: the position predicate is a
     * package-private class.
     */
    public static Optional<?> position(net.minecraft.class_2096.class_2099 x,
            net.minecraft.class_2096.class_2099 y, net.minecraft.class_2096.class_2099 z) {
        return net.minecraft.class_2090.class_2091.method_22484().method_35276(x).method_35278(y).method_35280(z)
            .method_9023().comp_1794();
    }

    // ------------------------------------------------------------------ the parts of the no-argument constructors

    public static Optional<?> absent() {
        return Optional.empty();
    }

    public static List<?> none() {
        return List.of();
    }

    /** MinMaxBounds.Ints.ANY */
    public static net.minecraft.class_2096.class_2100 anyInts() {
        return net.minecraft.class_2096.class_2100.field_9708;
    }

    /** MinMaxBounds.Doubles.ANY */
    public static net.minecraft.class_2096.class_2099 anyDoubles() {
        return net.minecraft.class_2096.class_2099.field_9705;
    }

    /**
     * the type set of a 1.20.1 EntityTypePredicate subclass's super(): 1.20.1's EntityTypePredicate was
     * abstract with no state, its subclass decided matches itself; the record's set is never consulted
     * for it (the subclass's own matches overrides the record's)
     */
    public static net.minecraft.class_6885<net.minecraft.class_1299<?>> noEntityTypes() {
        return net.minecraft.class_6885.method_40246();
    }

    // ------------------------------------------------------------------ LocationPredicate factories

    /** LocationPredicate.inBiome(biome) */
    public static net.minecraft.class_2090 method_9022(net.minecraft.class_5321<net.minecraft.class_1959> biome) {
        return net.minecraft.class_2090.class_2091.method_53180(biome).method_9023();
    }

    /** LocationPredicate.inDimension(dimension) */
    public static net.minecraft.class_2090 method_9016(net.minecraft.class_5321<net.minecraft.class_1937> dimension) {
        return net.minecraft.class_2090.class_2091.method_53182(dimension).method_9023();
    }

    /** LocationPredicate.atYLocation(y) */
    public static net.minecraft.class_2090 method_38858(net.minecraft.class_2096.class_2099 y) {
        return net.minecraft.class_2090.class_2091.method_53181(y).method_9023();
    }

    // ------------------------------------------------------------------ MobEffectsPredicate

    /** MobEffectsPredicate.effects(): an empty, growable predicate (1.20.1: a LinkedHashMap) */
    public static net.minecraft.class_2102 method_9066() {
        return new net.minecraft.class_2102(new LinkedHashMap<>());
    }

    /** MobEffectsPredicate.and(effect): the effect with any amplifier and duration (a subclass's own and answers) */
    public static net.minecraft.class_2102 method_9065(net.minecraft.class_2102 self, net.minecraft.class_1291 effect) {
        return (Object) self instanceof CbAdvLegacy.EffectsAnd own ? own.method_9065(effect) : super_method_9065(self, effect);
    }

    /** MobEffectsPredicate.and(effect, instance) */
    public static net.minecraft.class_2102 method_35290(net.minecraft.class_2102 self, net.minecraft.class_1291 effect,
            net.minecraft.class_2102.class_2103 instance) {
        return (Object) self instanceof CbAdvLegacy.EffectsAndInstance own ? own.method_35290(effect, instance)
            : super_method_35290(self, effect, instance);
    }

    /** super.and(effect) of a 1.20.1 subclass: 1.20.1's body, and(effect, new MobEffectInstancePredicate()) */
    public static net.minecraft.class_2102 super_method_9065(net.minecraft.class_2102 self, net.minecraft.class_1291 effect) {
        return method_35290(self, effect, new net.minecraft.class_2102.class_2103());
    }

    /**
     * super.and(effect, instance): 1.20.1 put the condition into the predicate's own map and answered the
     * predicate. A predicate this layer built (effects(), a 1.20.1 constructor's map) holds 1.20.1's mutable
     * map and is extended in place; one 1.20.2 decoded holds an immutable map, so the answer is a copy with
     * the condition added (the answer 1.20.1 callers chain on; the decoded predicate itself stays as read)
     */
    public static net.minecraft.class_2102 super_method_35290(net.minecraft.class_2102 self, net.minecraft.class_1291 effect,
            net.minecraft.class_2102.class_2103 instance) {
        java.util.Map<net.minecraft.class_6880<net.minecraft.class_1291>, net.minecraft.class_2102.class_2103> map = self.comp_1811();
        if (map instanceof java.util.HashMap) {
            map.put(effect.method_53123(), instance);
            return self;
        }
        LinkedHashMap<net.minecraft.class_6880<net.minecraft.class_1291>, net.minecraft.class_2102.class_2103> copy = new LinkedHashMap<>(map);
        copy.put(effect.method_53123(), instance);
        return new net.minecraft.class_2102(copy);
    }

    /**
     * MobEffectsPredicate(map): 1.20.1 keyed the conditions by effect, the record by the effect's holder.
     * A LinkedHashMap with the keys converted (a key that already is a holder passes as it is), so the
     * order 1.20.1 tested and wrote them in is kept and and(..) can still add to it. (1.20.1 held the
     * mod's own map; a write the mod makes to that map afterwards no longer reaches the predicate.)
     */
    public static java.util.Map<?, ?> effectMap(java.util.Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        LinkedHashMap<Object, Object> out = new LinkedHashMap<>();
        for (java.util.Map.Entry<?, ?> e : map.entrySet()) {
            Object k = e.getKey();
            out.put(k instanceof net.minecraft.class_1291 effect ? effect.method_53123() : k, e.getValue());
        }
        return out;
    }

    /**
     * StatePropertiesPredicate.Builder.build(): 1.20.1 answered a predicate over the matchers added so far
     * (none: one matching any state, as ANY does); 1.20.2 answers absent for none
     */
    public static net.minecraft.class_4559 method_22528(net.minecraft.class_4559.class_4560 self) {
        return self.method_22528().orElse(CbAdvAny.field_20736);
    }

    private CbAdvRecords() {
    }
}
