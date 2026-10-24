package top.dext.centurybridge.step.v26_1.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.DragonRespawnStage;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.level.storage.WorldData;
import top.dext.centurybridge.step.v26_1.facade.EndDragonFightData;
import top.dext.centurybridge.step.v26_1.mixin.CbWallDragonFightAccess;

/**
 * 1.21.11's dragon fight state (EndDragonFight.Data, facade/EndDragonFightData) on 26.1's EnderDragonFight.
 * <p>
 * 1.21.11 built the End's fight from the world data's "DragonFight" record -- new EndDragonFight(level,
 * seed, data[, origin]) -- and stored the fight's saveData() back into the world data whenever the End
 * saved. 26.1's fight is saved data of the End level itself: constructed from its state, then
 * init(level, seed, origin), which also shuffles the gateway order from the seed when there is none --
 * as 1.21.11's constructor did.
 * <p>
 * WorldData.endDragonFightData() answers what 1.21.11's world data held: a record a mod set, until the End
 * saves over it (1.21.11's saveLevelData stored the fight's own state), else the End fight's state now
 * (what the End's last save would have stored). A record set before the End exists is the one the End's
 * fight starts from, as 1.21.11 built it from the world data (mixin/CbWallDragonFightLevelMixin).
 */
public final class CbWallDragonFight {

    private CbWallDragonFight() {
    }

    /** records mods set on a world data the End has not saved over yet */
    private static final Map<WorldData, EndDragonFightData> SET = Collections.synchronizedMap(new WeakHashMap<>());

    // ---- the record from and to 26.1's fight ----

