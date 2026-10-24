package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.util.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Fabric API event listeners whose callback changed shape at 26.1, given 1.21.11's shape back. A 1.21.11
 * listener -- a lambda (lambdaAdapters, the adapt statics) or a class (an override hook forwarding the new
 * shape to the old one) -- receives what 26.1 passes; a 1.21.11 call of the old shape (an invoker, callReshapes)
 * takes 26.1's.
 *
 * EntitySleepEvents.AllowBed / AllowSleepTime / AllowNearbyMonsters answered an InteractionResult in 1.21.11
 * and answer an EventResult in 26.1. 1.21.11's Fabric API read the answer as: PASS falls through to the next
 * listener and to vanilla; anything else decides, allowing exactly when it consumesAction()
 * (EntitySleepEvents' invokers and LivingEntityMixin / ServerPlayerMixin). 26.1 reads EventResult the same
 * way (PASS falls through, otherwise allowAction()), so the answer converts PASS -> PASS, consuming ->
 * ALLOW, any other -> DENY, and back PASS -> PASS, ALLOW -> SUCCESS, DENY -> FAIL.
 *
 * ServerChunkEvents.Load: 26.1 adds whether the chunk was just generated (its mixin passes true unless the
 * chunk came from disk); 1.21.11's listener was told of every load alike and does not read it.
 */
public final class CbFapiEvents {
    private CbFapiEvents() {
    }

    public static EventResult eventResult(InteractionResult result) {
        if (result == null || result == InteractionResult.PASS) {
            return EventResult.PASS;
        }
        return result.consumesAction() ? EventResult.ALLOW : EventResult.DENY;
    }

    public static InteractionResult interactionResult(EventResult result) {
        if (result == null || result == EventResult.PASS) {
            return InteractionResult.PASS;
        }
        return result == EventResult.ALLOW ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    /**
     * the added argument of a 1.21.11 mod's own call of ServerChunkEvents.CHUNK_LOAD's invoker: 1.21.11's
     * event had no notion of a fresh chunk and such a call announces a chunk already there; 26.1 fires its
     * separate CHUNK_GENERATE for generation, which this call never meant
     */
    public static boolean notGenerated() {
        return false;
    }

    @FunctionalInterface
    public interface LegacyAllowBed {
        InteractionResult allowBed(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult);
    }

    @FunctionalInterface
    public interface LegacyAllowSleepTime {
        InteractionResult allowSleepTime(Player player, BlockPos sleepingPos, boolean vanillaResult);
    }

    @FunctionalInterface
    public interface LegacyAllowNearbyMonsters {
        InteractionResult allowNearbyMonsters(Player player, BlockPos sleepingPos, boolean vanillaResult);
    }

    @FunctionalInterface
    public interface LegacyChunkLoad {
        void onChunkLoad(ServerLevel level, LevelChunk chunk);
    }

    public static EntitySleepEvents.AllowBed adapt(LegacyAllowBed legacy) {
        return (entity, pos, state, vanilla) -> eventResult(legacy.allowBed(entity, pos, state, vanilla));
    }

    public static EntitySleepEvents.AllowSleepTime adapt(LegacyAllowSleepTime legacy) {
        return (player, pos, vanilla) -> eventResult(legacy.allowSleepTime(player, pos, vanilla));
    }

    public static EntitySleepEvents.AllowNearbyMonsters adapt(LegacyAllowNearbyMonsters legacy) {
        return (player, pos, vanilla) -> eventResult(legacy.allowNearbyMonsters(player, pos, vanilla));
    }

    public static ServerChunkEvents.Load adapt(LegacyChunkLoad legacy) {
        return (level, chunk, generated) -> legacy.onChunkLoad(level, chunk);
    }
}
