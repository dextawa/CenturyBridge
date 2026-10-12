package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's LootDataId&lt;T&gt;(LootDataType&lt;T&gt; type, ResourceLocation location), the name of one loot
 * data element (a predicate, item modifier or loot table). 1.20.5 names an element by its registry key
 * instead -- the type's registry and the location (rt/CbLootData.key / id). The record's members as
 * they were: the components, the canonical constructor, equality, hash and text of a record.
 */
public final class LootDataId<T> {

    /** type */
    public final net.minecraft.class_8490<T> comp_1474;

    /** location */
    public final net.minecraft.class_2960 comp_1475;

    public LootDataId(net.minecraft.class_8490<T> type, net.minecraft.class_2960 location) {
        this.comp_1474 = type;
        this.comp_1475 = location;
    }

    /** type() */
    public net.minecraft.class_8490<T> comp_1474() {
        return comp_1474;
    }

    /** location() */
    public net.minecraft.class_2960 comp_1475() {
        return comp_1475;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LootDataId<?> other && java.util.Objects.equals(comp_1474, other.comp_1474)
            && java.util.Objects.equals(comp_1475, other.comp_1475);
    }

    /** a record's hash: each component's, in order, folded by 31 */
    @Override
    public int hashCode() {
        return 31 * java.util.Objects.hashCode(comp_1474) + java.util.Objects.hashCode(comp_1475);
    }

    @Override
    public String toString() {
        return "LootDataId[type=" + comp_1474 + ", location=" + comp_1475 + "]";
    }
}
