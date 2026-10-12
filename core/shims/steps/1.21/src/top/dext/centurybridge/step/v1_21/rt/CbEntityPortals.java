package top.dext.centurybridge.step.v1_21.rt;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.Optional;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_1946;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2423;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2784;
import net.minecraft.class_2874;
import net.minecraft.class_2902;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3230;
import net.minecraft.class_4048;
import net.minecraft.class_4153;
import net.minecraft.class_4156;
import net.minecraft.class_5454;
import net.minecraft.class_5459;
import net.minecraft.class_7477;
import net.minecraft.class_9787;
import net.minecraft.class_9797;
import org.slf4j.Logger;
import top.dext.centurybridge.step.v1_21.facade.PortalInfo;

/**
 * Dimension changes and portals. 1.20.6 moved an entity with changeDimension(ServerLevel), which asked the
 * entity's own findDimensionEntryPoint(dest) where to arrive (and, between overworld and nether, its
 * getExitPortal for the portal there); a nether portal marked the entity with handleInsidePortal(pos) and its
 * handleNetherPortal() tick counted getPortalWaitTime() ticks. 1.21 asks the portal block instead
 * (Portal.getPortalDestination answers a DimensionTransition carrying the level and what follows the move) and
 * moves the entity with changeDimension(DimensionTransition); the marking and counting live in a
 * PortalProcessor. Here 1.20.6's rules are rebuilt on 1.21's API for a 1.20.6 caller, and a 1.20.6 subclass's
 * overrides are asked where 1.20.6 asked them (the override hooks, superRedirects and portal-block mixins of
 * s6world.json). Each static says which 1.20.6 body it is.
 */
public final class CbEntityPortals {

    private static final Logger LOGGER = LogUtils.getLogger();

    // ------------------------------------------------------------------------------------------------ adapters
    /** a 1.20.6 entity class's own findDimensionEntryPoint(dest) */
    public interface LegacyEntryPoint {
        PortalInfo method_30329(class_3218 dest);
    }

    /** a 1.20.6 entity class's own getExitPortal(dest, pos, toNether, border) */
    public interface LegacyExitPortal {
        Optional<class_5459.class_5460> method_30330(class_3218 dest, class_2338 pos, boolean toNether, class_2784 border);
    }

    /** a 1.20.6 entity class's own changeDimension(dest) */
    public interface LegacyChangeDimension {
        class_1297 method_5731(class_3218 dest);
    }

    /** a 1.20.6 entity class's own handleInsidePortal(pos) */
    public interface LegacyInsidePortal {
        void method_5717(class_2338 pos);
    }

    /** a 1.20.6 entity class's own handleNetherPortal() */
    public interface LegacyNetherPortalTick {
        void method_18379();
    }

    /** a 1.20.6 entity class's own getPortalWaitTime() */
    public interface LegacyPortalWaitTime {
        int method_5741();
    }

    /** what the layer's Entity mixin adds: 1.20.6's portalEntrancePos, and handlePortal() for the statics here */
    public interface EntityPortalState {
        class_2338 cb$portalEntrancePos();

        void cb$setPortalEntrancePos(class_2338 pos);

        void cb$handlePortal();
    }

    private static class_2423 netherPortal() {
        return (class_2423) class_2246.field_10316;
    }

    private static boolean isNetherPortal(class_9797 portal) {
        return portal == class_2246.field_10316;
    }

    // ------------------------------------------------------------------------------------------------ entry points (1.20.6 rules)
    /**
     * 1.20.6 findDimensionEntryPoint(dest) as a call: the entity's own override answers; else 1.20.6's body for
     * the entity -- ServerPlayer's (Entity's, moved one block down onto the platform, motionless and turned to
     * 90 degrees when going from the overworld to the end) or Entity's.
     */
    public static PortalInfo findDimensionEntryPoint(class_1297 entity, class_3218 dest) {
        if (entity instanceof LegacyEntryPoint legacy) {
            return legacy.method_30329(dest);
        }
        return vanillaEntryPoint(entity, dest);
    }

    /** a 1.20.6 subclass's super.findDimensionEntryPoint(dest): the 1.20.6 body of the class it extends */
    public static PortalInfo superFindDimensionEntryPoint(class_1297 entity, class_3218 dest) {
        return vanillaEntryPoint(entity, dest);
    }

    public static PortalInfo superFindDimensionEntryPoint(class_3222 player, class_3218 dest) {
        return vanillaEntryPoint(player, dest);
    }

    private static PortalInfo vanillaEntryPoint(class_1297 entity, class_3218 dest) {
        PortalInfo info = entityEntryPoint(entity, dest);
        if (entity instanceof class_3222 && info != null && entity.method_37908().method_27983() == class_1937.field_25179
                && dest.method_27983() == class_1937.field_25181) {
            return new PortalInfo(info.field_25879.method_1031(0.0, -1.0, 0.0), class_243.field_1353, 90.0F, 0.0F);
        }
        return info;
    }

