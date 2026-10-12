package top.dext.centurybridge.step.v1_20_6.rt;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;

/**
 * 1.20.6's Fabric API gave its server-side networking contexts an abstract server(): "the MinecraftServer
 * instance" the packet arrived on (javadoc). Fabric's own contexts are records holding the server their
 * connection's handler belongs to. A context written against 1.20.5 has no such method (Fabric API made it
 * abstract: no default); the one synthesized for it answers that same server, from what the 1.20.5
 * context already gives:
 * <ul>
 * <li>a configuration context's network handler: Fabric API's own ServerConfigurationNetworking.getServer,
 *     the handler's server;</li>
 * <li>a play context's player: the server the player is on (ServerPlayerEntity.server), the server of the
 *     play handler Fabric builds its context with.</li>
 * </ul>
 */
public final class CbServerNetworkingContexts {

    private CbServerNetworkingContexts() {
    }

    public static MinecraftServer configurationServer(ServerConfigurationNetworking.Context context) {
        return ServerConfigurationNetworking.getServer(context.networkHandler());
    }

    public static MinecraftServer playServer(ServerPlayNetworking.Context context) {
        return context.player().field_13995;
    }
}
