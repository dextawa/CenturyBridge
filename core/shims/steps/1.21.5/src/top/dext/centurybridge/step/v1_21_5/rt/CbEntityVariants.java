package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1438;
import net.minecraft.class_1451;
import net.minecraft.class_1453;
import net.minecraft.class_1462;
import net.minecraft.class_1463;
import net.minecraft.class_1474;
import net.minecraft.class_1493;
import net.minecraft.class_1498;
import net.minecraft.class_1501;
import net.minecraft.class_1534;
import net.minecraft.class_1535;
import net.minecraft.class_2378;
import net.minecraft.class_3851;
import net.minecraft.class_3854;
import net.minecraft.class_4019;
import net.minecraft.class_5149;
import net.minecraft.class_5762;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7102;
import net.minecraft.class_7106;
import net.minecraft.class_7375;
import net.minecraft.class_7376;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9346;
import top.dext.centurybridge.step.v1_21_5.facade.EntitySubPredicates.EntityHolderVariantPredicateType;
import top.dext.centurybridge.step.v1_21_5.facade.EntitySubPredicates.EntityVariantPredicateType;

/**
 * 1.21.4's EntitySubPredicates variant types (class_7378 AXOLOTL ... WOLF, catVariant / frogVariant / wolfVariant), gone
 * in 1.21.5, which matches entity variants through data component predicates. Built here as 1.21.4 built them (javap
 * -c -p 1.21.4 class_7378 <clinit> and lambdas: each a facade EntityVariantPredicateType of the variant's codec, or an
 * EntityHolderVariantPredicateType of the variant registry, with the entity -> variant getter) and registered under
 * 1.21.4's names in the entity sub-predicate type registry (class_7923.field_49911, which 1.21.5 still dispatches
 * "type_specific" predicates through), so a converted mod's references and a 1.21.4 datapack's
 * {"type_specific": {"type": "cat", "variant": ...}} work as on 1.21.4. Getters: 1.21.4's (same intermediary members on
 * 1.21.5, javap), except villager type (1.21.5 VillagerData.type() is a Holder: its value), tropical fish (1.21.5
 * getPattern, method_66681) and wolf (1.21.5's getVariant is private: the layer access widener opens it). The fox and
 * salmon variant codecs are StringRepresentable.fromEnum(values) built here, as 1.21.5's Fox.Variant.CODEC /
 * Salmon.Variant.CODEC are (javap <clinit>): those fields are typed EnumCodec in 1.21.5 and Codec from 26.1, and a read
 * of them under 1.21.5's type stops linking past the 26.1 wall.
 */
public final class CbEntityVariants {
    private CbEntityVariants() {
    }

    public static final EntityVariantPredicateType<class_5762.class_5767> AXOLOTL = EntityVariantPredicateType.method_47840(
        class_5762.class_5767.field_41585, e -> e instanceof class_5762 a ? Optional.of(a.method_33225()) : Optional.empty());
    public static final EntityVariantPredicateType<class_4019.class_4039> FOX = EntityVariantPredicateType.method_47840(
        net.minecraft.class_3542.method_28140(class_4019.class_4039::values), e -> e instanceof class_4019 f ? Optional.of(f.method_47845()) : Optional.empty());
    public static final EntityVariantPredicateType<class_1438.class_4053> MOOSHROOM = EntityVariantPredicateType.method_47840(
        class_1438.class_4053.field_41549, e -> e instanceof class_1438 m ? Optional.of(m.method_47847()) : Optional.empty());
    public static final EntityVariantPredicateType<class_1463.class_7990> RABBIT = EntityVariantPredicateType.method_47840(
        class_1463.class_7990.field_41568, e -> e instanceof class_1463 r ? Optional.of(r.method_47855()) : Optional.empty());
    public static final EntityVariantPredicateType<class_5149> HORSE = EntityVariantPredicateType.method_47840(
        class_5149.field_41595, e -> e instanceof class_1498 h ? Optional.of(h.method_27077()) : Optional.empty());
    public static final EntityVariantPredicateType<class_1501.class_7993> LLAMA = EntityVariantPredicateType.method_47840(
        class_1501.class_7993.field_41590, e -> e instanceof class_1501 l ? Optional.of(l.method_6809()) : Optional.empty());
    public static final EntityVariantPredicateType<class_3854> VILLAGER = EntityVariantPredicateType.method_47840(
        class_7923.field_41194.method_39673(),
        e -> e instanceof class_3851 v ? Optional.of(v.method_7231().comp_3520().comp_349()) : Optional.empty());
    public static final EntityVariantPredicateType<class_1453.class_7989> PARROT = EntityVariantPredicateType.method_47840(
        class_1453.class_7989.field_41555, e -> e instanceof class_1453 p ? Optional.of(p.method_6584()) : Optional.empty());
    public static final EntityVariantPredicateType<class_1462.class_9869> SALMON = EntityVariantPredicateType.method_47840(
        net.minecraft.class_3542.method_28140(class_1462.class_9869::values), e -> e instanceof class_1462 s ? Optional.of(s.method_61472()) : Optional.empty());
    public static final EntityVariantPredicateType<class_1474.class_1475> TROPICAL_FISH = EntityVariantPredicateType.method_47840(
        class_1474.class_1475.field_41578, e -> e instanceof class_1474 t ? Optional.of(t.method_66681()) : Optional.empty());
    public static final EntityHolderVariantPredicateType<class_1535> PAINTING = EntityHolderVariantPredicateType.method_58354(
        class_7924.field_41209, e -> e instanceof class_1534 p ? Optional.of(p.method_43404()) : Optional.empty());
    public static final EntityHolderVariantPredicateType<class_7375> CAT = EntityHolderVariantPredicateType.method_58354(
        class_7924.field_41259, e -> e instanceof class_1451 c ? Optional.of(c.method_47843()) : Optional.empty());
    public static final EntityHolderVariantPredicateType<class_7106> FROG = EntityHolderVariantPredicateType.method_58354(
        class_7924.field_41272, e -> e instanceof class_7102 f ? Optional.of(f.method_41354()) : Optional.empty());
    public static final EntityHolderVariantPredicateType<class_9346> WOLF = EntityHolderVariantPredicateType.method_58354(
        class_7924.field_49772, e -> e instanceof class_1493 w ? Optional.of(w.method_58062()) : Optional.empty());

