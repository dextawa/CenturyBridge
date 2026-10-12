package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * The registries a block's NBT goes through on 1.20.5, which threads a HolderLookup.Provider
 * through every NBT entry point 1.20.4 had without one: a block entity's own level's registries
 * when it has a level -- what 1.20.5 itself hands a block entity it loads in a level (LevelChunk's
 * block entity loading passes level.registryAccess() to BlockEntity.loadStatic) -- else the running
 * world's (CbRegistries.current()). These are also the frame defaults of the block entity and chunk
 * NBT hooks: what the rewritten super call passes when the mod calls its own old method outside a
 * hooked call, where vanilla passed nothing.
 */
public final class CbBlockRegistries {

    /** a block entity's registries: its level's, else the running world's */
    public static net.minecraft.class_7225.class_7874 of(net.minecraft.class_2586 be) {
        net.minecraft.class_1937 level = be.method_10997();
        return level != null ? level.method_30349() : CbRegistries.current();
    }

    /** a chunk's registries: a level chunk's level's, else (a proto chunk has no level) the running world's */
    public static net.minecraft.class_7225.class_7874 of(net.minecraft.class_2791 chunk) {
        return chunk instanceof net.minecraft.class_2818 lc && lc.method_12200() != null
            ? lc.method_12200().method_30349() : CbRegistries.current();
    }

    private CbBlockRegistries() {
    }
}
