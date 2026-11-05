package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1086;
import net.minecraft.class_4990;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbModelRotationQuadrantHolder;
import top.dext.centurybridge.step.v1_21_11.rt.CbModelRotationQuadrants;

/**
 * Fills 1.21.10 ModelRotation's restored x / y Quadrants (mixin/CbModelRotationRestoredMixin) at the end of 1.21.11's
 * ModelRotation(OctahedralGroup): those of the 1.21.10 constant with that group, else null (rt/CbModelRotationQuadrants).
 */
@Environment(EnvType.CLIENT)
@Mixin(class_1086.class)
public abstract class CbModelRotationMixinHooks {

    @Inject(method = "<init>(Lnet/minecraft/class_4990;)V", at = @At("TAIL"))
    private void cb$v1_21_11$quadrants(class_4990 group, CallbackInfo ci) {
        ((CbModelRotationQuadrantHolder) (Object) this).cb$v1_21_11$setQuadrants(CbModelRotationQuadrants.xOf(group),
            CbModelRotationQuadrants.yOf(group));
    }
}