    /** 1.20.6 Entity.findDimensionEntryPoint(dest) */
    private static PortalInfo entityEntryPoint(class_1297 entity, class_3218 dest) {
        class_1937 here = entity.method_37908();
        boolean endToOverworld = here.method_27983() == class_1937.field_25181 && dest.method_27983() == class_1937.field_25179;
        boolean toEnd = dest.method_27983() == class_1937.field_25181;
        if (!endToOverworld && !toEnd) {
            boolean toNether = dest.method_27983() == class_1937.field_25180;
            if (here.method_27983() != class_1937.field_25180 && !toNether) {
                return null;
            }
            class_2784 border = dest.method_8621();
            double scale = class_2874.method_31109(here.method_8597(), dest.method_8597());
            class_2338 target = border.method_39538(entity.method_23317() * scale, entity.method_23318(), entity.method_23321() * scale);
            return getExitPortal(entity, dest, target, toNether, border)
                .map(rect -> arrival(entity, dest, rect))
                .orElse(null);
        }
        class_2338 spawn = toEnd ? class_3218.field_25144 : dest.method_43126();
        dest.method_14178().method_17297(class_3230.field_19280, new class_1923(spawn), 3, spawn);
        int y = toEnd ? spawn.method_10264()
            : dest.method_8500(spawn).method_12005(class_2902.class_2903.field_13203, spawn.method_10263(), spawn.method_10260()) + 1;
        return new PortalInfo(new class_243(spawn.method_10263() + 0.5, y, spawn.method_10260() + 0.5),
            entity.method_18798(), entity.method_36454(), entity.method_36455());
    }

    /** the lambda of 1.20.6's findDimensionEntryPoint: where in the exit portal, from where in the entrance */
    private static PortalInfo arrival(class_1297 entity, class_3218 dest, class_5459.class_5460 exit) {
        class_1937 here = entity.method_37908();
        class_2338 entrance = portalEntrancePos(entity);
        class_2680 state = entrance == null ? null : here.method_8320(entrance);
        class_2350.class_2351 axis;
        class_243 relative;
        if (state != null && state.method_28498(class_2741.field_12529)) {
            axis = state.method_11654(class_2741.field_12529);
            class_5459.class_5460 from = class_5459.method_30574(entrance, axis, 21, class_2350.class_2351.field_11052, 21,
                p -> here.method_8320(p) == state);
            relative = entity.method_30633(axis, from);
        } else {
            axis = class_2350.class_2351.field_11048;
            relative = new class_243(0.5, 0.0, 0.0);
        }
        return createPortalInfo(dest, exit, axis, relative, entity, entity.method_18798(), entity.method_36454(), entity.method_36455());
    }

    /**
     * 1.20.6 PortalShape.createPortalInfo(level, exit, axis, offset, entity, speed, yRot, xRot): the arrival in
     * the exit rectangle -- turned by 90 degrees and the motion with it when the portals' axes differ -- moved to
     * the nearest spot the entity fits (PortalShape.findCollisionFreePosition, unchanged in 1.21).
     */
    public static PortalInfo createPortalInfo(class_3218 level, class_5459.class_5460 exit, class_2350.class_2351 axis,
                                              class_243 offset, class_1297 entity, class_243 speed, float yRot, float xRot) {
        class_2338 corner = exit.field_25936;
        class_2680 state = level.method_8320(corner);
        class_2350.class_2351 exitAxis = state.method_28500(class_2741.field_12529).orElse(class_2350.class_2351.field_11048);
        double width = exit.field_25937;
        double height = exit.field_25938;
        class_4048 dims = entity.method_18377(entity.method_18376());
        int turn = axis == exitAxis ? 0 : 90;
        class_243 motion = axis == exitAxis ? speed : new class_243(speed.field_1350, speed.field_1351, -speed.field_1352);
        double across = (double) dims.comp_2185() / 2.0 + (width - (double) dims.comp_2185()) * offset.method_10216();
        double up = (height - (double) dims.comp_2186()) * offset.method_10214();
        double along = 0.5 + offset.method_10215();
        boolean xAxis = exitAxis == class_2350.class_2351.field_11048;
        class_243 at = new class_243(corner.method_10263() + (xAxis ? across : along), corner.method_10264() + up,
            corner.method_10260() + (xAxis ? along : across));
        class_243 free = net.minecraft.class_2424.method_47382(at, level, entity, dims);
        return new PortalInfo(free, motion, yRot + (float) turn, xRot);
    }

