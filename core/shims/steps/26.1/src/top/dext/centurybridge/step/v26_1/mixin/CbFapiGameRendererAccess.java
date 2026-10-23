package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** whether this frame draws the block outline (1.21.11's WorldExtractionContext.blockOutlines; rt/CbFapiWorldContexts) */
@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public interface CbFapiGameRendererAccess {

    @Invoker("shouldRenderBlockOutline")
    boolean cb$shouldRenderBlockOutline();
}
