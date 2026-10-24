package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10015;
import net.minecraft.class_1510;
import net.minecraft.class_9871;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityDragon;

/**
 * 1.21.1's dragon model asked the dragon's getHeadPartYOffset(i, a, b); 1.21.2's asks the render state's. The
 * state's flight history is a copy of the dragon's and knows it: a mod dragon overriding 1.21.1's answers.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_10015.class)
public abstract class CbEntityDragonStateMixin {

    @Inject(method = "method_62612", at = @At("HEAD"), cancellable = true)
    private void cb$headPartYOffset(int index, class_9871.class_9872 a, class_9871.class_9872 b,
                                    CallbackInfoReturnable<Float> cir) {
        class_1510 owner = ((CbEntityDragon.History) ((class_10015) (Object) this).field_53321).cb$owner();
        if (owner instanceof CbEntityDragon.LegacyHeadOffset legacy) {
            cir.setReturnValue(legacy.method_6823(index, CbEntityDragon.position(a), CbEntityDragon.position(b)));
        }
    }
}