    /** a mod fight class's own 1.21.11 saveData() (declared below EnderDragonFight), or null */
    private static final ClassValue<java.lang.reflect.Method> OVERRIDE = new ClassValue<>() {
        @Override
        protected java.lang.reflect.Method computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != EnderDragonFight.class; c = c.getSuperclass()) {
                try {
                    java.lang.reflect.Method m = c.getDeclaredMethod("saveData");
                    if (m.getReturnType() == EndDragonFightData.class) {
                        m.setAccessible(true);
                        return m;
                    }
                } catch (NoSuchMethodException e) {
                    // not this class
                }
            }
            return null;
        }
    };

    /** 1.21.11's EndDragonFight.saveData() called on a fight: its class's override, else the fight's state */
    public static EndDragonFightData saveData(EnderDragonFight fight) {
        java.lang.reflect.Method override = OVERRIDE.get(fight.getClass());
        if (override == null) {
            return superSaveData(fight);
        }
        try {
            return (EndDragonFightData) override.invoke(fight);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException r) {
                throw r;
            }
            if (cause instanceof Error err) {
                throw err;
            }
            throw new IllegalStateException(cause);
        }
    }

    /**
     * What the End saves for this fight: 1.21.11 stored saveData()'s answer, so a fight whose class overrides
     * it is saved as that record says (26.1's codec over the fight the record describes); others as they are
     * (mixin/CbWallDragonFightSaveMixin)
     */
    public static EnderDragonFight toSave(EnderDragonFight fight) {
        return OVERRIDE.get(fight.getClass()) == null ? fight : fromData(saveData(fight));
    }

    /** 1.21.11's EndDragonFight.saveData() body (a mod's super.saveData()): the fight's state */
    public static EndDragonFightData superSaveData(EnderDragonFight fight) {
        CbWallDragonFightAccess a = (CbWallDragonFightAccess) fight;
        return new EndDragonFightData(a.cb$needsStateScanning(), a.cb$dragonKilled(), fight.hasPreviouslyKilledDragon(), a.cb$respawnStage() != null,
            Optional.ofNullable(a.cb$dragonUUID()), Optional.ofNullable(a.cb$exitPortalLocation()), Optional.of(new ArrayList<>(a.cb$gateways())));
    }

    /** 26.1's fight in 1.21.11's constructor's starting state (not yet init) */
    public static EnderDragonFight fromData(EndDragonFightData data) {
        return new EnderDragonFight(needsStateScanning(data), dragonKilled(data), previouslyKilled(data), respawnStage(data), noRespawnTime(),
            dragonUUID(data), exitPortalLocation(data), gateways(data), noRespawnCrystals());
    }

    // the 26.1 constructor's arguments (superCtorReshapes of a mod's EndDragonFight subclass)

    public static boolean needsStateScanning(EndDragonFightData data) {
        return data.comp_1540();
    }

    public static boolean dragonKilled(EndDragonFightData data) {
        return data.comp_1541();
    }

    public static boolean previouslyKilled(EndDragonFightData data) {
        return data.comp_1542();
    }

    /** 1.21.11's constructor started a respawning fight at START */
    public static Optional<DragonRespawnStage> respawnStage(EndDragonFightData data) {
        return data.comp_1543() ? Optional.of(DragonRespawnStage.START) : Optional.empty();
    }

    /** a fresh fight's respawn time (1.21.11's field started at 0) */
    public static int noRespawnTime() {
        return 0;
    }

    public static Optional<UUID> dragonUUID(EndDragonFightData data) {
        return data.comp_1544();
    }

    public static Optional<BlockPos> exitPortalLocation(EndDragonFightData data) {
        return data.comp_1545();
    }

    /** the record's gateway order; none is an empty list, which 26.1's init fills shuffled from the seed as 1.21.11's constructor did */
    public static List<Integer> gateways(EndDragonFightData data) {
        return new ArrayList<>(data.comp_1546().orElse(List.of()));
    }

    /** 1.21.11 kept no respawn crystals across a save (its field started null) */
    public static List<EntityReference<EndCrystal>> noRespawnCrystals() {
        return new ArrayList<>();
    }

    // ---- 1.21.11's constructors ----

    /** new EndDragonFight(level, seed, data): origin BlockPos.ZERO, as 1.21.11's delegated */
    public static EnderDragonFight create(ServerLevel level, long seed, EndDragonFightData data) {
        return create(level, seed, data, BlockPos.ZERO);
    }

    /** new EndDragonFight(level, seed, data, origin) */
    public static EnderDragonFight create(ServerLevel level, long seed, EndDragonFightData data, BlockPos origin) {
        EnderDragonFight fight = fromData(data);
        fight.init(level, seed, origin);
        return fight;
    }

    /** post of a mod subclass's super(level, seed, data): 26.1's init with 1.21.11's origin BlockPos.ZERO */
    public static void init(EnderDragonFight fight, ServerLevel level, long seed) {
        fight.init(level, seed, BlockPos.ZERO);
    }

    /** post of a mod subclass's super(level, seed, data, origin) */
    public static void init(EnderDragonFight fight, ServerLevel level, long seed, BlockPos origin) {
        fight.init(level, seed, origin);
    }

    // ---- WorldData ----

    private static ServerLevel end(WorldData data) {
        MinecraftServer s = CbWallLevelData.SERVER;
        if (s == null || s.getWorldData() != data) {
            return null;
        }
        return s.getLevel(Level.END);
    }

    /** 1.21.11 WorldData.endDragonFightData() */
    public static EndDragonFightData endDragonFightData(WorldData data) {
        EndDragonFightData set = SET.get(data);
        if (set != null) {
            return set;
        }
        ServerLevel end = end(data);
        if (end == null || end.getDragonFight() == null) {
            throw new IllegalStateException(data + ": 1.21.11 world data's dragon fight read with no End of a running server;"
                + " 26.1 keeps the fight in the End level's saved data");
        }
        return saveData(end.getDragonFight());
    }

    /** 1.21.11 WorldData.setEndDragonFightData(data) */
    public static void setEndDragonFightData(WorldData data, EndDragonFightData fight) {
        SET.put(data, fight);
    }

    /** the End is built: a record set before it is the one its fight starts from (1.21.11 built it from the world data) */
    public static EndDragonFightData takeForNewEnd(WorldData data) {
        return SET.remove(data);
    }

    /** the End saved: 1.21.11 stored the fight's own state over whatever the world data held */
    public static void endSaved(WorldData data) {
        SET.remove(data);
    }
}
