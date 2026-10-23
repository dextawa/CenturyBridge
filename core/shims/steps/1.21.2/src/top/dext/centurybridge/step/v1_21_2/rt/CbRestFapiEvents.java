package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.class_2818;
import net.minecraft.class_3194;
import net.minecraft.class_3218;
import net.minecraft.class_3222;

/**
 * Fabric API events 1.21.1's Fabric API (0.116) had and 1.21.2's last Fabric API (0.106.1) does not:
 * ServerPlayerEvents.JOIN / LEAVE and ServerChunkEvents.CHUNK_GENERATE / CHUNK_LEVEL_TYPE_CHANGE, with their
 * callback interfaces (classRenames / fieldRedirects). They fire where 0.116's own mixins fired them, whose
 * 1.21.1 targets 1.21.2 keeps unchanged (CbRestFapiPlayerListMixin, CbRestFapiChunkStatusMixin,
 * CbRestFapiChunkHolderMixin): JOIN when PlayerList.placeNewPlayer returns, LEAVE at the start of
 * PlayerList.remove; CHUNK_GENERATE when a chunk becomes full from a freshly generated proto chunk (not one
 * read from disk); CHUNK_LEVEL_TYPE_CHANGE at each step of a full chunk's level type, as 0.116 tracked it.
 */
public final class CbRestFapiEvents {

    private CbRestFapiEvents() {
    }

    /** ServerPlayerEvents.Join of 1.21.1's Fabric API */
    @FunctionalInterface
    public interface Join {
        void onJoin(class_3222 player);
    }

    /** ServerPlayerEvents.Leave of 1.21.1's Fabric API */
    @FunctionalInterface
    public interface Leave {
        void onLeave(class_3222 player);
    }

    /** ServerChunkEvents.Generate of 1.21.1's Fabric API */
    @FunctionalInterface
    public interface Generate {
        void onChunkGenerate(class_3218 level, class_2818 chunk);
    }

    /** ServerChunkEvents.LevelTypeChange of 1.21.1's Fabric API */
    @FunctionalInterface
    public interface LevelTypeChange {
        void onChunkLevelTypeChange(class_3218 level, class_2818 chunk, class_3194 oldLevelType, class_3194 newLevelType);
    }

    /** ServerPlayerEvents.JOIN */
    public static final Event<Join> JOIN = EventFactory.createArrayBacked(Join.class, callbacks -> player -> {
        for (Join callback : callbacks) {
            callback.onJoin(player);
        }
    });

    /** ServerPlayerEvents.LEAVE */
    public static final Event<Leave> LEAVE = EventFactory.createArrayBacked(Leave.class, callbacks -> player -> {
        for (Leave callback : callbacks) {
            callback.onLeave(player);
        }
    });

    /** ServerChunkEvents.CHUNK_GENERATE */
    public static final Event<Generate> CHUNK_GENERATE = EventFactory.createArrayBacked(Generate.class, callbacks -> (level, chunk) -> {
        for (Generate callback : callbacks) {
            callback.onChunkGenerate(level, chunk);
        }
    });

    /** ServerChunkEvents.CHUNK_LEVEL_TYPE_CHANGE */
    public static final Event<LevelTypeChange> CHUNK_LEVEL_TYPE_CHANGE = EventFactory.createArrayBacked(LevelTypeChange.class,
        callbacks -> (level, chunk, oldType, newType) -> {
            for (LevelTypeChange callback : callbacks) {
                callback.onChunkLevelTypeChange(level, chunk, oldType, newType);
            }
        });

    // ------------------------------------------------------------------ UseItemCallback

    /**
     * UseItemCallback.interact of 1.21.1's Fabric API: answered an InteractionResultHolder of the stack (0.106.1:
     * an InteractionResult). Converted as Item.use is (CbItemResults): the holder's stack is what a Success says
     * the hand turns into, and PASS / FAIL set the hand as 1.21.1's game modes did.
     */
    @FunctionalInterface
    public interface LegacyUseItem {
        top.dext.centurybridge.step.v1_21_2.facade.InteractionResultHolder<net.minecraft.class_1799> interact(
            net.minecraft.class_1657 player, net.minecraft.class_1937 level, net.minecraft.class_1268 hand);
    }

