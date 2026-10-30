package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import net.minecraft.util.worldupdate.WorldUpgrader;

/**
 * 1.21.11 WorldUpgrader.progressMap (class_1257.field_5762, Reference2FloatMap per dimension): 26.1 keeps the same map as
 * UpgradeProgress.progressMap, held in WorldUpgrader.upgradeProgress (javap 26.1: setDimensionProgress puts into it,
 * getDimensionProgress / WorldUpgrader.dimensionProgress read it). The read answers that map itself, so writes reach it.
 */
public final class CbWallWorldUpgrader {
    private CbWallWorldUpgrader() {
    }

    public static Reference2FloatMap progressMap(WorldUpgrader o) {
        return o.upgradeProgress == null ? null : o.upgradeProgress.progressMap;
    }
}
