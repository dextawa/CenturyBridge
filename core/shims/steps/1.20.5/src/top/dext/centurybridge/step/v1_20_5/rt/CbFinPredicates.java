package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_5.facade.EntitySubPredicateType;
import top.dext.centurybridge.step.v1_20_5.facade.EntitySubPredicateTypes;
import top.dext.centurybridge.step.v1_20_5.facade.EntityVariantSubPredicate;

/**
 * 1.20.4's advancement predicate API on 1.20.5's (area s4fin advancements):
 * <ul>
 * <li>Entity sub-predicates: 1.20.4's type() is the record over the predicate's codec (facade/EntitySubPredicateType,
 *     the named ones in facade/EntitySubPredicateTypes); a 1.20.4 sub-predicate's own type() is its 1.20.5 codec();
 *     1.20.4's variant predicates (facade/EntityVariantPredicate) make 1.20.5's, and a wrapped one is unwrapped where
 *     it enters an entity predicate, so it serializes as 1.20.5's.</li>
 * <li>Tags and single values 1.20.5 folds into holder sets (block, fluid and item predicates): a tag is the
 *     registry's named set; a tag and a set together, the set's members in the tag.</li>
 * <li>ItemPredicate's durability, enchantments, stored enchantments, potion and NBT are 1.20.5's item sub-predicates
 *     (damage, enchantments, stored_enchantments, potion_contents, custom_data); its getters read them back.</li>
 * <li>Arguments 1.20.5 added: an entity predicate's slots and equipment body (none, as 1.20.4 had none).</li>
 * </ul>
 */
public final class CbFinPredicates {

    // ---------------------------------------------------------------- entity sub-predicates

    /** a 1.20.4 sub-predicate declaring type() */
    public interface Legacy {
        EntitySubPredicateType comp_1776();
    }

    /** predicate.type(): its own (a 1.20.4 one), 1.20.4's named type for its codec, else the record over its codec */
    public static EntitySubPredicateType comp_1776(net.minecraft.class_7376 predicate) {
        if (predicate instanceof Legacy l) {
            return l.comp_1776();
        }
        if (predicate instanceof EntityVariantSubPredicate<?> v) {
            return v.comp_1776;
        }
        com.mojang.serialization.MapCodec<?> codec = predicate.method_58152();
        for (EntitySubPredicateType t : EntitySubPredicateTypes.field_38731.values()) {
            if (t.comp_1774 == codec) {
                return t;
            }
        }
        return new EntitySubPredicateType(predicate.method_58152());
    }

    public static EntitySubPredicateType comp_1776(net.minecraft.class_4553 p) {
        return comp_1776((net.minecraft.class_7376) p);
    }

    public static EntitySubPredicateType comp_1776(net.minecraft.class_4965 p) {
        return comp_1776((net.minecraft.class_7376) p);
    }

    public static EntitySubPredicateType comp_1776(net.minecraft.class_6404 p) {
        return comp_1776((net.minecraft.class_7376) p);
    }

    public static EntitySubPredicateType comp_1776(net.minecraft.class_7383 p) {
        return comp_1776((net.minecraft.class_7376) p);
    }

    /** codec() of a 1.20.4 sub-predicate: its type's codec */
    public static com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> legacyCodec(Legacy legacy) {
        return legacy.comp_1776().comp_1774;
    }

    /** EntitySubPredicate.variant(cat variant) */
    public static net.minecraft.class_7376 catVariant(net.minecraft.class_7375 variant) {
        return net.minecraft.class_7378.method_58153(net.minecraft.class_7923.field_41163.method_47983(variant));
    }

    /** EntitySubPredicate.variant(frog variant) */
    public static net.minecraft.class_7376 frogVariant(net.minecraft.class_7106 variant) {
        return net.minecraft.class_7378.method_58154(net.minecraft.class_7923.field_41164.method_47983(variant));
    }

    /** the codec of a 1.20.5 variant predicate type (CbFinVariantTypeAccess) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> variantCodec(net.minecraft.class_7378.class_7380<?> w) {
        return (com.mojang.serialization.MapCodec) ((top.dext.centurybridge.step.v1_20_5.mixin.CbFinVariantTypeAccess) w).cbS4$codec();
    }

    /** its getter */
    public static java.util.function.Function<net.minecraft.class_1297, Optional<?>> variantGetter(net.minecraft.class_7378.class_7380<?> w) {
        return ((top.dext.centurybridge.step.v1_20_5.mixin.CbFinVariantTypeAccess) w).cbS4$getter();
    }

