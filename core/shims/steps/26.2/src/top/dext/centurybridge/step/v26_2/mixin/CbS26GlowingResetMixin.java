package top.dext.centurybridge.step.v26_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26GlowingEntities;

/**
 * 26.1.2's LevelRenderState.reset() cleared haveGlowingEntities (javap) with the frame's other per-frame state; 26.2's
 * reset (LevelExtractor.extract calls it where 26.1.2's extraction did) clears the restored field
 * (mixin/CbS26GlowingStateMixin) the same way.
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderState.class)
public abstract class CbS26GlowingResetMixin {
    @Inject(method = "reset", at = @At("TAIL"))
    private void cbS26$clearGlowing(CallbackInfo ci) {
        ((CbS26GlowingEntities) (Object) this).cbS26$setHaveGlowingEntities(false);
    }
}
