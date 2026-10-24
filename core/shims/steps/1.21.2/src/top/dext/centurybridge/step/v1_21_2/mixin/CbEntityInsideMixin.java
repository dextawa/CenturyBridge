package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import java.util.Set;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityInside;

/**
 * 1.21.2's applyEffectsFromBlocks visits the blocks along the tick's movement in this private loop, where 1.21.1
 * ran the overridable tryCheckInsideBlocks / checkInsideBlocks: for a mod class overriding either, 1.21.1's
 * tryCheckInsideBlocks runs instead (CbEntityInside), so the override sees the blocks.
 */
@Mixin(class_1297.class)
public abstract class CbEntityInsideMixin {

    @Inject(method = "method_65037", at = @At("HEAD"), cancellable = true)
    private void cb$legacyInside(List<?> movements, Set<?> blocksInside, CallbackInfo ci) {
        if (this instanceof CbEntityInside.LegacyTry || this instanceof CbEntityInside.LegacyCheck) {
            CbEntityInside.tryCheckInsideBlocks((class_1297) (Object) this);
            ci.cancel();
        }
    }
}
