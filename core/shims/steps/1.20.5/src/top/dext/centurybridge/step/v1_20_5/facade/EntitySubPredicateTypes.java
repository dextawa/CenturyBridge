package top.dext.centurybridge.step.v1_20_5.facade;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;

/**
 * 1.20.4's EntitySubPredicate.Types: the sub-predicate types by name, each over the codec 1.20.5 registered under
 * that name in BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, and the variant predicates over 1.20.5's
 * EntitySubPredicates variant types. "any" (1.20.4's always-matching type) has no 1.20.5 registration: its codec
 * is the unit codec of an always-matching predicate, as 1.20.4's was.
 */
public final class EntitySubPredicateTypes {

    /** 1.20.4's ANY predicate: matches every entity */
    public static final net.minecraft.class_7376 ANY_PREDICATE = new net.minecraft.class_7376() {
        @Override
        public boolean method_22497(net.minecraft.class_1297 entity, net.minecraft.class_3218 level, net.minecraft.class_243 position) {
            return true;
        }

        @Override
        public com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> method_58152() {
            return field_38724.comp_1774;
        }
    };

    /** ANY */
    public static final EntitySubPredicateType field_38724 = new EntitySubPredicateType(com.mojang.serialization.MapCodec.unit(ANY_PREDICATE));
    /** LIGHTNING */
    public static final EntitySubPredicateType field_38725 = new EntitySubPredicateType(net.minecraft.class_7378.field_38726);
    /** FISHING_HOOK */
    public static final EntitySubPredicateType field_38726 = new EntitySubPredicateType(net.minecraft.class_7378.field_38727);
    /** PLAYER */
    public static final EntitySubPredicateType field_38727 = new EntitySubPredicateType(net.minecraft.class_7378.field_38725);
    /** SLIME */
    public static final EntitySubPredicateType field_38728 = new EntitySubPredicateType(net.minecraft.class_7378.field_38728);

    /** CAT */
    public static final EntityVariantPredicate<net.minecraft.class_7375> field_38729 = EntityVariantPredicate.holders(
        net.minecraft.class_7378.field_38729,
        e -> e instanceof net.minecraft.class_1451 cat ? Optional.of(cat.method_47843().comp_349()) : Optional.empty(),
        v -> net.minecraft.class_7923.field_41163.method_47983(v));
    /** FROG */
    public static final EntityVariantPredicate<net.minecraft.class_7106> field_38730 = EntityVariantPredicate.holders(
        net.minecraft.class_7378.field_38730,
        e -> e instanceof net.minecraft.class_7102 frog ? Optional.of(frog.method_41354().comp_349()) : Optional.empty(),
        v -> net.minecraft.class_7923.field_41164.method_47983(v));
    /** PAINTING (1.20.4's variant is the holder itself) */
    public static final EntityVariantPredicate<net.minecraft.class_6880<net.minecraft.class_1535>> field_41538 = EntityVariantPredicate.holderValued(
        net.minecraft.class_7378.field_41538,
        e -> e instanceof net.minecraft.class_1534 p ? Optional.of(p.method_43404()) : Optional.empty());

    public static final EntityVariantPredicate<net.minecraft.class_5762.class_5767> field_41534 = plain(net.minecraft.class_7378.field_41534);
    public static final EntityVariantPredicate<net.minecraft.class_1690.class_1692> field_41535 = plain(net.minecraft.class_7378.field_41535);
    public static final EntityVariantPredicate<net.minecraft.class_4019.class_4039> field_41536 = plain(net.minecraft.class_7378.field_41536);
    public static final EntityVariantPredicate<net.minecraft.class_1438.class_4053> field_41537 = plain(net.minecraft.class_7378.field_41537);
    public static final EntityVariantPredicate<net.minecraft.class_1463.class_7990> field_41539 = plain(net.minecraft.class_7378.field_41539);
    public static final EntityVariantPredicate<net.minecraft.class_5149> field_41540 = plain(net.minecraft.class_7378.field_41540);
    public static final EntityVariantPredicate<net.minecraft.class_1501.class_7993> field_41541 = plain(net.minecraft.class_7378.field_41541);
    public static final EntityVariantPredicate<net.minecraft.class_3854> field_41542 = plain(net.minecraft.class_7378.field_41542);
    public static final EntityVariantPredicate<net.minecraft.class_1453.class_7989> field_41543 = plain(net.minecraft.class_7378.field_41543);
    public static final EntityVariantPredicate<net.minecraft.class_1474.class_1475> field_41544 = plain(net.minecraft.class_7378.field_41544);

    /** TYPES: 1.20.4's names */
    public static final BiMap<String, EntitySubPredicateType> field_38731;

    /** CODEC: a type by its 1.20.4 name */
    public static final Codec<EntitySubPredicateType> field_45748;

    static {
        BiMap<String, EntitySubPredicateType> m = HashBiMap.create();
        m.put("any", field_38724);
        m.put("lightning", field_38725);
        m.put("fishing_hook", field_38726);
        m.put("player", field_38727);
        m.put("slime", field_38728);
        m.put("cat", field_38729.method_43114());
        m.put("frog", field_38730.method_43114());
        m.put("axolotl", field_41534.method_43114());
        m.put("boat", field_41535.method_43114());
        m.put("fox", field_41536.method_43114());
        m.put("mooshroom", field_41537.method_43114());
        m.put("painting", field_41538.method_43114());
        m.put("rabbit", field_41539.method_43114());
        m.put("horse", field_41540.method_43114());
        m.put("llama", field_41541.method_43114());
        m.put("villager", field_41542.method_43114());
        m.put("parrot", field_41543.method_43114());
        m.put("tropical_fish", field_41544.method_43114());
        field_38731 = com.google.common.collect.ImmutableBiMap.copyOf(m);
        field_45748 = Codec.STRING.comapFlatMap(
            name -> Optional.ofNullable(field_38731.get(name)).map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "Unknown type: " + name)),
            type -> field_38731.inverse().get(type));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <V> EntityVariantPredicate<V> plain(net.minecraft.class_7378.class_7380<V> w) {
        return EntityVariantPredicate.plain(w, (java.util.function.Function) top.dext.centurybridge.step.v1_20_5.rt.CbFinPredicates.variantGetter(w));
    }

    public EntitySubPredicateTypes() {
    }
}
