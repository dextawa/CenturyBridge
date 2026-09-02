package top.dext.centurybridge.compat.fapi;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/** was ServerWorldEvents.Load (SAM onWorldLoad) */
@FunctionalInterface
public interface CbServerWorldLoad {
    void onWorldLoad(MinecraftServer server, ServerLevel level);
}
