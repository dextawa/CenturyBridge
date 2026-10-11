package top.dext.centurybridge.rt.v26_2;

/**
 * Client-side registry access, kept in its own class so the reference to
 * net.minecraft.client.Minecraft is never resolved on a dedicated server.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbClientRegistry {

    private CbClientRegistry() {
    }

    /** the connected world's synced dynamic registries, or null outside a world */
    public static net.minecraft.core.RegistryAccess current() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc == null) {
            return null;
        }
        net.minecraft.client.multiplayer.ClientLevel level = mc.level;
        return level == null ? null : level.registryAccess();
    }
}
