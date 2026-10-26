package cb.s6b.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * a 1.21.5 mod's mixin on RecipeBookSettings' private write / read(FriendlyByteBuf) (its STREAM_CODEC's member
 * encoder and decoder): counts both (S6bTestMod.settingsNet 0 / 1)
 */
@Mixin(net.minecraft.class_5411.class)
public abstract class S6bRecipeSettingsMixin {

    @Inject(method = "method_30190(Lnet/minecraft/class_2540;)V", at = @At("HEAD"))
    private void cbs6b$write(net.minecraft.class_2540 buf, CallbackInfo ci) {
        cb.s6b.S6bTestMod.settingsNet[0]++;
    }

    @Inject(method = "method_30186(Lnet/minecraft/class_2540;)Lnet/minecraft/class_5411;", at = @At("RETURN"))
    private static void cbs6b$read(net.minecraft.class_2540 buf, CallbackInfoReturnable<net.minecraft.class_5411> cir) {
        cb.s6b.S6bTestMod.settingsNet[1]++;
    }
}
