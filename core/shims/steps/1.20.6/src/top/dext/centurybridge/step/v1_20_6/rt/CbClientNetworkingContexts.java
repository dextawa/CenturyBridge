package top.dext.centurybridge.step.v1_20_6.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

/**
 * 1.20.6's Fabric API gave ClientConfigurationNetworking.Context an abstract client(): "the MinecraftClient
 * instance" (javadoc). Fabric's own context is a record holding the client its configuration addon was
 * made with -- the game's one MinecraftClient. A 1.20.5 context has no such method (Fabric API made it
 * abstract: no default), and 1.20.5's interface, with responseSender() alone, was a functional interface,
 * so 1.20.5 mods also built contexts as lambdas. Both get that same client:
 * <ul>
 * <li>a class implementing the interface gets client() synthesized ({@link #client});</li>
 * <li>a lambda is built as {@link CbLegacyClientConfigurationContext} and adapted ({@link #adapt}) to a
 *     context whose responseSender() is the lambda's and whose client() is the game's client.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbClientNetworkingContexts {

    private CbClientNetworkingContexts() {
    }

    public static net.minecraft.class_310 client(ClientConfigurationNetworking.Context context) {
        return net.minecraft.class_310.method_1551();
    }

    public static ClientConfigurationNetworking.Context adapt(CbLegacyClientConfigurationContext legacy) {
        return new Adapted(legacy);
    }

    /** a 1.20.5 context lambda as 1.20.6's context */
    @Environment(EnvType.CLIENT)
    static final class Adapted implements ClientConfigurationNetworking.Context {
        private final CbLegacyClientConfigurationContext legacy;

        Adapted(CbLegacyClientConfigurationContext legacy) {
            this.legacy = legacy;
        }

        @Override
        public net.minecraft.class_310 client() {
            return net.minecraft.class_310.method_1551();
        }

        @Override
        public PacketSender responseSender() {
            return legacy.responseSender();
        }
    }
}
