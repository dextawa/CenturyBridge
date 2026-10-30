package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_1079;
import net.minecraft.class_1058;
import net.minecraft.class_10209;
import net.minecraft.class_10214;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3302;
import net.minecraft.class_7677;
import net.minecraft.class_7766;

/**
 * 1.21.8's TextureAtlasHolder (class_4075), which 1.21.9 removed with its three vanilla subclasses when every atlas
 * moved into AtlasManager. 1.21.8's bodies (javap -c): the holder makes its atlas, registers it with the texture
 * manager under the atlas location, and is a reload listener that stitches the sprites listed by its atlas definition
 * (SpriteLoader.loadAndStitch with its metadata sections), waits for them and uploads them into the atlas.
 *
 * <p>1.21.9's reload shape reaches 1.21.8's reload with the shared state's resource manager (all 1.21.8 had, as
 * rt/CbReload does for every 1.21.8 listener). A 1.21.8 mod's own holder (a subclass, or {@code new} of a vanilla one)
 * keeps its own atlas, loaded wherever the mod registers it as 1.21.8 did.
 *
 * <p>The holders 1.21.8's Minecraft handed out (getPaintingTextures, getMapDecorationTextures, getGuiSprites) are views
 * of the same atlas in 1.21.9's AtlasManager ({@link #TextureAtlasHolder(Supplier)}): their sprites are AtlasManager's,
 * loaded by AtlasManager, so their reload does not stitch the atlas a second time.
 */
@Environment(EnvType.CLIENT)
public abstract class TextureAtlasHolder implements class_3302, AutoCloseable {
    // public: a facade's members stand in for the class's, and access wideners never reach a CenturyBridge class
    public final class_1059 field_18230;
    public final class_2960 field_41425;
    public final Set<class_7677<?>> field_45644;
    /** a view's atlas (looked up in AtlasManager at each use), null for a holder of its own */
    private final Supplier<class_1059> cb$view;

    public TextureAtlasHolder(class_1060 textureManager, class_2960 atlasLocation, class_2960 atlasInfoLocation) {
        // 1.21.8 SpriteLoader.DEFAULT_METADATA_SECTIONS = Set.of(AnimationMetadataSection.TYPE)
        this(textureManager, atlasLocation, atlasInfoLocation, Set.of(class_1079.field_55537));
    }

    public TextureAtlasHolder(class_1060 textureManager, class_2960 atlasLocation, class_2960 atlasInfoLocation,
                              Set<class_7677<?>> metadataSections) {
        this.field_41425 = atlasInfoLocation;
        this.field_18230 = new class_1059(atlasLocation);
        textureManager.method_4616(this.field_18230.method_24106(), this.field_18230);
        this.field_45644 = metadataSections;
        this.cb$view = null;
    }

    /** a view of an atlas 1.21.9's AtlasManager owns and loads */
    protected TextureAtlasHolder(Supplier<class_1059> view) {
        this.field_41425 = null;
        this.field_18230 = null;
        this.field_45644 = Set.of();
        this.cb$view = view;
    }

    private class_1059 cb$atlas() {
        return this.cb$view != null ? this.cb$view.get() : this.field_18230;
    }

    public class_1058 method_18667(class_2960 sprite) {
        return this.cb$atlas().method_4608(sprite);
    }

    /** 1.21.8 PreparableReloadListener.reload(barrier, resourceManager, backgroundExecutor, gameExecutor) */
    public final CompletableFuture<Void> method_25931(class_3302.class_4045 barrier, class_3300 manager, Executor background,
                                                     Executor game) {
        if (this.cb$view != null) {
            return barrier.<Void>method_18352(null);
        }
        return class_7766.method_45837(this.field_18230)
            .method_47661(manager, this.field_41425, 0, background, this.field_45644)
            .thenCompose(preparations -> preparations.comp_1045().thenApply(ready -> preparations))
            .thenCompose(barrier::method_18352)
            .thenAcceptAsync(this::method_18666, game);
    }

    /** 1.21.9 PreparableReloadListener.reload(state, backgroundExecutor, barrier, gameExecutor) */
    @Override
    public final CompletableFuture<Void> method_25931(class_3302.class_11558 state, Executor background, class_3302.class_4045 barrier,
                                                     Executor game) {
        return this.method_25931(barrier, state.method_72361(), background, game);
    }

    public void method_18666(class_7766.class_7767 preparations) {
        try (class_10214 zone = class_10209.method_64146().method_64145("upload")) {
            this.field_18230.method_45848(preparations);
        }
    }

    @Override
    public void close() {
        this.cb$atlas().method_4601();
    }
}
