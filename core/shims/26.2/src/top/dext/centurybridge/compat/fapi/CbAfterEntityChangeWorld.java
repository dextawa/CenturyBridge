package top.dext.centurybridge.compat.fapi;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/** was ServerEntityWorldChangeEvents.AfterEntityChange (SAM afterChangeWorld) */
@FunctionalInterface
public interface CbAfterEntityChangeWorld extends ServerEntityLevelChangeEvents.AfterEntityChange {

    void afterChangeWorld(Entity originalEntity, Entity newEntity, ServerLevel origin, ServerLevel destination);

    @Override
    default void afterChangeLevel(Entity originalEntity, Entity newEntity, ServerLevel origin, ServerLevel destination) {
        afterChangeWorld(originalEntity, newEntity, origin, destination);
    }
}
