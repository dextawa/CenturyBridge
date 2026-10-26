package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26OutlineFrame;

/**
 * 26.1.2 composited the entity outline target when LevelRenderState.haveGlowingEntities was set (javap: renderLevel's
 * outline post chain, addMainPass's outline target); 26.2 asks PreparedFrame.hasAnyOutline() (an outline feature was
 * submitted) at the same two places, its only readers. The answer also holds when the frame's copy of the restored
 * flag (set by mixin/CbS26GlowingPassMixin) is set: what 26.1.2 composited for, 26.2 composites, and 26.2's own
 * outline submits still composite. Injected at RETURN only: mixins on hasAnyOutline still run and decide first.
 */
@Environment(EnvType.CLIENT)
@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public abstract class CbS26GlowingFrameMixin implements CbS26OutlineFrame {
    @Unique
    private boolean cbS26$legacyOutline;

    @Override
    public void cbS26$setLegacyOutline(boolean value) {
        this.cbS26$legacyOutline = value;
    }

    @Inject(method = "hasAnyOutline", at = @At("RETURN"), cancellable = true)
    private void cbS26$orLegacyOutline(CallbackInfoReturnable<Boolean> cir) {
        if (this.cbS26$legacyOutline && !cir.getReturnValueZ()) {
            cir.setReturnValue(true);
        }
    }
}
