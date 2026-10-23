package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * 1.20.4's ItemParser.TagResult: an item tag and the NBT that followed it, which 1.20.4's parseForTesting answered
 * for {@code #tag{nbt}}. 1.20.5 dropped it (item predicates parse differently); 1.20.4's record as a class (its
 * members public, as a facade's must be).
 */
public final class ItemParserTagResult {

    /** tag */
    public final net.minecraft.class_6885<net.minecraft.class_1792> comp_630;

    /** nbt, or null */
    public final net.minecraft.class_2487 comp_631;

    public ItemParserTagResult(net.minecraft.class_6885<net.minecraft.class_1792> tag, net.minecraft.class_2487 nbt) {
        this.comp_630 = tag;
        this.comp_631 = nbt;
    }

    public net.minecraft.class_6885<net.minecraft.class_1792> comp_630() {
        return this.comp_630;
    }

    public net.minecraft.class_2487 comp_631() {
        return this.comp_631;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ItemParserTagResult r && java.util.Objects.equals(r.comp_630, this.comp_630)
            && java.util.Objects.equals(r.comp_631, this.comp_631);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(this.comp_630) * 31 + java.util.Objects.hashCode(this.comp_631);
    }

    @Override
    public String toString() {
        return "TagResult[tag=" + this.comp_630 + ", nbt=" + this.comp_631 + "]";
    }
}
