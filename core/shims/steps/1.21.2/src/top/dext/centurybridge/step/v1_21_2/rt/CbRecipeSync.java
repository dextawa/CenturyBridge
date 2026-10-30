package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Supplier;

/**
 * The id of the recipe being decoded on this thread, for a 1.20.1 recipe serializer's read(id, json) (the 1.20.2 layer's
 * CbRecipes, read through Fabric Loader's ObjectShare entry {@value #KEY}, a ThreadLocal&lt;ResourceLocation&gt; shared by
 * every layer that publishes one). 1.21.1 decoded each datapack recipe in RecipeManager.fromJson(id, json, registries)
 * (class_1863.method_17720), where the 1.20.2 layer's RecipeLoadIdBridge took the id; 1.21.2 decodes recipes in
 * SimpleJsonResourceReloadListener.scanDirectory (class_4309.method_51148), with the id it made from the file path
 * (FileToIdConverter.fileToId), and no longer calls fromJson (javap 1.21.1 / 1.21.2 class_1863). CbRecipeScanIdRoute
 * publishes the id there; {@link #SCANNED} holds it between the two points of one file.
 */
public final class CbRecipeSync {
    public static final String KEY = "centurybridge:recipe_id";
    private static volatile ThreadLocal<net.minecraft.class_2960> shared;
    /** the id scanDirectory made for the file it is parsing on this thread */
    public static final ThreadLocal<net.minecraft.class_2960> SCANNED = new ThreadLocal<>();

    private CbRecipeSync() {
    }

    /** the shared ThreadLocal (this layer's, unless another layer published one first) */
    @SuppressWarnings("unchecked")
    public static ThreadLocal<net.minecraft.class_2960> channel() {
        ThreadLocal<net.minecraft.class_2960> s = shared;
        if (s == null) {
            Object o = net.fabricmc.loader.api.FabricLoader.getInstance().getObjectShare().putIfAbsent(KEY, new ThreadLocal<net.minecraft.class_2960>());
            if (o == null) {
                o = net.fabricmc.loader.api.FabricLoader.getInstance().getObjectShare().get(KEY);
            }
            shared = s = (ThreadLocal<net.minecraft.class_2960>) o;
        }
        return s;
    }

    /** runs a decode with {@code id} as the recipe being decoded on this thread (the outer one restored after) */
    public static <T> T decoding(net.minecraft.class_2960 id, Supplier<T> decode) {
        ThreadLocal<net.minecraft.class_2960> c = channel();
        net.minecraft.class_2960 outer = c.get();
        c.set(id);
        try {
            return decode.get();
        } finally {
            if (outer == null) {
                c.remove();
            } else {
                c.set(outer);
            }
        }
    }
}
