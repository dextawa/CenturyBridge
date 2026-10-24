package top.dext.centurybridge.step.v26_2.rt;

import net.minecraft.server.MinecraftServer;

/**
 * Values 26.1.2 implied where 26.2 asks for them (frameDefaults: a 26.1.2 override calling its own old method has no
 * 26.2 caller to take them from).
 */
public final class CbS26Server {
    private CbS26Server() {}

    /**
     * MinecraftServer.publishServer's scope: 26.1.2's publishServer(gameType, cheats, port) opened the game to LAN, the
     * only scope it had; 26.2's PublishCommand passes MultiplayerScope.LAN where 26.1.2's called the old method
     */
    public static MinecraftServer.MultiplayerScope lanScope(MinecraftServer self) {
        return MinecraftServer.MultiplayerScope.LAN;
    }
}
