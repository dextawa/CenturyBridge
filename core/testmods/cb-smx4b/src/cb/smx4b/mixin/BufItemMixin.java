package cb.smx4b.mixin;

import cb.smx4b.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.20.4 FriendlyByteBuf.writeItem / readItem: counted */
@Mixin(net.minecraft.class_2540.class)
public abstract class BufItemMixin {
    @Inject(method = "method_10793(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2540;", at = @At("HEAD"), remap = false)
    private void cbsmx4b$writeItem(net.minecraft.class_1799 stack, CallbackInfoReturnable<net.minecraft.class_2540> cir) {
        Driver.writeItemAsked++;
    }

    @Inject(method = "method_10819()Lnet/minecraft/class_1799;", at = @At("HEAD"), remap = false)
    private void cbsmx4b$readItem(CallbackInfoReturnable<net.minecraft.class_1799> cir) {
        Driver.readItemAsked++;
    }
}
