package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_5455;
import net.minecraft.class_634;
import net.minecraft.server.MinecraftServer;

/** The client half of {@link CbNetRegistries}: never loaded on a dedicated server. */
@Environment(EnvType.CLIENT)
public final class CbNetClientRegistries {

    /** the registries the client's connection synchronized, while it plays on one; null otherwise */
    public static class_5455 cbConnection() {
        class_310 mc = class_310.method_1551();
        class_634 connection = mc == null ? null : mc.method_1562();
        return connection == null ? null : connection.method_29091();
    }

    /** the integrated server, while the client hosts one; null otherwise */
    public static MinecraftServer cbIntegratedServer() {
        class_310 mc = class_310.method_1551();
        return mc == null ? null : mc.method_1576();
    }

    private CbNetClientRegistries() {
    }
}
