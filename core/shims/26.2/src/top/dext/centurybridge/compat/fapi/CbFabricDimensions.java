package top.dext.centurybridge.compat.fapi;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;

/** fabric-dimensions-v1's FabricDimensions.teleport (1 corpus mod): vanilla Entity.teleport(TeleportTransition) does it since 1.21.2 (the target carries the level). */
public final class CbFabricDimensions {

    @SuppressWarnings("unchecked")
    public static <E extends Entity> E teleport(E teleported, ServerLevel destination, TeleportTransition target) {
        return (E) teleported.teleport(target);
    }

    private CbFabricDimensions() {
    }
}
