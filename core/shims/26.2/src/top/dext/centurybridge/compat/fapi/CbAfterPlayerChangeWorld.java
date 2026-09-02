package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** was ServerEntityWorldChangeEvents.AfterPlayerChange (SAM afterChangeWorld) */
@FunctionalInterface
public interface CbAfterPlayerChangeWorld extends ServerEntityLevelChangeEvents.AfterPlayerChange {

    void afterChangeWorld(ServerPlayer player, ServerLevel origin, ServerLevel destination);

    @Override
    default void afterChangeLevel(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
        afterChangeWorld(player, origin, destination);
    }
}
