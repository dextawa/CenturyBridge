package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's DecoratedPotBlockEntity.Decorations on 1.20.5's PotDecorations, the same record moved
 * out (intermediary keeps class_8526; converted mods are renamed onto it) with each side an
 * Optional&lt;Item&gt; where 1.20.4 kept the Item, brick standing for an undecorated side. Its
 * constructor from four items, EMPTY, save and load (the "sherds" list of item ids, brick for a
 * plain side, in both) are 1.20.5's own; a side reads back as 1.20.4's item, brick when plain, and
 * sorted() is the stream of 1.20.5's ordered() list, which already puts brick there.
 */
public final class CbBlockPot {

    /** a side of the pot as 1.20.4 answered it: the sherd, else brick */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_1792 side(java.util.Optional side) {
        return (net.minecraft.class_1792) side.orElse(net.minecraft.class_1802.field_8621);
    }

    /** Decorations.sorted(): back, left, right, front as a stream */
    @SuppressWarnings("rawtypes")
    public static java.util.stream.Stream sorted(java.util.List ordered) {
        return ordered.stream();
    }

    private CbBlockPot() {
    }
}
