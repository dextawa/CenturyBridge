package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Converters for the client members whose erased shape stayed while their type arguments changed (generic drift), 1.21.3 to
 * 1.21.4. Each is adaptive (a value already of the wanted kind passes through) and the two directions undo each other.
 * <ul>
 * <li>SkinManager.getOrLoad: CompletableFuture&lt;PlayerSkin&gt; became CompletableFuture&lt;Optional&lt;PlayerSkin&gt;&gt;; 1.21.4 answers
 *     empty where 1.21.3's future failed (the loader's error, which 1.21.4 logs), so empty is a failed future again.</li>
 * <li>Metadata section sets and collections (SpriteLoader, SpriteResourceLoader, TextureAtlasHolder): 1.21.3's serializers and
 *     1.21.4's section types, through s9rest's CbRestMetadata.</li>
 * <li>Model output consumers (ModelTemplate, TexturedModel): BiConsumer&lt;id, Supplier&lt;JsonElement&gt;&gt; became
 *     BiConsumer&lt;id, ModelInstance&gt;; a ModelInstance is a Supplier, and a plain Supplier is made one.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbGenericClient {
    private CbGenericClient() {
    }

    // ---- SkinManager.getOrLoad ----

    /** 1.21.4's future of an Optional skin as 1.21.3's future of the skin (empty: failed, as 1.21.3's) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CompletableFuture skinFuture(CompletableFuture future) {
        if (future == null) {
            return null;
        }
        return future.thenCompose(v -> {
            if (v instanceof Optional<?> o) {
                return o.isPresent() ? CompletableFuture.completedFuture(o.get())
                    : CompletableFuture.failedFuture(new CompletionException(new IllegalStateException("skin textures failed to load")));
            }
            return CompletableFuture.completedFuture(v);
        });
    }

    /** a 1.21.3 override's future of the skin as 1.21.4's future of an Optional skin (failed: empty, as 1.21.4's loader answers) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CompletableFuture optionalSkinFuture(CompletableFuture future) {
        if (future == null) {
            return null;
        }
        return future.handle((v, ex) -> ex != null ? Optional.empty() : v instanceof Optional ? v : Optional.ofNullable(v));
    }

    // ---- metadata sections ----

    /** 1.21.3 serializers as 1.21.4 section types */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection types(Collection sections) {
        return CbRestMetadata.types(sections);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection serializers(Collection sections) {
        return CbRestMetadata.serializers(sections);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set typeSet(Set sections) {
        return sections == null ? null : new LinkedHashSet(CbRestMetadata.types(sections));
    }

    /** was SpriteLoader.DEFAULT_METADATA_SECTIONS (field_45632): the section types as 1.21.3's serializers */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set field_45632() {
        return new LinkedHashSet(CbRestMetadata.serializers(net.minecraft.class_7766.field_45632));
    }

    // ---- model output consumers ----

    /** a 1.21.3 consumer handed a 1.21.4 consumer: what it puts in is made a ModelInstance */
    private record OldView(BiConsumer<net.minecraft.class_2960, net.minecraft.class_10411> inner)
        implements BiConsumer<net.minecraft.class_2960, Supplier<com.google.gson.JsonElement>> {
        @Override
        public void accept(net.minecraft.class_2960 id, Supplier<com.google.gson.JsonElement> json) {
            this.inner.accept(id, json instanceof net.minecraft.class_10411 instance ? instance : (net.minecraft.class_10411) json::get);
        }
    }

    /** 1.21.4's model output as the consumer a 1.21.3 override writes Suppliers to */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BiConsumer oldModelOutput(BiConsumer output) {
        if (output == null || output instanceof OldView) {
            return output;
        }
        return new OldView(output);
    }

    /** a 1.21.3 model output handed where 1.21.4 writes ModelInstances (Suppliers already) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static BiConsumer newModelOutput(BiConsumer output) {
        return output instanceof OldView view ? view.inner() : output;
    }
}
