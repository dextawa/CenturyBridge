package top.dext.centurybridge.rt.v26_2;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import top.dext.centurybridge.shims.v26_2.TagValueInputAccessor;
import top.dext.centurybridge.shims.v26_2.TagValueOutputAccessor;

/**
 * 1.20.1 block entities and entities saved themselves into an NBT compound (readNbt /
 * writeNbt, readCustomDataFromNbt / writeCustomDataToNbt). 1.21.6 replaced the compound
 * with ValueInput / ValueOutput, so an old override is never called: a converted mod's
 * chest forgets its contents, its entity its state, at the first save.
 *
 * The converter synthesizes loadAdditional / saveAdditional (block entities) and
 * readAdditionalSaveData / addAdditionalSaveData (entities) calling the bridges below;
 * they hand the old override the very compound the tag-backed input reads from, or a
 * fresh one whose entries are merged into the output -- the same bytes on disk as before.
 * An old super call (the vanilla parent's part: a lootable container's loot table and
 * lock, a living entity's health) goes to the parent's new method through the guard.
 */
public final class CbNbtStorage {

    private static boolean warned;

    /** the compound behind a ValueInput; vanilla's are all tag-backed */
    public static CompoundTag tagOf(ValueInput in) {
        if (in instanceof TagValueInput) {
            return ((TagValueInputAccessor) in).cbInput();
        }
        warnOnce(in);
        return new CompoundTag();
    }

    /** a compound's entries written into a ValueOutput */
    public static void write(ValueOutput out, CompoundTag tag) {
        if (out instanceof TagValueOutput) {
            ((TagValueOutputAccessor) out).cbOutput().merge(tag);
            return;
        }
        warnOnce(out);
    }

    private static void warnOnce(Object io) {
        if (!warned) {
            warned = true;
            org.slf4j.LoggerFactory.getLogger("CenturyBridge").warn(
                "{} is not tag-backed: a 1.20.1 block entity / entity's own data is not carried through it", io.getClass());
        }
    }

    // ---- overrideHooks bridges (the synthesized new shapes) ----

    public static void loadAdditional(CbLegacyBlockEntityRead self, ValueInput in) {
        self.loadAdditional(tagOf(in));
    }

    public static void saveAdditional(CbLegacyBlockEntityWrite self, ValueOutput out) {
        CompoundTag tag = new CompoundTag();
        self.saveAdditional(tag);
        write(out, tag);
    }

    public static void readAdditionalSaveData(CbLegacyEntityRead self, ValueInput in) {
        self.readAdditionalSaveData(tagOf(in));
    }

    public static void addAdditionalSaveData(CbLegacyEntityWrite self, ValueOutput out) {
        CompoundTag tag = new CompoundTag();
        self.addAdditionalSaveData(tag);
        write(out, tag);
    }

    // ---- instanceRedirects: calls to the old shapes, super calls above all ----

    private static HolderLookup.Provider lookup(net.minecraft.world.level.Level level) {
        return level != null ? level.registryAccess() : registries();
    }

    /**
     * The registries an item stack's components decode against (enchantments are
     * data-driven): the running server's, else the client level's, else the built-in
     * static ones before any world exists.
     */
    public static HolderLookup.Provider registries() {
        net.minecraft.server.MinecraftServer server = Trackers.SERVER;
        if (server != null) {
            return server.registryAccess();
        }
        net.minecraft.world.level.Level client = Statics.cbTooltipLevel();
        return client != null ? client.registryAccess() : CbProvisionalComponents.staticRegistries();
    }

    // ---- Inventories (ContainerHelper) item lists: NBT in 1.20.1, ValueInput/Output since 1.21.6 ----

    /** Inventories.readNbt(nbt, stacks) */
    public static void method_5429(CompoundTag nbt,
            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> stacks) {
        net.minecraft.world.ContainerHelper.loadAllItems(
            TagValueInput.create(ProblemReporter.DISCARDING, registries(), nbt), stacks);
    }

    /** Inventories.writeNbt(nbt, stacks): also writes an empty list, as 1.20.1 did */
    public static CompoundTag method_5426(CompoundTag nbt,
            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> stacks) {
        return method_5427(nbt, stacks, true);
    }

    /** Inventories.writeNbt(nbt, stacks, setIfEmpty) */
    public static CompoundTag method_5427(CompoundTag nbt,
            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> stacks, boolean setIfEmpty) {
        TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries());
        net.minecraft.world.ContainerHelper.saveAllItems(out, stacks, setIfEmpty);
        nbt.merge(out.buildResult());
        return nbt;
    }

    /** BlockEntity.readNbt(nbt): the block entity's own load, from that compound */
    public static void method_11014(BlockEntity self, CompoundTag nbt) {
        self.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, lookup(self.getLevel()), nbt));
    }

    /** BlockEntity.readNbt(nbt, registries) (the 1.20.5 shape) */
    public static void method_11014(BlockEntity self, CompoundTag nbt, HolderLookup.Provider registries) {
        self.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, registries, nbt));
    }

    /** BlockEntity.writeNbt(nbt): the block entity's own save, into that compound */
    public static void method_11007(BlockEntity self, CompoundTag nbt) {
        TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, lookup(self.getLevel()));
        self.saveAdditional(out);
        nbt.merge(out.buildResult());
    }

    /** BlockEntity.writeNbt(nbt, registries) (the 1.20.5 shape) */
    public static void method_11007(BlockEntity self, CompoundTag nbt, HolderLookup.Provider registries) {
        TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        self.saveAdditional(out);
        nbt.merge(out.buildResult());
    }

    /** Entity.readCustomDataFromNbt(nbt) */
    public static void method_5749(Entity self, CompoundTag nbt) {
        self.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, lookup(self.level()), nbt));
    }

    /** Entity.writeCustomDataToNbt(nbt) */
    public static void method_5652(Entity self, CompoundTag nbt) {
        TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, lookup(self.level()));
        self.addAdditionalSaveData(out);
        nbt.merge(out.buildResult());
    }

    private CbNbtStorage() {
    }
}
