package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21.11 LiquidBlockRenderer.calculateAverageHeight (class_775.method_40077, its BlockAndTintGetter): 26.1's FluidRenderer takes the client getter */
@Mixin(net.minecraft.class_775.class)
public abstract class FluidHeightMixin {
    @Inject(method = "method_40077(Lnet/minecraft/class_1920;Lnet/minecraft/class_3611;FFFLnet/minecraft/class_2338;)F", at = @At("HEAD"), remap = false)
    private void cbswallc$height(net.minecraft.class_1920 level, net.minecraft.class_3611 fluid, float a, float b, float c, net.minecraft.class_2338 pos,
            CallbackInfoReturnable<Float> cir) {
        if (level != null) SwallClient.hit("fluid.calculateAverageHeight");
    }
}
