package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.function.Supplier;
import net.minecraft.class_10689;
import net.minecraft.class_10702;
import net.minecraft.class_10726;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_7106;
import net.minecraft.class_7375;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

/**
 * 1.21.4's static cat / frog variant registries and FrogVariant record on 1.21.5, which made both variant kinds
 * data-driven: their registries live in the world's registry access (Registries.CAT_VARIANT / FROG_VARIANT, javap
 * 1.21.5 class_7924, no BuiltInRegistries field), a FrogVariant is (ClientAsset, SpawnPrioritySelectors) and the
 * vanilla keys moved to FrogVariants (class_10689).
 *
 * BuiltInRegistries.CAT_VARIANT / FROG_VARIANT answer the running world's registry: the server's while one runs (the
 * integrated server in single player), else the client connection's. 1.21.4's static registries existed from
 * bootstrap on; before any world is loaded there is none to answer (an IllegalStateException naming the registry,
 * recorded as a loss).
 */
public final class CbVariants {
    private CbVariants() {
    }

    private static volatile MinecraftServer server;
    /** set by the client side (rt/CbVariantsClient) to the client connection's registries */
    static volatile Supplier<class_5455> client = () -> null;

    public static void serverStarting(MinecraftServer s) {
        server = s;
    }

    public static void serverStopped(MinecraftServer s) {
        if (server == s) {
            server = null;
        }
    }

    private static <T> class_2378<T> world(class_5321<? extends class_2378<T>> key) {
        MinecraftServer s = server;
        class_5455 access = s != null ? s.method_30611() : client.get();
        if (access == null) {
            throw new IllegalStateException("1.21.5 keeps " + key.method_29177() + " in the world's registries and no world is loaded");
        }
        return access.method_30530(key);
    }

    /** BuiltInRegistries.CAT_VARIANT */
    public static class_2378<class_7375> field_41163() {
        return world(class_7924.field_41259);
    }

    /** BuiltInRegistries.FROG_VARIANT */
    public static class_2378<class_7106> field_41164() {
        return world(class_7924.field_41272);
    }

    /** FrogVariant.TEMPERATE / WARM / COLD: 1.21.5's FrogVariants keys (same ids, javap 1.21.5 class_10689) */
    public static class_5321<class_7106> field_37462() {
        return class_10689.field_56278;
    }

    public static class_5321<class_7106> field_37463() {
        return class_10689.field_56279;
    }

    public static class_5321<class_7106> field_37464() {
        return class_10689.field_56280;
    }

    /**
     * new FrogVariant(texture): 1.21.5's FrogVariant drawing that texture -- a ClientAsset whose texture path is the
     * 1.21.4 texture as given and whose id is that path without "textures/" and ".png" (1.21.5's own frog assets,
     * javap class_10726: texturePath = "textures/" + id + ".png") -- with no spawn conditions of its own (1.21.4's
     * FrogVariant had none; the frog chose by biome)
     */
    public static class_7106 newFrogVariant(class_2960 texture) {
        String p = texture.method_12832();
        String id = p.startsWith("textures/") && p.endsWith(".png") ? p.substring(9, p.length() - 4) : p;
        return new class_7106(new class_10726(class_2960.method_60655(texture.method_12836(), id), texture), class_10702.field_56285);
    }

    /** FrogVariant.texture() */
    public static class_2960 comp_707(class_7106 variant) {
        return variant.comp_3569().comp_3627();
    }
}
