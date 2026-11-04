package cb.sl6n.mixin;

import cb.sl6n.Count;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1799;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native-1.21 control: @Local ServerLevel (the type 1.21 declares) at the same call of $17; runs with or without the layer */
@Mixin(targets = "net/minecraft/class_2357$17")
public abstract class NativeLocalMixin {
    @Inject(method = "method_10135", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2530;method_10738(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;)V"))
    private void cbsl6n$native(CallbackInfoReturnable<class_1799> cir, @Local class_3218 level) {
        Count.hit("native");
    }
}