    /** 1.21.4 EntitySubPredicates registered each type's codec under its name (method_58155 / method_58352) */
    public static void register() {
        reg("axolotl", AXOLOTL.field_49794);
        reg("fox", FOX.field_49794);
        reg("mooshroom", MOOSHROOM.field_49794);
        reg("rabbit", RABBIT.field_49794);
        reg("horse", HORSE.field_49794);
        reg("llama", LLAMA.field_49794);
        reg("villager", VILLAGER.field_49794);
        reg("parrot", PARROT.field_49794);
        reg("salmon", SALMON.field_49794);
        reg("tropical_fish", TROPICAL_FISH.field_49794);
        reg("painting", PAINTING.field_49974);
        reg("cat", CAT.field_49974);
        reg("frog", FROG.field_49974);
        reg("wolf", WOLF.field_49974);
    }

    private static void reg(String name, com.mojang.serialization.MapCodec<? extends class_7376> codec) {
        class_2378.method_10226(class_7923.field_49911, name, codec);
    }

    public static EntityVariantPredicateType<class_5762.class_5767> field_41534() {
        return AXOLOTL;
    }

    public static EntityVariantPredicateType<class_4019.class_4039> field_41536() {
        return FOX;
    }

    public static EntityVariantPredicateType<class_1438.class_4053> field_41537() {
        return MOOSHROOM;
    }

    public static EntityVariantPredicateType<class_1463.class_7990> field_41539() {
        return RABBIT;
    }

    public static EntityVariantPredicateType<class_5149> field_41540() {
        return HORSE;
    }

    public static EntityVariantPredicateType<class_1501.class_7993> field_41541() {
        return LLAMA;
    }

    public static EntityVariantPredicateType<class_3854> field_41542() {
        return VILLAGER;
    }

    public static EntityVariantPredicateType<class_1453.class_7989> field_41543() {
        return PARROT;
    }

    public static EntityVariantPredicateType<class_1462.class_9869> field_54227() {
        return SALMON;
    }

    public static EntityVariantPredicateType<class_1474.class_1475> field_41544() {
        return TROPICAL_FISH;
    }

    public static EntityHolderVariantPredicateType<class_1535> field_41538() {
        return PAINTING;
    }

    public static EntityHolderVariantPredicateType<class_7375> field_38729() {
        return CAT;
    }

    public static EntityHolderVariantPredicateType<class_7106> field_38730() {
        return FROG;
    }

    public static EntityHolderVariantPredicateType<class_9346> field_49973() {
        return WOLF;
    }

    /** catVariant(holder): CAT.createPredicate(HolderSet.direct(holder)) */
    public static class_7376 method_58153(class_6880<class_7375> variant) {
        return CAT.method_58356(class_6885.method_40246(variant));
    }

    /** frogVariant(holder): FROG.createPredicate(HolderSet.direct(holder)) */
    public static class_7376 method_58154(class_6880<class_7106> variant) {
        return FROG.method_58356(class_6885.method_40246(variant));
    }

    /** wolfVariant(holders): WOLF.createPredicate(holders) */
    public static class_7376 method_59667(class_6885<class_9346> variants) {
        return WOLF.method_58356(variants);
    }
}
