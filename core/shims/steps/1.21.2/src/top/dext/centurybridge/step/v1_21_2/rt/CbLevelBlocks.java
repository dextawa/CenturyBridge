package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_2248;

/**
 * 1.21.1's Block.getDescriptionId() on 1.21.2.
 *
 * <p>getDescriptionId (Block.method_9539, overridable) became BlockBehaviour.getDescriptionId (method_63499),
 * final, answering the descriptionId field the constructor took from the properties (and the pending-id
 * backfill fills, CbPendingIds). 1.21.1's own wall blocks overrode it to answer their standing variant's
 * item name; 1.21.2 builds them with overrideDescription instead. A 1.21.1 block class overriding it has its
 * override renamed to {@link Legacy#cb$getDescriptionId} (overrideRenames) and the final 1.21.2 method hands
 * the call to it (CbLevelBlockNameMixin), so every caller -- getName, a call naming the old shape
 * ({@link #method_9539}) -- gets the override's answer, as 1.21.1's virtual dispatch gave it.
 */
public final class CbLevelBlocks {

    /** a 1.21.1 block class's renamed getDescriptionId override */
    public interface Legacy {
        String cb$getDescriptionId();
    }

    private static final ThreadLocal<java.util.Set<Object>> ASKING =
        ThreadLocal.withInitial(() -> java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));

    /** block.getDescriptionId() in 1.21.1's shape, on any block (a mod's super call included): 1.21.2's, which asks the override */
    public static String method_9539(class_2248 self) {
        return self.method_63499();
    }

    /**
     * What 1.21.2's final getDescriptionId answers for a block whose 1.21.1 class overrides getDescriptionId: the
     * override's answer, as every 1.21.1 caller (getName included) got it by virtual dispatch; null for any other
     * block, and inside the override itself -- whose super.getDescriptionId() is the 1.21.2 description id, what
     * 1.21.1's Block.getDescriptionId computed from the registry (CbLevelBlockNameMixin)
     */
    public static String legacyDescriptionId(Object block) {
        if (!(block instanceof Legacy l) || !ASKING.get().add(block)) {
            return null;
        }
        try {
            return l.cb$getDescriptionId();
        } finally {
            ASKING.get().remove(block);
        }
    }

    private CbLevelBlocks() {
    }
}
