package top.dext.centurybridge.step.v1_20_6.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

/**
 * 1.20.5's ClientConfigurationNetworking.Context as a lambda saw it: one abstract method, responseSender().
 * A lambda or method reference a 1.20.5 mod built as a context is built as this interface instead
 * (lambdaAdapters) and handed on through {@link CbClientNetworkingContexts#adapt}.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface CbLegacyClientConfigurationContext {
    PacketSender responseSender();
}
