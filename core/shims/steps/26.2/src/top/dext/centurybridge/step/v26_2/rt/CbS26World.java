package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * 26.1.2 world-side members 26.2 dropped whose 26.1.2 body is one call 26.2 still has: each answers with that
 * call (javap of 26.1.2's body).
 */
public final class CbS26World {
    private CbS26World() {}

    /** 26.1.2 BlockPos.getCenter(): Vec3.atCenterOf(this) */
    public static Vec3 getCenter(BlockPos pos) {
        return Vec3.atCenterOf(pos);
    }

    /** 26.1.2 BlockPos.getBottomCenter(): Vec3.atBottomCenterOf(this) */
    public static Vec3 getBottomCenter(BlockPos pos) {
        return Vec3.atBottomCenterOf(pos);
    }

    /** 26.1.2 SimpleRandomFeatureConfiguration.features (a public field): 26.2's CompositeFeatureConfiguration.features() */
    public static net.minecraft.core.HolderSet<net.minecraft.world.level.levelgen.placement.PlacedFeature> features(
            net.minecraft.world.level.levelgen.feature.configurations.CompositeFeatureConfiguration c) {
        return c.features();
    }

    /**
     * 26.1.2 NaturalSpawner.getFilteredSpawningCategories(state, spawnFriendlies, spawnEnemies, spawnPersistent). 26.2's
     * (state, spawnEnemies, spawnPersistent) is the same filter over the same categories in the same order, with friendly
     * categories always kept (javap of both); 26.1.2 dropped them when spawnFriendlies was false.
     */
    public static java.util.List<net.minecraft.world.entity.MobCategory> getFilteredSpawningCategories(
            net.minecraft.world.level.NaturalSpawner.SpawnState state, boolean spawnFriendlies, boolean spawnEnemies,
            boolean spawnPersistent) {
        java.util.List<net.minecraft.world.entity.MobCategory> categories =
            net.minecraft.world.level.NaturalSpawner.getFilteredSpawningCategories(state, spawnEnemies, spawnPersistent);
        if (!spawnFriendlies) {
            categories.removeIf(net.minecraft.world.entity.MobCategory::isFriendly);
        }
        return categories;
    }
}
