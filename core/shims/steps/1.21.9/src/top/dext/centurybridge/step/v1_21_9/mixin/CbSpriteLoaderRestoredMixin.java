package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_1079;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_7677;
import net.minecraft.class_7766;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's SpriteLoader.loadAndStitch(manager, atlasInfo, mipLevel, executor) and its metadata-sections overload taking
 * a Collection, which 1.21.9 narrowed to a Set and lost the default (javap -c both). 1.21.8's bodies: the first is the
 * second with DEFAULT_METADATA_SECTIONS = Set.of(AnimationMetadataSection.TYPE); the second built its
 * SpriteResourceLoader from the sections, as 1.21.9's Set overload does: it gets the same sections as a Set.
 * 1.21.9 never calls them.
 */
@Mixin(value = class_7766.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSpriteLoaderRestoredMixin {

    @Shadow
    public abstract CompletableFuture<class_7766.class_7767> method_47661(class_3300 manager, class_2960 atlasInfo, int mipLevel,
                                                                        Executor executor, Set<class_7677<?>> sections);

    /** SpriteLoader.loadAndStitch(manager, atlasInfoLocation, mipLevel, executor) */
    public CompletableFuture<class_7766.class_7767> method_52849(class_3300 manager, class_2960 atlasInfo, int mipLevel, Executor executor) {
        return this.method_47661(manager, atlasInfo, mipLevel, executor, Set.of(class_1079.field_55537));
    }

    /** SpriteLoader.loadAndStitch(manager, atlasInfoLocation, mipLevel, executor, metadataSections) */
    public CompletableFuture<class_7766.class_7767> method_47661(class_3300 manager, class_2960 atlasInfo, int mipLevel, Executor executor,
                                                               Collection<class_7677<?>> sections) {
        return this.method_47661(manager, atlasInfo, mipLevel, executor, Set.copyOf(sections));
    }
}
