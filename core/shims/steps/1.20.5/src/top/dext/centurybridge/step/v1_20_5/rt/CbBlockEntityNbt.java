package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's block NBT calls on 1.20.5, which gave each a registry lookup (CbBlockRegistries): the
 * same calls with the registries of the block entity (chunk, world) they are made on. A mod's
 * override of the old shape is called by vanilla through the override hooks; these are a mod's
 * calls.
 *
 * <p>load(tag): 1.20.4's load was both the hook a block entity overrode to read its data and the
 * call that applied a tag to it. 1.20.5 split the two: loadAdditional is the hook, loadCustomOnly
 * applies a tag through it, and loadWithComponents does that and also replaces the block entity's
 * leftover data components with the tag's "components" entry (a 1.20.5-only part of a saved block
 * entity, written by saveWithoutMetadata and kin). A tag carrying that entry is a 1.20.5 save and is
 * applied whole; any other tag only reaches the hook, as in 1.20.4 -- loadWithComponents would read
 * a missing entry as "no components" and drop the ones the block entity has.
 */
public final class CbBlockEntityNbt {

    /** BlockEntity's protected saveAdditional (CbBlockEntityInvoker) */
    public interface Saver {
        void cb$saveAdditional(net.minecraft.class_2487 tag, net.minecraft.class_7225.class_7874 registries);
    }

    /**
     * this.saveAdditional(tag) in a block entity class that inherits it (protected: a subclass's own
     * call): the registries a hooked call on the block entity carries, else its own -- what the
     * rewritten super call of the saveAdditional hook passes (frameDefaults).
     */
    public static void method_11007(net.minecraft.class_2586 self, net.minecraft.class_2487 tag) {
        ((Saver) self).cb$saveAdditional(tag,
            (net.minecraft.class_7225.class_7874) Guard.cbArgOrL(self, 1, CbBlockRegistries.of(self)));
    }

    /** BlockEntity.load(tag) */
    public static void method_11014(net.minecraft.class_2586 self, net.minecraft.class_2487 tag) {
        net.minecraft.class_7225.class_7874 registries = CbBlockRegistries.of(self);
        if (tag.method_10545("components")) {
            self.method_58690(tag, registries);
        } else {
            self.method_58691(tag, registries);
        }
    }

    /** BlockEntity.getUpdateTag() */
    public static net.minecraft.class_2487 method_16887(net.minecraft.class_2586 self) {
        return self.method_16887(CbBlockRegistries.of(self));
    }

    /** BlockEntity.saveWithoutMetadata() */
    public static net.minecraft.class_2487 method_38244(net.minecraft.class_2586 self) {
        return self.method_38244(CbBlockRegistries.of(self));
    }

    /** BlockEntity.saveWithId() */
    public static net.minecraft.class_2487 method_38243(net.minecraft.class_2586 self) {
        return self.method_38243(CbBlockRegistries.of(self));
    }

    /** BlockEntity.saveWithFullMetadata() */
    public static net.minecraft.class_2487 method_38242(net.minecraft.class_2586 self) {
        return self.method_38242(CbBlockRegistries.of(self));
    }

    /** BlockEntity.saveToItem(stack): 1.20.5 stores what 1.20.4 put under BlockEntityTag as the item's block entity data */
    public static void method_38240(net.minecraft.class_2586 self, net.minecraft.class_1799 stack) {
        self.method_38240(stack, CbBlockRegistries.of(self));
    }

    /** BlockEntity.loadStatic(pos, state, tag): no block entity yet, so the running world's registries */
    public static net.minecraft.class_2586 method_11005(net.minecraft.class_2338 pos, net.minecraft.class_2680 state,
            net.minecraft.class_2487 tag) {
        return net.minecraft.class_2586.method_11005(pos, state, tag, CbRegistries.current());
    }

    /** ShulkerBoxBlockEntity.loadFromTag(tag) */
    public static void method_11319(net.minecraft.class_2627 self, net.minecraft.class_2487 tag) {
        self.method_11319(tag, CbBlockRegistries.of(self));
    }

    /** BaseCommandBlock.load(tag): its level is a ServerLevel only on the server, so the running world's registries */
    public static void method_8305(net.minecraft.class_1918 self, net.minecraft.class_2487 tag) {
        self.method_8305(tag, CbRegistries.current());
    }

    /** BaseCommandBlock.save(tag) */
    public static net.minecraft.class_2487 method_8297(net.minecraft.class_1918 self, net.minecraft.class_2487 tag) {
        return self.method_8297(tag, CbRegistries.current());
    }

    /** ForcedChunksSavedData.load(tag): saved data loads with the server's registries */
    public static net.minecraft.class_1932 method_32350(net.minecraft.class_2487 tag) {
        return net.minecraft.class_1932.method_32350(tag, CbRegistries.current());
    }

    /** ForcedChunksSavedData.save(tag) */
    public static net.minecraft.class_2487 method_75(net.minecraft.class_1932 self, net.minecraft.class_2487 tag) {
        return self.method_75(tag, CbRegistries.current());
    }

    /** ChunkAccess.getBlockEntityNbtForSaving(pos) */
    public static net.minecraft.class_2487 method_20598(net.minecraft.class_2791 self, net.minecraft.class_2338 pos) {
        return self.method_20598(pos, CbBlockRegistries.of(self));
    }

    private CbBlockEntityNbt() {
    }
}
