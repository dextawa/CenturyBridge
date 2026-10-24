package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Optional;
import java.util.function.Function;

/**
 * 1.20.4's EntityVariantPredicate: a sub-predicate type for one entity variant -- create(registry, getter) /
 * create(codec, getter), type(), createPredicate(variant). 1.20.5's EntitySubPredicates.EntityVariantPredicateType
 * does the same over a codec ({@link #plain}); the variants 1.20.5 keeps in data registries (cat, frog, painting)
 * it matches by holder set ({@link #holders}), and this answers a single variant as the set of its holder. The
 * predicates it makes are 1.20.5's, wrapped as 1.20.4's SubPredicate record (EntityVariantSubPredicate).
 */
public class EntityVariantPredicate<V> {

    private final Function<net.minecraft.class_1297, Optional<V>> getter;
    private final EntitySubPredicateType type;
    private final Function<V, net.minecraft.class_7376> make;

    protected EntityVariantPredicate(Function<net.minecraft.class_1297, Optional<V>> getter, EntitySubPredicateType type,
                                   Function<V, net.minecraft.class_7376> make) {
        this.getter = getter;
        this.type = type;
        this.make = make;
    }

    /** over a 1.20.5 variant predicate type */
    public static <V> EntityVariantPredicate<V> plain(net.minecraft.class_7378.class_7380<V> w, Function<net.minecraft.class_1297, Optional<V>> getter) {
        return new EntityVariantPredicate<>(getter, new EntitySubPredicateType(
            top.dext.centurybridge.step.v1_20_5.rt.CbFinPredicates.variantCodec(w)), w::method_43117);
    }

    /** over a 1.20.5 holder-set variant predicate type: one variant is the set of its holder */
    public static <V> EntityVariantPredicate<V> holders(net.minecraft.class_7378.class_9419<V> w, Function<net.minecraft.class_1297, Optional<V>> getter,
                                                        Function<V, net.minecraft.class_6880<V>> holder) {
        return new EntityVariantPredicate<>(getter, new EntitySubPredicateType(
            top.dext.centurybridge.step.v1_20_5.rt.CbFinPredicates.holderVariantCodec(w)),
            v -> w.method_58356(net.minecraft.class_6885.method_40246(holder.apply(v))));
    }

    /** over a 1.20.5 holder-set variant predicate type whose 1.20.4 variant was the holder itself (painting) */
    public static <V> EntityVariantPredicate<net.minecraft.class_6880<V>> holderValued(net.minecraft.class_7378.class_9419<V> w,
            Function<net.minecraft.class_1297, Optional<net.minecraft.class_6880<V>>> getter) {
        return new EntityVariantPredicate<>(getter, new EntitySubPredicateType(
            top.dext.centurybridge.step.v1_20_5.rt.CbFinPredicates.holderVariantCodec(w)),
            h -> w.method_58356(net.minecraft.class_6885.method_40246(h)));
    }

    /** create(registry, getter) */
    public static <V> EntityVariantPredicate<V> method_43116(net.minecraft.class_2378<V> registry, Function<net.minecraft.class_1297, Optional<V>> getter) {
        return plain(net.minecraft.class_7378.class_7380.method_43116(registry, getter), getter);
    }

    /** create(codec, getter) */
    public static <V> EntityVariantPredicate<V> method_47840(com.mojang.serialization.Codec<V> codec, Function<net.minecraft.class_1297, Optional<V>> getter) {
        return plain(net.minecraft.class_7378.class_7380.method_47840(codec, getter), getter);
    }

    /** type() */
    public EntitySubPredicateType method_43114() {
        return this.type;
    }

    /** createPredicate(variant) */
    public EntityVariantSubPredicate<V> method_43117(V variant) {
        return new EntityVariantSubPredicate<>(this.type, this.getter, variant, this.make.apply(variant));
    }
}