    /** the codec of a 1.20.5 holder-set variant predicate type */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> holderVariantCodec(net.minecraft.class_7378.class_9419<?> w) {
        return (com.mojang.serialization.MapCodec) ((top.dext.centurybridge.step.v1_20_5.mixin.CbFinHolderVariantTypeAccess) w).cbS4$codec();
    }

    /** a 1.20.4 variant sub-predicate entering an entity predicate: the 1.20.5 predicate it stands for */
    public static net.minecraft.class_7376 unwrap(net.minecraft.class_7376 predicate) {
        return predicate instanceof EntityVariantSubPredicate<?> v && v.delegate != null ? v.delegate : predicate;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional unwrapOptional(Optional predicate) {
        return predicate.map(p -> unwrap((net.minecraft.class_7376) p));
    }

    /** EntityPredicate.Builder.subPredicate(predicate) */
    public static net.minecraft.class_2048.class_2049 subPredicate(net.minecraft.class_2048.class_2049 builder, net.minecraft.class_7376 predicate) {
        return builder.method_43094(unwrap(predicate));
    }

    /** a predicate component 1.20.5 added: none */
    @SuppressWarnings("rawtypes")
    public static Optional none() {
        return Optional.empty();
    }

    /** EntityEquipmentPredicate.CAPTAIN: 1.20.5 builds it over the banner patterns; the running world's */
    public static net.minecraft.class_3735 captain() {
        return net.minecraft.class_3735.method_58057(CbRegistries.current().method_46762(net.minecraft.class_7924.field_41252));
    }

    // ---------------------------------------------------------------- tags and holder sets

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Optional holders(net.minecraft.class_2378 registry, Optional tag, Optional set) {
        if (tag.isEmpty()) {
            return set;
        }
        net.minecraft.class_6885.class_6888 named = registry.method_40260((net.minecraft.class_6862) tag.get());
        if (set.isEmpty()) {
            return Optional.of(named);
        }
        List<net.minecraft.class_6880> both = new ArrayList<>();
        for (Object h : (net.minecraft.class_6885) set.get()) {
            if (((net.minecraft.class_6880) h).method_40220((net.minecraft.class_6862) tag.get())) {
                both.add((net.minecraft.class_6880) h);
            }
        }
        return Optional.of(net.minecraft.class_6885.method_40242((List) both));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Optional tagOf(Optional set) {
        return set.flatMap(s -> ((net.minecraft.class_6885) s).method_40248().left());
    }

    /** BlockPredicate(tag, blocks, ..): one holder set */
    @SuppressWarnings("rawtypes")
    public static Optional blocks(Optional tag, Optional blocks) {
        return holders(net.minecraft.class_7923.field_41175, tag, blocks);
    }

    /** BlockPredicate.tag() */
    @SuppressWarnings("rawtypes")
    public static Optional blockTag(net.minecraft.class_4550 p) {
        return tagOf(p.comp_1732());
    }

    /** FluidPredicate(tag, fluid, ..): one holder set */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional fluids(Optional tag, Optional fluid) {
        return holders(net.minecraft.class_7923.field_41173, tag,
            fluid.map(f -> net.minecraft.class_6885.method_40246((net.minecraft.class_6880) f)));
    }

    @SuppressWarnings("rawtypes")
    public static Optional fluidTag(net.minecraft.class_4551 p) {
        return tagOf(p.comp_2372());
    }

    /** FluidPredicate.fluid(): the set's single fluid */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional fluid(net.minecraft.class_4551 p) {
        return p.comp_2372().flatMap(s -> s.method_40248().right().filter(l -> ((List) l).size() == 1).map(l -> ((List) l).get(0)));
    }

    /** FluidPredicate.Builder.of(tag) */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_6885 fluidTagSet(net.minecraft.class_6862 tag) {
        return net.minecraft.class_7923.field_41173.method_40260(tag);
    }

    // ---------------------------------------------------------------- enchantments

    /** EnchantmentPredicate.containedIn(map): 1.20.5 tests item enchantments */
    public static boolean containedIn(net.minecraft.class_2035 p, Map<net.minecraft.class_1887, Integer> map) {
        net.minecraft.class_9304.class_9305 m = new net.minecraft.class_9304.class_9305(net.minecraft.class_9304.field_49385);
        map.forEach(m::method_57547);
        return p.method_8880(m.method_57549());
    }

    // ---------------------------------------------------------------- ItemPredicate

    /** ItemPredicate(tag, items, count, durability, enchantments, storedEnchantments, potion, nbt)'s 1.20.4 parts as 1.20.5's sub-predicates */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map itemSubPredicates(net.minecraft.class_2096.class_2100 durability, List enchantments, List stored,
                                        Optional potion, Optional nbt) {
        com.google.common.collect.ImmutableMap.Builder b = com.google.common.collect.ImmutableMap.builder();
        if (!durability.method_9041()) {
            b.put(net.minecraft.class_9361.field_49806, net.minecraft.class_9355.method_58165(durability));
        }
        if (!enchantments.isEmpty()) {
            b.put(net.minecraft.class_9361.field_49807, net.minecraft.class_9356.method_58173(enchantments));
        }
        if (!stored.isEmpty()) {
            b.put(net.minecraft.class_9361.field_49808, net.minecraft.class_9356.method_58176(stored));
        }
        if (potion.isPresent()) {
            b.put(net.minecraft.class_9361.field_49809, new net.minecraft.class_9359(net.minecraft.class_6885.method_40246((net.minecraft.class_6880) potion.get())));
        }
        if (nbt.isPresent()) {
            b.put(net.minecraft.class_9361.field_49810, net.minecraft.class_9354.method_58162((net.minecraft.class_2105) nbt.get()));
        }
        return b.build();
    }

    @SuppressWarnings("rawtypes")
    public static Optional itemSet(Optional tag, Optional items) {
        return holders(net.minecraft.class_7923.field_41178, tag, items);
    }

    /** an empty component predicate (1.20.4 had none) */
    public static net.minecraft.class_9329 noComponents() {
        return net.minecraft.class_9329.field_49597;
    }

    @SuppressWarnings("rawtypes")
    public static Optional comp_1783(net.minecraft.class_2073 p) {
        return tagOf(p.comp_1784());
    }

    public static net.minecraft.class_2096.class_2100 comp_1786(net.minecraft.class_2073 p) {
        net.minecraft.class_9360 d = p.comp_2462().get(net.minecraft.class_9361.field_49806);
        return d instanceof net.minecraft.class_9355 damage ? damage.comp_2459() : net.minecraft.class_2096.class_2100.field_9708;
    }

    public static List<net.minecraft.class_2035> comp_1787(net.minecraft.class_2073 p) {
        return enchantments(p.comp_2462().get(net.minecraft.class_9361.field_49807));
    }

    public static List<net.minecraft.class_2035> comp_1788(net.minecraft.class_2073 p) {
        return enchantments(p.comp_2462().get(net.minecraft.class_9361.field_49808));
    }

    private static List<net.minecraft.class_2035> enchantments(net.minecraft.class_9360 s) {
        return s == null ? List.of() : ((top.dext.centurybridge.step.v1_20_5.mixin.CbFinItemEnchantmentsAccess) s).cbS4$enchantments();
    }

    /** potion(): the potion predicate's single potion */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional comp_1789(net.minecraft.class_2073 p) {
        net.minecraft.class_9360 s = p.comp_2462().get(net.minecraft.class_9361.field_49809);
        if (!(s instanceof net.minecraft.class_9359 potions)) {
            return Optional.empty();
        }
        return potions.comp_2461().method_40248().right().filter(l -> ((List) l).size() == 1).map(l -> ((List) l).get(0));
    }

    @SuppressWarnings("rawtypes")
    public static Optional comp_1790(net.minecraft.class_2073 p) {
        net.minecraft.class_9360 s = p.comp_2462().get(net.minecraft.class_9361.field_49810);
        return s instanceof net.minecraft.class_9354 data ? Optional.of(data.comp_2458()) : Optional.empty();
    }

    // builder

    public static net.minecraft.class_2073.class_2074 hasDurability(net.minecraft.class_2073.class_2074 b, net.minecraft.class_2096.class_2100 durability) {
        return b.method_58179(net.minecraft.class_9361.field_49806, net.minecraft.class_9355.method_58165(durability));
    }

    public static net.minecraft.class_2073.class_2074 isPotion(net.minecraft.class_2073.class_2074 b, net.minecraft.class_1842 potion) {
        return b.method_58179(net.minecraft.class_9361.field_49809, new net.minecraft.class_9359(net.minecraft.class_6885.method_40246(CbHolders.potion(potion))));
    }

    public static net.minecraft.class_2073.class_2074 hasNbt(net.minecraft.class_2073.class_2074 b, net.minecraft.class_2487 nbt) {
        return b.method_58179(net.minecraft.class_9361.field_49810, net.minecraft.class_9354.method_58162(new net.minecraft.class_2105(nbt)));
    }

    public static net.minecraft.class_2073.class_2074 hasEnchantment(net.minecraft.class_2073.class_2074 b, net.minecraft.class_2035 e) {
        return b.method_58179(net.minecraft.class_9361.field_49807, net.minecraft.class_9356.method_58173(List.of(e)));
    }

    public static net.minecraft.class_2073.class_2074 hasStoredEnchantment(net.minecraft.class_2073.class_2074 b, net.minecraft.class_2035 e) {
        return b.method_58179(net.minecraft.class_9361.field_49808, net.minecraft.class_9356.method_58176(List.of(e)));
    }

    // ---------------------------------------------------------------- network forms

    /** a DisplayInfo write running 1.20.4's serializeToNetwork (CbFinDisplayInfoMixin): its super call writes 1.20.5's way */
    private static final ThreadLocal<Boolean> IN_DISPLAY_WRITE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /** classes of DisplayInfo declaring 1.20.4's serializeToNetwork(FriendlyByteBuf) */
    private static final ClassValue<Boolean> OVERRIDES_DISPLAY_WRITE = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != net.minecraft.class_185.class; c = c.getSuperclass()) {
                for (java.lang.reflect.Method m : c.getDeclaredMethods()) {
                    if (m.getName().equals("method_813") && m.getParameterCount() == 1
                            && m.getParameterTypes()[0] == net.minecraft.class_2540.class) {
                        return Boolean.TRUE;
                    }
                }
            }
            return Boolean.FALSE;
        }
    };

    public static boolean cbOverridesDisplayWrite(Class<?> type) {
        return OVERRIDES_DISPLAY_WRITE.get(type);
    }

    public static boolean cbInDisplayWrite() {
        return IN_DISPLAY_WRITE.get();
    }

    /** marks a 1.20.4 write in progress; answers the previous state for {@link #cbExitDisplayWrite} */
    public static boolean cbEnterDisplayWrite() {
        boolean outer = IN_DISPLAY_WRITE.get();
        IN_DISPLAY_WRITE.set(Boolean.TRUE);
        return outer;
    }

    public static void cbExitDisplayWrite(boolean outer) {
        IN_DISPLAY_WRITE.set(outer);
    }

    public static void method_813(net.minecraft.class_185 display, net.minecraft.class_2540 buf) {
        net.minecraft.class_185.field_48276.encode(CbNetBufs.cbRegistryBuf(buf), display);
    }

    public static net.minecraft.class_185 method_820(net.minecraft.class_2540 buf) {
        return net.minecraft.class_185.field_48276.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    public static void method_53627(net.minecraft.class_161 advancement, net.minecraft.class_2540 buf) {
        net.minecraft.class_161.field_47996.encode(CbNetBufs.cbRegistryBuf(buf), advancement);
    }

    public static net.minecraft.class_161 method_53632(net.minecraft.class_2540 buf) {
        return net.minecraft.class_161.field_47996.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    public static void method_53635(net.minecraft.class_8779 holder, net.minecraft.class_2540 buf) {
        net.minecraft.class_8779.field_48180.encode(CbNetBufs.cbRegistryBuf(buf), holder);
    }

    public static net.minecraft.class_8779 method_53636(net.minecraft.class_2540 buf) {
        return net.minecraft.class_8779.field_48180.decode(CbNetBufs.cbRegistryBuf(buf));
    }

    private CbFinPredicates() {
    }
}
