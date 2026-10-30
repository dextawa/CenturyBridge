package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.function.Supplier;

/**
 * The id of the recipe being decoded on this thread, for a 1.20.1 recipe serializer's read(id, buf) (the 1.20.2 layer's
 * CbRecipes.legacyRead). 1.20.4 read the recipe id just before the serializer's fromNetwork in the recipe-sync packet's
 * fromNetwork (class_2788.method_17817), where the 1.20.2 layer's RecipeSyncIdBridge took it; 1.20.5 decodes a recipe
 * through RecipeHolder.STREAM_CODEC (the id, then Recipe.STREAM_CODEC), a method no mixin can anchor in. This layer
 * publishes the id there (CbRecipeHolderCodecRoute) and where it reads 1.20.4's form itself (CbNetGame.recipeFromNetwork).
 *
 * <p>The channel is Fabric Loader's ObjectShare entry {@value #KEY}, a ThreadLocal&lt;ResourceLocation&gt;: the earlier
 * layer reads it where its own hooks no longer run (layers are separate mods; neither links the other's classes).
 */
public final class CbRecipeSync {
    public static final String KEY = "centurybridge:recipe_id";
    private static volatile ThreadLocal<net.minecraft.class_2960> shared;
    /** RecipeManager.apply's entry key, noted until its Recipe.CODEC.parse runs (mixin/CbRecipeApplyIdRoute) */
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
    /**
     * RecipeHolder.STREAM_CODEC as CbRecipeHolderCodecRoute stores it: decode peeks the id (the codec's first field) on a
     * duplicate of the buffer and runs {@code codec}'s decode with it published; encode is {@code codec}'s
     */
    public static net.minecraft.class_9139<Object, Object> publishingId(net.minecraft.class_9139<Object, Object> codec) {
        return new net.minecraft.class_9139<>() {
            @Override
            public Object decode(Object buf) {
                net.minecraft.class_2960 id;
                try {
                    id = net.minecraft.class_2960.field_48267.decode(((io.netty.buffer.ByteBuf) buf).duplicate());
                } catch (RuntimeException e) {
                    return codec.decode(buf);
                }
                return decoding(id, () -> codec.decode(buf));
            }

            @Override
            public void encode(Object buf, Object value) {
                codec.encode(buf, value);
            }
        };
    }
}
