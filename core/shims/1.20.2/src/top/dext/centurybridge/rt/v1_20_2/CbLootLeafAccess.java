package top.dext.centurybridge.rt.v1_20_2;

import java.util.List;

/**
 * LeafEntry.builder(factory) for a 1.20.1 factory. LeafEntry's EntryConstructor is a protected
 * nested interface: only code inside a LeafEntry subclass may implement it, so the adapter lives
 * in one. This class is never instantiated.
 */
public abstract class CbLootLeafAccess extends net.minecraft.class_85 {

    private CbLootLeafAccess() {
        super(0, 0, List.of(), List.of());
    }

    /** the 1.20.2 builder, with a factory that hands the 1.20.1 one arrays, as 1.20.1's builder did */
    static net.minecraft.class_85.class_86<?> builder(top.dext.centurybridge.facade.v1_20_2.class_85$class_89 factory) {
        net.minecraft.class_85.class_89 adapted = (weight, quality, conditions, functions) -> factory.build(weight, quality,
            conditions.toArray(new net.minecraft.class_5341[0]), functions.toArray(new net.minecraft.class_117[0]));
        return net.minecraft.class_85.method_434(adapted);
    }
}
