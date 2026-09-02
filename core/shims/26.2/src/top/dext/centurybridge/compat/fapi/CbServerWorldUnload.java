package top.dext.centurybridge.compat.fapi;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/** was ServerWorldEvents.Unload (SAM onWorldUnload) */
@FunctionalInterface
public interface CbServerWorldUnload {
    void onWorldUnload(MinecraftServer server, ServerLevel level);
}