    /**
     * 1.20.6 getExitPortal(dest, pos, toNether, border) as a call: the entity's own override answers; else
     * Entity's body (the portal forcer's findPortalAround) or, for a player, ServerPlayer's (and when there is
     * none, a new portal facing the entrance's axis).
     */
    public static Optional<class_5459.class_5460> getExitPortal(class_1297 entity, class_3218 dest, class_2338 pos, boolean toNether,
                                                                class_2784 border) {
        if (entity instanceof LegacyExitPortal legacy) {
            return legacy.method_30330(dest, pos, toNether, border);
        }
        return vanillaExitPortal(entity, dest, pos, toNether, border);
    }

    public static Optional<class_5459.class_5460> superGetExitPortal(class_1297 entity, class_3218 dest, class_2338 pos, boolean toNether,
                                                                     class_2784 border) {
        return vanillaExitPortal(entity, dest, pos, toNether, border);
    }

    public static Optional<class_5459.class_5460> superGetExitPortal(class_3222 player, class_3218 dest, class_2338 pos, boolean toNether,
                                                                     class_2784 border) {
        return vanillaExitPortal(player, dest, pos, toNether, border);
    }

    private static Optional<class_5459.class_5460> vanillaExitPortal(class_1297 entity, class_3218 dest, class_2338 pos, boolean toNether,
                                                                     class_2784 border) {
        Optional<class_5459.class_5460> found = findPortalAround(dest, pos, toNether, border);
        if (!(entity instanceof class_3222) || found.isPresent()) {
            return found;
        }
        class_2338 entrance = portalEntrancePos(entity);
        class_2350.class_2351 axis = entrance == null ? class_2350.class_2351.field_11048
            : entity.method_37908().method_8320(entrance).method_28500(class_2423.field_11310).orElse(class_2350.class_2351.field_11048);
        Optional<class_5459.class_5460> made = dest.method_14173().method_30482(pos, axis);
        if (made.isEmpty()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
        }
        return made;
    }

    /**
     * 1.20.6 PortalForcer.findPortalAround(pos, toNether, border): the nearest nether-portal point of interest
     * inside the border (16 blocks out from a nether search, 128 otherwise; nearest first, then lowest) that is
     * still a portal block, a portal ticket on its chunk, and the largest portal rectangle around it.
     */
    public static Optional<class_5459.class_5460> findPortalAround(class_3218 level, class_2338 pos, boolean toNether, class_2784 border) {
        class_4153 pois = level.method_19494();
        int radius = toNether ? 16 : 128;
        pois.method_22439(level, pos, radius);
        return pois.method_22383(type -> type.method_40225(class_7477.field_39295), pos, radius, class_4153.class_4155.field_18489)
            .filter(p -> border.method_11952(p.method_19141()))
            .sorted(java.util.Comparator.<class_4156>comparingDouble(p -> p.method_19141().method_10262(pos))
                .thenComparingInt(p -> p.method_19141().method_10264()))
            .filter(p -> level.method_8320(p.method_19141()).method_28498(class_2741.field_12529))
            .findFirst()
            .map(p -> {
                class_2338 at = p.method_19141();
                level.method_14178().method_17297(class_3230.field_19280, new class_1923(at), 3, at);
                class_2680 state = level.method_8320(at);
                return class_5459.method_30574(at, state.method_11654(class_2741.field_12529), 21, class_2350.class_2351.field_11052, 21,
                    q -> level.method_8320(q) == state);
            });
    }

    /** PortalForcer.findPortalAround as a 1.20.6 call names it: the forcer's own level */
    public static Optional<class_5459.class_5460> findPortalAround(class_1946 forcer, class_2338 pos, boolean toNether, class_2784 border) {
        return findPortalAround(((PortalForcerLevel) forcer).cb$level(), pos, toNether, border);
    }

    /** what the layer's PortalForcer accessor exposes */
    public interface PortalForcerLevel {
        class_3218 cb$level();
    }

