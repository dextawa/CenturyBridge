package top.dext.centurybridge.step.v26_1.rt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.storage.SavedDataStorage;
import top.dext.centurybridge.step.v26_1.facade.LegacyTagFixer;

/**
 * 1.21.11's SimpleRegionStorage legacy pass on 26.1's storage. 1.21.11's storage could be built with a
 * LegacyTagFixer supplier: upgradeChunkTag(tag, version, context) ran the fixer on a chunk older than the
 * current version, then the data fixers from the later of the fixer's target and the chunk's own version, and
 * markChunkDone(pos) told the fixer. 26.1's storage has no fixer; the supplier a 1.21.11 constructor gave is kept
 * per storage here (none: 1.21.11's EMPTY) and these are 1.21.11's bodies over 26.1's upgrade.
 */
public final class CbWallRegionStorage {

    private static final Map<SimpleRegionStorage, Supplier<LegacyTagFixer>> FIXERS = new WeakHashMap<>();

    private CbWallRegionStorage() {
    }

    /** post of 1.21.11's SimpleRegionStorage(.., legacyFixer) */
    public static void fixer(SimpleRegionStorage storage, Supplier<LegacyTagFixer> fixer) {
        synchronized (FIXERS) {
            FIXERS.put(storage, fixer);
        }
    }

    /** post of 1.21.11's RecreatingSimpleRegionStorage(.., legacyFixer) */
    public static void fixer(net.minecraft.world.level.chunk.storage.RecreatingSimpleRegionStorage storage, Supplier<LegacyTagFixer> fixer) {
        synchronized (FIXERS) {
            FIXERS.put(storage, fixer);
        }
    }

    private static LegacyTagFixer fixerOf(SimpleRegionStorage storage) {
        Supplier<LegacyTagFixer> s;
        synchronized (FIXERS) {
            s = FIXERS.get(storage);
        }
        return (s != null ? s : LegacyTagFixer.field_63959).get();
    }

    /** 1.21.11 SimpleRegionStorage.upgradeChunkTag(tag, version, contextTag) */
    public static CompoundTag upgradeChunkTag(SimpleRegionStorage storage, CompoundTag tag, int version, CompoundTag context) {
        int current = SharedConstants.getCurrentVersion().dataVersion().version();
        int from = NbtUtils.getDataVersion(tag, version);
        if (from == current) {
            return tag;
        }
        try {
            LegacyTagFixer fixer = fixerOf(storage);
            tag = fixer.applyFix(tag);
            // 26.1's upgrade reads the tag's own data version: the later of the fixer's target and the chunk's
            NbtUtils.addDataVersion(tag, Math.max(fixer.targetDataVersion(), from));
            return storage.upgradeChunkTag(tag, from, context, current);
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Updated chunk");
            report.addCategory("Updated chunk details").setDetail("Data version", from);
            throw new ReportedException(report);
        }
    }

    /** 1.21.11 SimpleRegionStorage.markChunkDone(pos): the fixer told */
    public static void markChunkDone(SimpleRegionStorage storage, ChunkPos pos) {
        fixerOf(storage).markChunkDone(pos);
    }

    /**
     * 1.21.11 DimensionDataStorage.getDataFile(name) (private; reached by mixin shadows, e.g. the 1.20.2 layer's
     * CbSavedDataReadCover): 1.21.11's body, dataFolder.resolve(name + ".dat"). 26.1's getDataFile takes an
     * Identifier and resolves {@code <namespace>/<path>.dat}, which a plain 1.21.11 name has no spelling for
     */
    public static Path getDataFile(SavedDataStorage storage, String name) {
        return CbWallSavedData.legacyFile(storage.dataFolder, name);
    }

    /** 1.21.11 DimensionDataStorage.readTagFromDisk(name, type, version): the file named so in its data folder */
    public static CompoundTag readTagFromDisk(SavedDataStorage storage, String name, DataFixTypes type, int version) throws IOException {
        return storage.readTagFromDisk(storage.dataFolder.resolve(name + ".dat"), type, version);
    }
}
