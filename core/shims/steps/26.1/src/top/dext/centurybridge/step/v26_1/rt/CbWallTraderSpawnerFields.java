package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;

/**
 * 1.21.11 WanderingTraderSpawner.spawnDelay / spawnChance (class_3990.field_17729 / field_17730, opened by access
 * wideners): 1.21.11 copied them from the level data in its constructor and wrote every change back
 * (setWanderingTraderSpawnDelay / Chance). 26.1 (javap) keeps the single copy in WanderingTraderData, reached through
 * the spawner's getTraderData() (computeIfAbsent from saved data); reads and writes go there.
 */
public final class CbWallTraderSpawnerFields {
    private CbWallTraderSpawnerFields() {
    }

    public static int spawnDelay(WanderingTraderSpawner s) {
        return s.getTraderData().spawnDelay();
    }

    public static void setSpawnDelay(WanderingTraderSpawner s, int v) {
        s.getTraderData().setSpawnDelay(v);
    }

    public static int spawnChance(WanderingTraderSpawner s) {
        return s.getTraderData().spawnChance();
    }

    public static void setSpawnChance(WanderingTraderSpawner s, int v) {
        s.getTraderData().setSpawnChance(v);
    }
}
