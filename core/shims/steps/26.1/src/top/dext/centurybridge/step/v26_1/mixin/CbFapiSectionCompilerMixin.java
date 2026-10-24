package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbFapiBlockLayers;

/**
 * 1.21.11's cutout-leaves flag was ItemBlockRenderTypes' static, set from the option by the level renderer's
 * allChanged (which on 26.1 builds the section compiler with it) and settable by mods; the compiler reads it from
 * there (rt/CbFapiBlockLayers).
 */
@Environment(EnvType.CLIENT)
@Mixin(SectionCompiler.class)
public abstract class CbFapiSectionCompilerMixin {
    @Shadow
    @Final
    private boolean cutoutLeaves;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$publishCutoutLeaves(CallbackInfo ci) {
        CbFapiBlockLayers.setCutoutLeaves(this.cutoutLeaves);
    }

    @Redirect(method = "compile", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/SectionCompiler;cutoutLeaves:Z"))
    private boolean cb$cutoutLeaves(SectionCompiler self) {
        return CbFapiBlockLayers.cutoutLeaves();
    }
}
