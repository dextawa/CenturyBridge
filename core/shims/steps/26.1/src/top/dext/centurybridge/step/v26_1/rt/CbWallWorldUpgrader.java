package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.util.worldupdate.RegionStorageUpgrader;
import net.minecraft.util.worldupdate.WorldUpgrader;

/**
 * 1.21.11 WorldUpgrader's fields (class_1257) on 26.1, which keeps the upgrade's state in UpgradeProgress, held in
 * WorldUpgrader.upgradeProgress (javap 26.1):
 * <ul>
 * <li>progressMap (Reference2FloatMap per dimension) is UpgradeProgress.progressMap (setDimensionProgress puts into it,
 *     dimensionProgress reads it); the read answers that map itself, so writes reach it;</li>
 * <li>running: 1.21.11 set it true when built and false in cancel(), and its upgrade loop stopped once false; 26.1's
 *     cancel() sets UpgradeProgress.canceled, which its upgrade loop checks: running is !canceled;</li>
 * <li>finished, progress (the total), totalChunks, converted, skipped are UpgradeProgress's own (the counts are
 *     AtomicIntegers there, written in place);</li>
 * <li>status is read only here: 1.21.11 kept the shown component, 26.1 computes it from the progress (getStatus);</li>
 * <li>REGEX (region file names, "^r\.(-?[0-9]+)\.(-?[0-9]+)\.mca$") is RegionStorageUpgrader.REGEX; NEW_DIRECTORY_PREFIX
 *     "new_" is RegionStorageUpgrader's constant; the status components carry the translation keys 1.21.11's clinit used,
 *     which 26.1's UpgradeStatusTranslator.Messages.create builds ("optimizeWorld.stage.upgrading." / "finished." + poi,
 *     entities, chunks).</li>
 * </ul>
 */
public final class CbWallWorldUpgrader {
    private CbWallWorldUpgrader() {
    }

    private static final Component UPGRADING_POI = Component.translatable("optimizeWorld.stage.upgrading.poi");
    private static final Component FINISHED_POI = Component.translatable("optimizeWorld.stage.finished.poi");
    private static final Component UPGRADING_ENTITIES = Component.translatable("optimizeWorld.stage.upgrading.entities");
    private static final Component FINISHED_ENTITIES = Component.translatable("optimizeWorld.stage.finished.entities");
    private static final Component UPGRADING_CHUNKS = Component.translatable("optimizeWorld.stage.upgrading.chunks");
    private static final Component FINISHED_CHUNKS = Component.translatable("optimizeWorld.stage.finished.chunks");

    public static Reference2FloatMap progressMap(WorldUpgrader o) {
        return o.upgradeProgress == null ? null : o.upgradeProgress.progressMap;
    }

    public static String newDirectoryPrefix() {
        return "new_";
    }

    public static Pattern regex() {
        return RegionStorageUpgrader.REGEX;
    }

    public static Component statusUpgradingPoi() {
        return UPGRADING_POI;
    }

    public static Component statusFinishedPoi() {
        return FINISHED_POI;
    }

    public static Component statusUpgradingEntities() {
        return UPGRADING_ENTITIES;
    }

    public static Component statusFinishedEntities() {
        return FINISHED_ENTITIES;
    }

    public static Component statusUpgradingChunks() {
        return UPGRADING_CHUNKS;
    }

    public static Component statusFinishedChunks() {
        return FINISHED_CHUNKS;
    }

    public static boolean running(WorldUpgrader o) {
        return !o.upgradeProgress.isCanceled();
    }

    public static void setRunning(WorldUpgrader o, boolean v) {
        o.upgradeProgress.canceled = !v;
    }

    public static boolean finished(WorldUpgrader o) {
        return o.upgradeProgress.isFinished();
    }

    public static void setFinished(WorldUpgrader o, boolean v) {
        o.upgradeProgress.setFinished(v);
    }

    public static float progress(WorldUpgrader o) {
        return o.upgradeProgress.getTotalProgress();
    }

    public static void setProgress(WorldUpgrader o, float v) {
        o.upgradeProgress.setTotalProgress(v);
    }

    public static int totalChunks(WorldUpgrader o) {
        return o.upgradeProgress.getTotalChunks();
    }

    public static void setTotalChunks(WorldUpgrader o, int v) {
        o.upgradeProgress.totalChunks.set(v);
    }

    public static int converted(WorldUpgrader o) {
        return o.upgradeProgress.getConverted();
    }

    public static void setConverted(WorldUpgrader o, int v) {
        o.upgradeProgress.converted.set(v);
    }

    public static int skipped(WorldUpgrader o) {
        return o.upgradeProgress.getSkipped();
    }

    public static void setSkipped(WorldUpgrader o, int v) {
        o.upgradeProgress.skipped.set(v);
    }

    public static Component status(WorldUpgrader o) {
        return o.getStatus();
    }
}
