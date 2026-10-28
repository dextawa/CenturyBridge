package cb.swallc.mixin;

import cb.swallc.SwallClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21.11 BiomeColors.getAverageColor (class_1163.method_4965, its BlockAndTintGetter): 26.1 takes the client BlockAndTintGetter */
@Mixin(net.minecraft.class_1163.class)
public abstract class BiomeColorMixin {
    @Inject(method = "method_4965(Lnet/minecraft/class_1920;Lnet/minecraft/class_2338;Lnet/minecraft/class_6539;)I", at = @At("HEAD"), remap = false)
    private static void cbswallc$average(net.minecraft.class_1920 level, net.minecraft.class_2338 pos, net.minecraft.class_6539 resolver,
            CallbackInfoReturnable<Integer> cir) {
        if (level != null) SwallClient.hit("biomeColors.getAverageColor");
    }
}
