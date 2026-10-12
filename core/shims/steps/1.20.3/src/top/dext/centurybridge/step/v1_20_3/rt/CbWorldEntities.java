package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1688;
import net.minecraft.class_1694;
import net.minecraft.class_1695;
import net.minecraft.class_1696;
import net.minecraft.class_1697;
import net.minecraft.class_1699;
import net.minecraft.class_1700;
import net.minecraft.class_1701;
import net.minecraft.class_1917;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_268;
import net.minecraft.class_270;
import net.minecraft.class_2960;
import net.minecraft.class_1299;
import net.minecraft.class_3532;
import net.minecraft.class_5819;
import top.dext.centurybridge.step.v1_20_3.mixin.CbWorldBaseSpawnerAccess;
import top.dext.centurybridge.step.v1_20_3.mixin.CbWorldItemEntityAccess;

/**
 * Entities: the 1.20.2 entity API on 1.20.3. Wired from s2world.json; each member names the 1.20.2
 * behaviour it keeps, read off the 1.20.2 and 1.20.3 bytecode.
 */
public final class CbWorldEntities {

    // ------------------------------------------------------------------ Bat

    /** 1.20.2 Bat.FLAP_DEGREES_PER_TICK (1.20.3 animates bats with keyframes and dropped it) */
    public static final float FLAP_DEGREES_PER_TICK = 74.48451F;
    /** 1.20.2 Bat.TICKS_PER_FLAP: Mth.ceil(2.4166098F) (1.20.3's field of that name is a float of another meaning) */
    public static final int TICKS_PER_FLAP = class_3532.method_15386(2.4166098F);

    // ------------------------------------------------------------------ Entity.ignoreExplosion()

    /**
     * Entity.ignoreExplosion() became ignoreExplosion(Explosion) in 1.20.3; a 1.20.2 caller asks without an
     * explosion, so none is passed. Every 1.20.3 implementation (Entity, ArmorStand, Warden) ignores the
     * argument, as do the hooks synthesized for 1.20.2 overrides.
     */
    public static class_1927 noExplosion() {
        return null;
    }

    // ------------------------------------------------------------------ Entity.getTeam()

    /**
     * Entity.getTeam() returns a PlayerTeam in 1.20.3 (a Team in 1.20.2). A 1.20.2 override's team becomes
     * the 1.20.3 return value; PlayerTeam is the only Team the game creates, so the cast holds for every
     * team a scoreboard knows. A mod's own Team subclass has no 1.20.3 counterpart and is refused by name.
     */
    public static class_268 playerTeam(class_270 team) {
        if (team == null || team instanceof class_268) {
            return (class_268) team;
        }
        throw new ClassCastException("Entity.getTeam(): " + team.getClass().getName()
            + " is not a PlayerTeam, the only kind of team 1.20.3 lets an entity report");
    }

    // ------------------------------------------------------------------ Player.awardRecipesByKey

    /** Player.awardRecipesByKey(ResourceLocation[]) takes a List since 1.20.3: the same ids, in order */
    public static List<class_2960> recipeList(class_2960[] ids) {
        return ids == null ? null : Arrays.asList(ids);
    }

    /** the other way, for a 1.20.2 override of awardRecipesByKey(ResourceLocation[]) that 1.20.3 calls with a List */
    public static class_2960[] recipeArray(List<class_2960> ids) {
        return ids == null ? null : ids.toArray(new class_2960[0]);
    }

    // ------------------------------------------------------------------ AbstractMinecart.createMinecart

    /**
     * 1.20.2's AbstractMinecart.createMinecart(Level, x, y, z, type), line for line: a new minecart of the
     * type. 1.20.3's takes a ServerLevel and applies an item's name and entity data, which 1.20.2's did not.
     */
    public static class_1688 createMinecart(class_1937 level, double x, double y, double z, class_1688.class_1689 type) {
        if (type == class_1688.class_1689.field_7678) {
            return new class_1694(level, x, y, z);
        }
        if (type == class_1688.class_1689.field_7679) {
            return new class_1696(level, x, y, z);
        }
        if (type == class_1688.class_1689.field_7675) {
            return new class_1701(level, x, y, z);
        }
        if (type == class_1688.class_1689.field_7680) {
            return new class_1699(level, x, y, z);
        }
        if (type == class_1688.class_1689.field_7677) {
            return new class_1700(level, x, y, z);
        }
        if (type == class_1688.class_1689.field_7681) {
            return new class_1697(level, x, y, z);
        }
        return new class_1695(level, x, y, z);
    }

    // ------------------------------------------------------------------ ItemEntity.setThrower(UUID)

    /**
     * 1.20.2's ItemEntity.setThrower(UUID) stored the id; 1.20.3's setThrower(Entity) stores the entity's id
     * and caches the entity. A 1.20.2 call has only the id: it is stored and the cache dropped, which is
     * what 1.20.3 itself does when it reads the id back from saved data (getOwner then looks it up).
     */
    public static void setThrower(class_1542 item, UUID thrower) {
        CbWorldItemEntityAccess access = (CbWorldItemEntityAccess) item;
        access.cb$setThrowerId(thrower);
        access.cb$setCachedThrower(null);
    }

    /** the other way, for a 1.20.2 override of setThrower(UUID) that 1.20.3 calls with the entity */
    public static UUID throwerId(class_1297 thrower) {
        return thrower == null ? null : thrower.method_5667();
    }

    // ------------------------------------------------------------------ BaseSpawner.getOrCreateDisplayEntity

    /**
     * 1.20.2's BaseSpawner.getOrCreateDisplayEntity(level, random, pos), line for line: 1.20.3 dropped the
     * random and draws the spawn data from level.getRandom() instead.
     */
    public static class_1297 getOrCreateDisplayEntity(class_1917 spawner, class_1937 level, class_5819 random, class_2338 pos) {
        CbWorldBaseSpawnerAccess access = (CbWorldBaseSpawnerAccess) spawner;
        if (access.cb$displayEntity() == null) {
            class_2487 tag = access.cb$getOrCreateNextSpawnData(level, random, pos).method_38093();
            if (!tag.method_10573("id", 8)) {
                return null;
            }
            access.cb$setDisplayEntity(class_1299.method_17842(tag, level, java.util.function.Function.identity()));
        }
        return access.cb$displayEntity();
    }

    /** the random a 1.20.2 override of getOrCreateDisplayEntity(level, random, pos) gets when 1.20.3 calls (level, pos) */
    public static class_5819 levelRandom(class_1937 level) {
        return level.method_8409();
    }

    private CbWorldEntities() {
    }
}
