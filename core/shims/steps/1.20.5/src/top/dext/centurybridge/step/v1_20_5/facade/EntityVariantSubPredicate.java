package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Optional;
import java.util.function.Function;

/**
 * 1.20.4's EntityVariantPredicate.SubPredicate: the record (type, getter, variant) that matches an entity whose
 * variant (by the getter) is the variant. Matches as 1.20.4's did; its codec is that of the 1.20.5 predicate it
 * stands for (when built by an EntityVariantPredicate), so it reports the type 1.20.5 registered. Members public,
 * as a facade's must be.
 */
public final class EntityVariantSubPredicate<V> implements net.minecraft.class_7376 {

    /** type */
    public final EntitySubPredicateType comp_1776;

    /** getter */
    public final Function<net.minecraft.class_1297, Optional<V>> comp_1777;

    /** variant */
    public final V comp_1778;

    /** the 1.20.5 predicate this stands for, or null (one built by a 1.20.4 constructor call) */
    public final net.minecraft.class_7376 delegate;

    public EntityVariantSubPredicate(EntitySubPredicateType type, Function<net.minecraft.class_1297, Optional<V>> getter, V variant) {
        this(type, getter, variant, null);
    }

    EntityVariantSubPredicate(EntitySubPredicateType type, Function<net.minecraft.class_1297, Optional<V>> getter, V variant,
                              net.minecraft.class_7376 delegate) {
        this.comp_1776 = type;
        this.comp_1777 = getter;
        this.comp_1778 = variant;
        this.delegate = delegate;
    }

    public EntitySubPredicateType comp_1776() {
        return this.comp_1776;
    }

    public Function<net.minecraft.class_1297, Optional<V>> comp_1777() {
        return this.comp_1777;
    }

    public V comp_1778() {
        return this.comp_1778;
    }

    /** matches(entity, level, position): the entity's variant is this one */
    @Override
    public boolean method_22497(net.minecraft.class_1297 entity, net.minecraft.class_3218 level, net.minecraft.class_243 position) {
        return this.comp_1777.apply(entity).filter(v -> v.equals(this.comp_1778)).isPresent();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> method_58152() {
        return (com.mojang.serialization.MapCodec) this.comp_1776.comp_1774;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof EntityVariantSubPredicate<?> p && p.comp_1776.equals(this.comp_1776)
            && p.comp_1777.equals(this.comp_1777) && java.util.Objects.equals(p.comp_1778, this.comp_1778);
    }

    @Override
    public int hashCode() {
        return (this.comp_1776.hashCode() * 31 + this.comp_1777.hashCode()) * 31 + java.util.Objects.hashCode(this.comp_1778);
    }

    @Override
    public String toString() {
        return "SubPredicate[type=" + this.comp_1776 + ", getter=" + this.comp_1777 + ", variant=" + this.comp_1778 + "]";
    }
}
