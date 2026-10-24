package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbFapiModels;

/** the ModelBakery of the latest reload, for 1.21.1 Fabric API's bake contexts' loader() (CbFapiModels) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1088.class)
public abstract class CbFapiModelBakeryMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$current(CallbackInfo ci) {
        CbFapiModels.bakery((net.minecraft.class_1088) (Object) this);
    }
}
