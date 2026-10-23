package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel;

/** 1.21.3's MissingBlockModel.missingModel() named its BlockModel "missingno" (BlockModel.name) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10096.class)
public abstract class CbModelMissingNameMixin {

    @Inject(method = "method_62629", at = @At("RETURN"))
    private static void cb$name(CallbackInfoReturnable<net.minecraft.class_1100> cir) {
        if (cir.getReturnValue() instanceof CbModelBlockModel.CbLegacyBlockModel model) {
            model.cb$setName("missingno");
        }
    }
}
