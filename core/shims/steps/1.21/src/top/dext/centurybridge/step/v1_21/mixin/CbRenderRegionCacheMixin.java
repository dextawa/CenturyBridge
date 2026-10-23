package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbRenderLevel;

/**
 * RenderRegionCache.createRegion(level, section) answers null when the centre section is empty; 1.20.6's
 * createRegion(level, from, to, padding) answered null only when every section in the box was. For a 1.20.6 call
 * whose box CbRenderLevel has found not empty, the region is built regardless.
 */
@Mixin(net.minecraft.class_6850.class)
public abstract class CbRenderRegionCacheMixin {

    @Redirect(method = "method_39969", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2818;method_60791(I)Z"))
    private boolean cb$sectionEmpty(net.minecraft.class_2818 chunk, int sectionY) {
        return !CbRenderLevel.cbRegionForced() && chunk.method_60791(sectionY);
    }
}
