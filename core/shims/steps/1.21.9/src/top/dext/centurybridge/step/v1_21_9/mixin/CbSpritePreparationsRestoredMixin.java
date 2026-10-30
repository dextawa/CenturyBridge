package top.dext.centurybridge.step.v1_21_9.mixin;

import java.util.concurrent.CompletableFuture;
import net.minecraft.class_7766;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's SpriteLoader.Preparations.waitForUpload(), which 1.21.9 dropped (its callers wait on readyForUpload
 * themselves). 1.21.8's body (javap -c): readyForUpload.thenApply(v -> this). 1.21.9 never calls it.
 */
@Mixin(value = class_7766.class_7767.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSpritePreparationsRestoredMixin {

    @Shadow
    public abstract CompletableFuture<Void> comp_1045();

    /** SpriteLoader.Preparations.waitForUpload() */
    public CompletableFuture<class_7766.class_7767> method_45845() {
        return this.comp_1045().thenApply(ready -> (class_7766.class_7767) (Object) this);
    }
}
