package top.dext.centurybridge.rt.v1_20_2;

import java.util.Arrays;
import java.util.List;

/**
 * CompositeEntryBase.compose(children): 1.20.1 handed it the entries as an array
 * (ComposableEntryContainer[]), 1.20.2 as a List. A 1.20.1 composite's own this.compose(children)
 * (protected: the subclass's call, dispatched to whichever class implements it) reaches 1.20.2's
 * shape with the same entries in the same order; a 1.20.1 implementation behind it gets them back
 * as an array through its override hook (CbLootBridges.combinerArray).
 */
public final class CbLootCompose {

    /**
     * the children 1.20.1 passed as an array, as the list 1.20.2's compose takes (a view of that array).
     * Typed Object[]: ComposableEntryContainer (class_64) is package-private, not nameable here; the
     * reshape passes the call's own ComposableEntryContainer[] (Chain.nameableSupertype).
     */
    public static List<Object> children(Object[] children) {
        return Arrays.asList(children);
    }

    private CbLootCompose() {
    }
}