    // ------------------------------------------------------------------------------------------------ the move
    /** 1.21 transitions a 1.20.6 changeDimension override is handed, innermost first, per entity */
    private static final ThreadLocal<ArrayDeque<Object[]>> TRANSITIONS = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 1.20.6 changeDimension(dest) as a call: the entity's own override answers; else 1.20.6's body (see
     * {@link #legacyChangeDimension}).
     */
    public static class_1297 changeDimension(class_1297 entity, class_3218 dest) {
        if (entity instanceof LegacyChangeDimension legacy) {
            return legacy.method_5731(dest);
        }
        return legacyChangeDimension(entity, dest);
    }

    /**
     * 1.21's changeDimension(transition) on a 1.20.6 subclass that overrides changeDimension(dest): the override
     * runs for the transition's level, and its super call moves the entity by the transition 1.21 computed.
     */
    public static class_1297 changeDimension(LegacyChangeDimension self, class_5454 transition) {
        ArrayDeque<Object[]> frames = TRANSITIONS.get();
        frames.push(new Object[] {self, transition});
        try {
            return self.method_5731(transition.comp_2820());
        } finally {
            frames.pop();
        }
    }

    /**
     * A 1.20.6 subclass's super.changeDimension(dest), whichever vanilla class it extends: the move vanilla
     * makes for that class in 1.21 (ItemEntity merging, a villager ending its trade -- the same work 1.20.6's
     * overrides did), with the 1.21 transition behind the call when there is one for this level, else 1.20.6's.
     * The guard is held across the call, so the subclass's synthesized changeDimension(transition) passes it
     * straight on to vanilla's instead of back into the override.
     */
    public static class_1297 superChangeDimension(class_1297 entity, class_3218 dest) {
        return guardedMove(entity, dest);
    }

    public static class_1297 superChangeDimension(class_3222 player, class_3218 dest) {
        return guardedMove(player, dest);
    }

    public static class_1297 superChangeDimension(net.minecraft.class_1542 item, class_3218 dest) {
        return guardedMove(item, dest);
    }

    public static class_1297 superChangeDimension(net.minecraft.class_3988 villager, class_3218 dest) {
        return guardedMove(villager, dest);
    }

    /** ThrownEnderpearl's 1.20.6 changeDimension: the pearl lets go of an owner in another dimension, then moves */
    public static class_1297 superChangeDimension(net.minecraft.class_1684 pearl, class_3218 dest) {
        class_1297 owner = pearl.method_24921();
        if (owner != null && owner.method_37908().method_27983() != dest.method_27983()) {
            pearl.method_7432(null);
        }
        return guardedMove(pearl, dest);
    }

    private static class_1297 guardedMove(class_1297 entity, class_3218 dest) {
        for (Object[] f : TRANSITIONS.get()) {
            if (f[0] == entity && ((class_5454) f[1]).comp_2820() == dest) {
                return guarded(entity, (class_5454) f[1]);
            }
        }
        return legacyMove(entity, dest, true);
    }

    private static class_1297 guarded(class_1297 entity, class_5454 transition) {
        boolean entered = Guard.cbEnter(entity);
        try {
            return entity.method_5731(transition);
        } finally {
            if (entered) {
                Guard.cbExit(entity);
            }
        }
    }

    /** 1.20.6's changeDimension(dest) body for the entity, on 1.21's move */
    public static class_1297 legacyChangeDimension(class_1297 entity, class_3218 dest) {
        return legacyMove(entity, dest, false);
    }

    /**
     * 1.20.6's changeDimension(dest): nothing off a server level or for a removed entity; a player leaving the
     * end for the overworld sees the credits instead (ServerPlayer.showEndCredits is 1.20.6's branch exactly);
     * else the entry point 1.20.6 asked the entity for -- none, no move -- and the move. 1.20.6 left passengers
     * behind (unRide), so they are let go first; a player arriving in the end gets 1.20.6's player platform
     * before the move, any other entity the obsidian spawn platform after it; a player hears the portal travel
     * sound (level event 1032), as 1.20.6 sent it. 1.20.6's ServerPlayer had already sent the respawn packet when
     * it found no entry point and left the player in neither level; here there is no transfer at all.
     */
    private static class_1297 legacyMove(class_1297 entity, class_3218 dest, boolean guard) {
        if (!(entity.method_37908() instanceof class_3218 here) || entity.method_31481()) {
            return null;
        }
        if (entity instanceof class_3222 player && here.method_27983() == class_1937.field_25181
                && dest.method_27983() == class_1937.field_25179) {
            player.method_60594();
            return player;
        }
        PortalInfo info = findDimensionEntryPoint(entity, dest);
        if (info == null) {
            return null;
        }
        entity.method_18375();
        boolean toEnd = dest.method_27983() == class_1937.field_25181;
        class_5454.class_9823 after;
        if (entity instanceof class_3222) {
            if (toEnd) {
                createEndPlatform(dest, class_2338.method_49638(info.field_25879));
            }
            after = class_5454.field_52246;
        } else {
            after = toEnd ? moved -> makeObsidianPlatform(dest) : class_5454.field_52245;
        }
        class_5454 transition = new class_5454(dest, info.field_25879, info.field_25880, info.field_25881, info.field_25882, after);
        return guard ? guarded(entity, transition) : entity.method_5731(transition);
    }

    /** 1.20.6 ServerPlayer.createEndPlatform(level, pos): obsidian under a 5x5 area, three blocks of air above it */
    private static void createEndPlatform(class_3218 level, class_2338 pos) {
        class_2338.class_2339 at = pos.method_25503();
        for (int z = -2; z <= 2; z++) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y < 3; y++) {
                    class_2680 state = y == -1 ? class_2246.field_10540.method_9564() : class_2246.field_10124.method_9564();
                    level.method_8501(at.method_10101(pos).method_10100(x, y, z), state);
                }
            }
        }
    }

    /** 1.20.6 ServerLevel.makeObsidianPlatform(level): the 5x5 obsidian platform two below the end spawn, air above */
    public static void makeObsidianPlatform(class_3218 level) {
        class_2338 spawn = class_3218.field_25144;
        int x = spawn.method_10263();
        int y = spawn.method_10264() - 2;
        int z = spawn.method_10260();
        class_2338.method_10094(x - 2, y + 1, z - 2, x + 2, y + 3, z + 2)
            .forEach(p -> level.method_8501(p, class_2246.field_10124.method_9564()));
        class_2338.method_10094(x - 2, y, z - 2, x + 2, y, z + 2)
            .forEach(p -> level.method_8501(p, class_2246.field_10540.method_9564()));
    }

    // ------------------------------------------------------------------------------------------------ 1.21 portal blocks asking 1.20.6 overrides
    /**
     * 1.21's nether / end portal destination for an entity of a 1.20.6 class that overrides
     * findDimensionEntryPoint or getExitPortal (CbEntityPortalBlocksMixin): 1.20.6 took the destination level the
     * same way (the overworld from the nether or the end, else the nether or the end) and asked the entity
     * where to arrive; no entry point, no move.
     */
    public static class_5454 legacyPortalDestination(class_3218 level, class_1297 entity, boolean nether) {
        net.minecraft.class_5321<class_1937> key;
        if (nether) {
            key = level.method_27983() == class_1937.field_25180 ? class_1937.field_25179 : class_1937.field_25180;
        } else {
            key = level.method_27983() == class_1937.field_25181 ? class_1937.field_25179 : class_1937.field_25181;
        }
        class_3218 dest = level.method_8503().method_3847(key);
        if (dest == null) {
            return null;
        }
        PortalInfo info = findDimensionEntryPoint(entity, dest);
        if (info == null) {
            return null;
        }
        boolean toEnd = dest.method_27983() == class_1937.field_25181;
        class_5454.class_9823 after;
        if (entity instanceof class_3222) {
            if (toEnd) {
                createEndPlatform(dest, class_2338.method_49638(info.field_25879));
            }
            after = class_5454.field_52246;
        } else {
            after = toEnd ? moved -> makeObsidianPlatform(dest) : class_5454.field_52245;
        }
        return new class_5454(dest, info.field_25879, info.field_25880, info.field_25881, info.field_25882, after);
    }

    public static boolean asksLegacyDestination(class_1297 entity, boolean nether) {
        return entity instanceof LegacyEntryPoint || nether && entity instanceof LegacyExitPortal;
    }

    // ------------------------------------------------------------------------------------------------ marking and counting
    /** 1.20.6 portalEntrancePos: the last nether portal block the entity was marked in (kept by the Entity mixin) */
    public static class_2338 portalEntrancePos(class_1297 entity) {
        return ((EntityPortalState) entity).cb$portalEntrancePos();
    }

    public static void setPortalEntrancePos(class_1297 entity, class_2338 pos) {
        ((EntityPortalState) entity).cb$setPortalEntrancePos(pos);
        class_9787 process = entity.field_51994;
        if (process != null && process.method_60703(netherPortal()) && pos != null) {
            process.method_60704(pos);
        }
    }

    /** 1.20.6 isInsidePortal: inside a nether portal this tick (1.21's processor for the nether portal says) */
    public static boolean isInsidePortal(class_1297 entity) {
        class_9787 process = entity.field_51994;
        return process != null && process.method_60703(netherPortal()) && process.method_60709();
    }

    /** a 1.20.6 write of isInsidePortal: the nether portal processor is marked for this tick (made at the entrance if missing) */
    public static void setInsidePortal(class_1297 entity, boolean inside) {
        class_9787 process = entity.field_51994;
        if (inside) {
            if (process == null || !process.method_60703(netherPortal())) {
                class_2338 at = portalEntrancePos(entity);
                process = new class_9787(netherPortal(), at != null ? at : entity.method_24515());
                entity.field_51994 = process;
            }
            process.method_60705(true);
        } else if (process != null && process.method_60703(netherPortal())) {
            process.method_60705(false);
        }
    }

    /** 1.20.6 portalTime: the nether portal processor's count (0 without one) */
    public static int portalTime(class_1297 entity) {
        class_9787 process = entity.field_51994;
        return process != null && process.method_60703(netherPortal()) ? process.method_60708() : 0;
    }

    /** a 1.20.6 write of portalTime: the nether portal processor's count (one is made at the entrance for a count above 0) */
    public static void setPortalTime(class_1297 entity, int time) {
        class_9787 process = entity.field_51994;
        if (process == null || !process.method_60703(netherPortal())) {
            if (time <= 0) {
                return;
            }
            class_2338 at = portalEntrancePos(entity);
            process = new class_9787(netherPortal(), at != null ? at : entity.method_24515());
            entity.field_51994 = process;
        }
        ((PortalProcessorTime) process).cb$setPortalTime(time);
    }

    /** what the layer's PortalProcessor accessor exposes */
    public interface PortalProcessorTime {
        void cb$setPortalTime(int time);
    }

    /**
     * 1.20.6 handleInsidePortal(pos) as a call: the entity's own override; else 1.21's setAsInsidePortal with the
     * nether portal -- 1.20.6's body (on cooldown the cooldown restarts, else the entrance is kept and the entity
     * marked inside), and the only portal 1.20.6 marked this way.
     */
    public static void handleInsidePortal(class_1297 entity, class_2338 pos) {
        if (entity instanceof LegacyInsidePortal legacy) {
            legacy.method_5717(pos);
            return;
        }
        entity.method_60697(netherPortal(), pos);
    }

    /** a 1.20.6 subclass's super.handleInsidePortal(pos): 1.21's nether portal marking, past the subclass's hook */
    public static void superHandleInsidePortal(class_1297 entity, class_2338 pos) {
        boolean entered = Guard.cbEnter(entity);
        try {
            entity.method_60697(netherPortal(), pos);
        } finally {
            if (entered) {
                Guard.cbExit(entity);
            }
        }
    }

    /**
     * 1.21's setAsInsidePortal(portal, pos) on a 1.20.6 subclass that overrides handleInsidePortal(pos): a nether
     * portal is the override's (1.20.6 marked nothing else this way); any other portal goes on to 1.21's own.
     */
    public static void setAsInsidePortal(LegacyInsidePortal self, class_9797 portal, class_2338 pos) {
        if (isNetherPortal(portal)) {
            self.method_5717(pos);
        } else {
            ((class_1297) self).method_60697(portal, pos);
        }
    }

    /**
     * 1.20.6 handleNetherPortal() as a call: the entity's own override; else 1.21's handlePortal, which does what
     * 1.20.6's did -- count the portal time up while inside and teleport when it reaches the wait time, let it
     * decay outside, run the portal cooldown -- for the portal the entity is in.
     */
    public static void handleNetherPortal(class_1297 entity) {
        if (entity instanceof LegacyNetherPortalTick legacy) {
            legacy.method_18379();
            return;
        }
        ((EntityPortalState) entity).cb$handlePortal();
    }

    /** a 1.20.6 subclass's super.handleNetherPortal(): 1.21's handlePortal, past the subclass's hook */
    public static void superHandleNetherPortal(class_1297 entity) {
        boolean entered = Guard.cbEnter(entity);
        try {
            ((EntityPortalState) entity).cb$handlePortal();
        } finally {
            if (entered) {
                Guard.cbExit(entity);
            }
        }
    }

    /**
     * 1.21's handlePortal() tick on a 1.20.6 subclass that overrides handleNetherPortal(): the override runs, as
     * 1.20.6's baseTick ran it, unless the entity is in another kind of portal -- 1.20.6 moved end portal and
     * gateway travellers without this tick, so those go on to 1.21's own.
     */
    public static void handlePortal(LegacyNetherPortalTick self) {
        class_1297 entity = (class_1297) self;
        class_9787 process = entity.field_51994;
        if (process == null || process.method_60703(netherPortal())) {
            self.method_18379();
        } else {
            ((EntityPortalState) entity).cb$handlePortal();
        }
    }

    /**
     * 1.20.6 getPortalWaitTime() as a call: the entity's own override; else 1.20.6's -- a player waits the
     * creative or default nether portal delay (at least one tick) by its invulnerability, anything else 0.
     */
    public static int getPortalWaitTime(class_1297 entity) {
        if (entity instanceof LegacyPortalWaitTime legacy) {
            return legacy.method_5741();
        }
        return vanillaPortalWaitTime(entity);
    }

    public static int superGetPortalWaitTime(class_1297 entity) {
        return 0;
    }

    public static int superGetPortalWaitTime(class_1657 player) {
        return vanillaPortalWaitTime(player);
    }

    private static int vanillaPortalWaitTime(class_1297 entity) {
        if (entity instanceof class_1657 player) {
            return Math.max(1, player.method_37908().method_8450().method_8356(player.method_31549().field_7480
                ? net.minecraft.class_1928.field_46795 : net.minecraft.class_1928.field_46794));
        }
        return 0;
    }

    // ------------------------------------------------------------------------------------------------ teleports
    /**
     * 1.20.6 teleportToWithTicket(x, y, z): on a server level, a post-teleport ticket for the target chunk (radius
     * 0, the entity's id), the chunk loaded, then teleportTo.
     */
    public static void teleportToWithTicket(class_1297 entity, double x, double y, double z) {
        if (entity.method_37908() instanceof class_3218 level) {
            class_1923 chunk = new class_1923(class_2338.method_49637(x, y, z));
            level.method_14178().method_17297(class_3230.field_19347, chunk, 0, entity.method_5628());
            level.method_8497(chunk.field_9181, chunk.field_9180);
            entity.method_5859(x, y, z);
        }
    }

    /** what the layer's TheEndGatewayBlockEntity accessor exposes */
    public interface GatewayCooldown {
        void cb$setTeleportCooldown(int ticks);
    }

    /**
     * 1.20.6 TheEndGatewayBlockEntity.teleportEntity(level, pos, state, entity, gateway): nothing off a server
     * level or while the gateway cools down; else a 100 tick cooldown, the exit 1.21's getPortalPosition finds
     * (1.20.6's search, making the exit gateway when there is none, the same block's bottom centre), and the move
     * -- an ender pearl takes its owner there (an advancement trigger for a player, the pearl gone), anything
     * else its root vehicle -- with a portal cooldown and a ticket, then the gateway's cooldown event.
     */
    public static void teleportEntity(class_1937 level, class_2338 pos, class_2680 state, class_1297 entity,
                                      net.minecraft.class_2643 gateway) {
        if (!(level instanceof class_3218 server) || gateway.method_11421()) {
            return;
        }
        ((GatewayCooldown) gateway).cb$setTeleportCooldown(100);
        class_243 exit = gateway.method_60787(server, pos);
        if (exit != null) {
            class_1297 target;
            if (entity instanceof net.minecraft.class_1684 pearl) {
                class_1297 owner = pearl.method_24921();
                if (owner instanceof class_3222 player) {
                    net.minecraft.class_174.field_1180.method_8885(player, state);
                }
                if (owner != null) {
                    target = owner;
                    entity.method_31472();
                } else {
                    target = entity;
                }
            } else {
                target = entity.method_5668();
            }
            target.method_30229();
            teleportToWithTicket(target, exit.field_1352, exit.field_1351, exit.field_1350);
        }
        net.minecraft.class_2643.method_11411(level, pos, state, gateway);
    }

    /** 1.20.6 TheEndGatewayBlockEntity.canEntityTeleport(entity): no spectator, root vehicle not on portal cooldown */
    public static boolean canEntityTeleport(class_1297 entity) {
        return net.minecraft.class_1301.field_6155.test(entity) && !entity.method_5668().method_30230();
    }

    /**
     * 1.20.6 Player.findRespawnPositionAndUseSpawnBlock(level, pos, angle, forced, keepCharge): a charged (or forced)
     * respawn anchor where anchors work gives a stand-up spot and, unless forced or kept, one charge; a bed where
     * beds work, its stand-up spot; a forced spawn on two blocks one can respawn in, their centre 0.1 up.
     */
    public static Optional<class_243> findRespawnPositionAndUseSpawnBlock(class_3218 level, class_2338 pos, float angle,
                                                                          boolean forced, boolean keepCharge) {
        class_2680 state = level.method_8320(pos);
        net.minecraft.class_2248 block = state.method_26204();
        if (block instanceof net.minecraft.class_4969 && (forced || state.method_11654(net.minecraft.class_4969.field_23153) > 0)
                && net.minecraft.class_4969.method_27353(level)) {
            Optional<class_243> stand = net.minecraft.class_4969.method_26156(net.minecraft.class_1299.field_6097, level, pos);
            if (!forced && !keepCharge && stand.isPresent()) {
                level.method_8652(pos, state.method_11657(net.minecraft.class_4969.field_23153,
                    state.method_11654(net.minecraft.class_4969.field_23153) - 1), 3);
            }
            return stand;
        }
        if (block instanceof net.minecraft.class_2244 && net.minecraft.class_2244.method_27352(level)) {
            return net.minecraft.class_2244.method_9484(net.minecraft.class_1299.field_6097, level, pos,
                state.method_11654(net.minecraft.class_2383.field_11177), angle);
        }
        if (!forced) {
            return Optional.empty();
        }
        boolean here = block.method_9538(state);
        class_2680 above = level.method_8320(pos.method_10084());
        boolean up = above.method_26204().method_9538(above);
        return here && up
            ? Optional.of(new class_243(pos.method_10263() + 0.5, pos.method_10264() + 0.1, pos.method_10260() + 0.5))
            : Optional.empty();
    }

    // ------------------------------------------------------------------------------------------------ arriving in a level
    /** a 1.20.6 ServerLevel subclass's own addDuringCommandTeleport(player) */
    public interface LegacyCommandArrival {
        void method_18207(class_3222 player);
    }

    /** a 1.20.6 ServerLevel subclass's own addDuringPortalTeleport(player) */
    public interface LegacyPortalArrival {
        void method_18211(class_3222 player);
    }

    /** marks every 1.20.6 ServerLevel subclass, whose addDuringTeleport(entity) the bridge below answers */
    public interface LegacyLevel {
    }

    /** the player a command teleport is moving right now (CbEntityPortalCommandMixin on ServerPlayer.teleportTo) */
    private static final ThreadLocal<class_3222> COMMAND_TELEPORT = new ThreadLocal<>();

    public static class_3222 beginCommandTeleport(class_3222 player) {
        class_3222 outer = COMMAND_TELEPORT.get();
        COMMAND_TELEPORT.set(player);
        return outer;
    }

    public static void endCommandTeleport(class_3222 outer) {
        COMMAND_TELEPORT.set(outer);
    }

    /**
     * 1.20.6 addDuringCommandTeleport(player) / addDuringPortalTeleport(player) as calls: both added the player to
     * the level (addPlayer), which 1.21's addDuringTeleport does for a player; a 1.20.6 subclass's own override
     * runs instead.
     */
    public static void addDuringCommandTeleport(class_3218 level, class_3222 player) {
        if (level instanceof LegacyCommandArrival legacy) {
            legacy.method_18207(player);
        } else {
            level.method_18769(player);
        }
    }

    public static void addDuringPortalTeleport(class_3218 level, class_3222 player) {
        if (level instanceof LegacyPortalArrival legacy) {
            legacy.method_18211(player);
        } else {
            level.method_18769(player);
        }
    }

    /** a 1.20.6 subclass's super.addDuringCommandTeleport / addDuringPortalTeleport: 1.21's addDuringTeleport, past its hook */
    public static void superAddDuring(class_3218 level, class_3222 player) {
        boolean entered = Guard.cbEnter(level);
        try {
            level.method_18769(player);
        } finally {
            if (entered) {
                Guard.cbExit(level);
            }
        }
    }

    /**
     * 1.21's addDuringTeleport(entity) on a 1.20.6 ServerLevel subclass: a player arriving by a command teleport
     * goes to the subclass's addDuringCommandTeleport, any other arriving player (1.21 moves both through
     * changeDimension; 1.20.6 called the portal one from there) to its addDuringPortalTeleport -- each when the
     * subclass overrides it; anything else, and an override it lacks, goes on to 1.21's own (the guard).
     */
    public static void addDuringTeleport(LegacyLevel self, class_1297 entity) {
        if (entity instanceof class_3222 player) {
            if (COMMAND_TELEPORT.get() == player) {
                if (self instanceof LegacyCommandArrival legacy) {
                    legacy.method_18207(player);
                    return;
                }
            } else if (self instanceof LegacyPortalArrival legacy) {
                legacy.method_18211(player);
                return;
            }
        }
        ((class_3218) self).method_18769(entity);
    }

    // ------------------------------------------------------------------------------------------------ PortalForcer.findPortalAround
    /**
     * 1.20.6 PortalForcer.findPortalAround(pos, toNether, border) as a call. A plain forcer: 1.20.6's body (the
     * 1.21 search is the same; the ticket and the rectangle are 1.20.6's). A 1.20.6 subclass's override is reached
     * through its 1.21-shaped bridge (convertHooks), whose portal positions are the rectangles' corners again.
     */
    public static Optional<class_5459.class_5460> findPortalAround(class_1946 forcer, class_2338 pos, boolean toNether, class_2784 border) {
        class_3218 level = ((PortalForcerLevel) forcer).cb$level();
        if (forcer.getClass() == class_1946.class) {
            return findPortalAround(level, pos, toNether, border);
        }
        return forcer.method_30483(pos, toNether, border).map(at -> {
            class_2680 state = level.method_8320(at);
            return state.method_28498(class_2741.field_12529)
                ? class_5459.method_30574(at, state.method_11654(class_2741.field_12529), 21, class_2350.class_2351.field_11052, 21,
                    q -> level.method_8320(q) == state)
                : new class_5459.class_5460(at, 1, 1);
        });
    }

    /** a 1.20.6 subclass's super.findPortalAround: 1.20.6's body for its level */
    public static Optional<class_5459.class_5460> superFindPortalAround(class_1946 forcer, class_2338 pos, boolean toNether, class_2784 border) {
        return findPortalAround(((PortalForcerLevel) forcer).cb$level(), pos, toNether, border);
    }

    /**
     * What a 1.20.6 findPortalAround override answers, as 1.21's findClosestPortalPosition: the corner of each
     * rectangle (a portal block -- 1.21 takes the rectangle around it again). Adaptive: a position passes.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional portalPositions(Optional found) {
        return found.map(x -> x instanceof class_5459.class_5460 rect ? rect.field_25936 : x);
    }

    private CbEntityPortals() {
    }
}
