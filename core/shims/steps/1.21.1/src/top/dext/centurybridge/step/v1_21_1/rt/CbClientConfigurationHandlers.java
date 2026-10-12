package top.dext.centurybridge.step.v1_21_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;

/**
 * 1.21.1's Fabric API (networking 4.3.1) gave ClientConfigurationNetworking.Context an abstract
 * networkHandler(): "the ClientConfigurationNetworkHandler instance" (javadoc; abstract, no default).
 * Fabric's own context is a record holding the handler of the connection being configured -- the one its
 * configuration addon was made for, which Fabric keeps as the client's current configuration addon from the
 * handler's construction until the configuration completes (ClientConfigurationNetworkHandlerMixin,
 * ClientConfigurationNetworkAddon.handleComplete). A context written against 1.21 has client() and
 * responseSender() only; the networkHandler() synthesized for it answers that same current handler, which
 * this layer tracks the same way (mixin CbClientConfigurationHandlerTracking: set when a handler is made,
 * cleared when its configuration finishes). Outside configuration there is none, and it throws as Fabric
 * API's own ClientConfigurationNetworking.getSender() does then.
 */
@Environment(EnvType.CLIENT)
public final class CbClientConfigurationHandlers {

    private static volatile net.minecraft.class_8674 current;

    private CbClientConfigurationHandlers() {
    }

    public static void begin(net.minecraft.class_8674 handler) {
        current = handler;
    }

    public static void end(net.minecraft.class_8674 handler) {
        if (current == handler) {
            current = null;
        }
    }

    public static net.minecraft.class_8674 networkHandler(ClientConfigurationNetworking.Context context) {
        net.minecraft.class_8674 handler = current;
        if (handler == null) {
            throw new IllegalStateException("Cannot get the configuration network handler while not configuring!");
        }
        return handler;
    }
}
