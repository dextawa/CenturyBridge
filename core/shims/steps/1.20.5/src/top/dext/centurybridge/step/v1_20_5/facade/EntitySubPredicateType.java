package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's EntitySubPredicate.Type: a record over the sub-predicate's map codec. 1.20.5 dropped it -- a
 * sub-predicate answers its codec itself, which is registered in BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE --
 * so the type of any 1.20.5 sub-predicate is the record over its codec (rt/CbFinPredicates.type). 1.20.4's
 * record as a class (members public, as a facade's must be).
 */
public final class EntitySubPredicateType {

    /** codec */
    public final com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> comp_1774;

    public EntitySubPredicateType(com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> codec) {
        this.comp_1774 = codec;
    }

    public com.mojang.serialization.MapCodec<? extends net.minecraft.class_7376> comp_1774() {
        return this.comp_1774;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof EntitySubPredicateType t && t.comp_1774 == this.comp_1774;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this.comp_1774);
    }

    @Override
    public String toString() {
        return "Type[codec=" + this.comp_1774 + "]";
    }
}
