package cb.s212n.mixin;

import cb.s212n.Counts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21.2 AbstractFurnaceBlockEntity.getTotalCookTime(ServerLevel, be) */
@Mixin(net.minecraft.class_2609.class)
public abstract class NativeCookTimeMixin {
    @Inject(method = "method_17029(Lnet/minecraft/class_3218;Lnet/minecraft/class_2609;)I", at = @At("RETURN"), remap = false)
    private static void cbs212n$cookTime(net.minecraft.class_3218 level, net.minecraft.class_2609 be, CallbackInfoReturnable<Integer> cir) {
        Counts.cookTime++;
    }
}
