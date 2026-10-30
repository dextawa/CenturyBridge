package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import net.minecraft.class_1860;
import net.minecraft.class_2540;
import net.minecraft.class_2960;

/**
 * The 1.20.1 RecipeSerializer contract on top of the 1.20.2 one. 1.20.2 moved a
 * recipe's id out of the recipe (into RecipeEntry) and its JSON reading into
 * codec(); a legacy serializer still reads (id, json) and (id, buf). The converter
 * adds {@link CbLegacyRecipeSerializer} to such a serializer and synthesizes codec()
 * and read(buf) to call here. The id the old methods need is {@link #CURRENT_ID}:
 * set while RecipeManager deserializes a datapack recipe and while the recipe-sync
 * packet reads one (RecipeLoadIdBridge / RecipeSyncIdBridge).
 */
public final class CbRecipes {

    /** the id of the recipe being decoded on this thread */
    public static final ThreadLocal<class_2960> CURRENT_ID = new ThreadLocal<>();

    /**
     * The id of the recipe being decoded on this thread: {@link #CURRENT_ID}, else the one a later layer publishes where
     * the later release decodes recipes past this layer's hooks -- Fabric Loader's ObjectShare entry
     * "centurybridge:recipe_id", a ThreadLocal of the id (the 1.20.5 layer's CbRecipeSync: RecipeHolder.STREAM_CODEC,
     * which 1.20.5's recipe-sync packet decodes with, where 1.20.2..1.20.4 read the id in the packet's fromNetwork).
     */
    public static class_2960 currentId() {
        class_2960 id = CURRENT_ID.get();
        if (id != null) {
            return id;
        }
        Object shared = net.fabricmc.loader.api.FabricLoader.getInstance().getObjectShare().get("centurybridge:recipe_id");
        return shared instanceof ThreadLocal<?> t && t.get() instanceof class_2960 later ? later : null;
    }

    private static final Map<CbLegacyRecipeSerializer, Codec<class_1860<?>>> CODECS =
        Collections.synchronizedMap(new WeakHashMap<>());

    /** codec() of a legacy serializer: a map codec, so the recipe dispatch hands it the whole object, "type" included, as 1.20.1 did */
    public static Codec<class_1860<?>> legacyCodec(CbLegacyRecipeSerializer serializer) {
        return CODECS.computeIfAbsent(serializer, s -> new MapCodec<class_1860<?>>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }

            @Override
            public <T> DataResult<class_1860<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
                class_2960 id = currentId();
                if (id == null) {
                    return DataResult.error(() -> "CenturyBridge: a 1.20.1 recipe serializer reads JSON only while "
                        + "RecipeManager loads a recipe -- its read(id, json) needs the recipe id");
                }
                JsonElement json = ops.convertTo(JsonOps.INSTANCE, ops.createMap(input.entries()));
                if (!json.isJsonObject()) {
                    return DataResult.error(() -> "CenturyBridge: recipe " + id + " is not a JSON object");
                }
                try {
                    return DataResult.success(CbRecipeIds.remember(s.method_8121(id, json.getAsJsonObject()), id));
                } catch (RuntimeException e) {
                    return DataResult.error(() -> "CenturyBridge: recipe " + id + ": " + e.getMessage());
                }
            }

            @Override
            public <T> RecordBuilder<T> encode(class_1860<?> recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix.withErrorsFrom(DataResult.error(
                    () -> "CenturyBridge: a 1.20.1 recipe serializer has no JSON writer"));
            }
        }.codec());
    }

    /** read(buf) of a legacy serializer */
    public static class_1860<?> legacyRead(CbLegacyRecipeSerializer serializer, class_2540 buf) {
        class_2960 id = currentId();
        if (id == null) {
            throw new IllegalStateException("CenturyBridge: a 1.20.1 recipe serializer reads the network form only "
                + "inside the recipe-sync packet -- its read(id, buf) needs the recipe id");
        }
        return CbRecipeIds.remember(serializer.method_8122(id, buf), id);
    }

    private CbRecipes() {
    }
}
