package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1295;
import net.minecraft.class_1297;
import net.minecraft.class_1303;
import net.minecraft.class_1508;
import net.minecraft.class_1511;
import net.minecraft.class_1538;
import net.minecraft.class_1540;
import net.minecraft.class_1541;
import net.minecraft.class_1542;
import net.minecraft.class_3218;
import net.minecraft.class_6335;
import net.minecraft.class_8113;
import net.minecraft.class_8150;
import net.minecraft.class_9461;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityHurt;

/**
 * The twelve classes on which 1.21.2 made hurtServer final: a mod subclass cannot override it, so the override
 * hook that reconnects a 1.21.1 hurt elsewhere is not synthesized there (the engine skips it); this hands the
 * call to the mod class's old hurt instead (CbEntityHurt#finalHurtServer).
 */
@Mixin({class_1295.class, class_8113.class, class_1303.class, class_8150.class, class_1538.class, class_6335.class,
    class_9461.class, class_1508.class, class_1511.class, class_1540.class, class_1542.class, class_1541.class})
public abstract class CbEntityHurtFinalMixin {

    @Inject(method = "method_64397", at = @At("HEAD"), cancellable = true)
    private void cb$oldHurt(class_3218 level, class_1282 source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Boolean r = CbEntityHurt.finalHurtServer((class_1297) (Object) this, level, source, amount);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