    /** a 1.21.1 UseItemCallback lambda as 0.106.1's callback (lambdaAdapters) */
    public static net.fabricmc.fabric.api.event.player.UseItemCallback useItem(LegacyUseItem legacy) {
        return (player, level, hand) -> useItem(legacy, player, level, hand);
    }

    /** 0.106.1's interact of a 1.21.1 UseItemCallback class (override hook bridge) */
    public static net.minecraft.class_1269 useItem(LegacyUseItem legacy, net.minecraft.class_1657 player, net.minecraft.class_1937 level,
                                                  net.minecraft.class_1268 hand) {
        return CbItemResults.result(legacy.interact(player, level, hand), player, hand);
    }

    /** callback.interact(player, level, hand) called by a 1.21.1 mod: 0.106.1's answer as a holder (interfaceCallRedirects) */
    public static top.dext.centurybridge.step.v1_21_2.facade.InteractionResultHolder<net.minecraft.class_1799> interact(
            net.fabricmc.fabric.api.event.player.UseItemCallback callback, net.minecraft.class_1657 player, net.minecraft.class_1937 level,
            net.minecraft.class_1268 hand) {
        return CbItemResults.holder(callback.interact(player, level, hand), player, hand);
    }

    // ------------------------------------------------------------------ ServerLivingEntityEvents.MobConversion

    /**
     * ServerLivingEntityEvents.MobConversion of 1.21.1's Fabric API: (previous, converted, keepEquipment);
     * 0.106.1 hands the conversion's ConversionParams, whose keepEquipment is 1.21.1's flag.
     */
    @FunctionalInterface
    public interface LegacyMobConversion {
        void onConversion(net.minecraft.class_1308 previous, net.minecraft.class_1308 converted, boolean keepEquipment);
    }

    /** a 1.21.1 MobConversion lambda as 0.106.1's callback (lambdaAdapters) */
    public static net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.MobConversion mobConversion(LegacyMobConversion legacy) {
        return (previous, converted, params) -> legacy.onConversion(previous, converted, params.comp_3145());
    }

    /** 0.106.1's onConversion of a 1.21.1 MobConversion class (override hook bridge) */
    public static void mobConversion(LegacyMobConversion legacy, net.minecraft.class_1308 previous, net.minecraft.class_1308 converted,
                                     net.minecraft.class_10179 params) {
        legacy.onConversion(previous, converted, params.comp_3145());
    }

    /**
     * callback.onConversion(previous, converted, keepEquipment) called by a 1.21.1 mod: 0.106.1's callback with
     * the parameters of 1.21.1's Mob.convertTo -- a single conversion keeping the equipment as asked, the
     * converted mob taking the previous one's loot pickup and team (1.21.1's convertTo copied both)
     */
    public static void onConversion(net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.MobConversion callback,
                                    net.minecraft.class_1308 previous, net.minecraft.class_1308 converted, boolean keepEquipment) {
        callback.onConversion(previous, converted, net.minecraft.class_10179.method_63607(previous, keepEquipment, true));
    }

    /** the level type 0.116 last reported for a chunk holder (CbRestFapiChunkHolderMixin) */
    public interface LevelTypeTracker {
        class_3194 cb$getCurrentEventLevelType();

        void cb$setCurrentEventLevelType(class_3194 type);
    }

    /**
     * Every step from the tracked level type up to the target's, reported in order (0.116's
     * ChunkGeneratingMixin, when a chunk becomes full)
     */
    public static void raiseLevelType(LevelTypeTracker tracker, class_3218 level, class_2818 chunk, class_3194 target) {
        class_3194[] types = class_3194.values();
        for (int i = tracker.cb$getCurrentEventLevelType().ordinal(); i < target.ordinal(); i++) {
            class_3194 oldType = types[i];
            class_3194 newType = types[i + 1];
            CHUNK_LEVEL_TYPE_CHANGE.invoker().onChunkLevelTypeChange(level, chunk, oldType, newType);
            tracker.cb$setCurrentEventLevelType(newType);
        }
    }
}
