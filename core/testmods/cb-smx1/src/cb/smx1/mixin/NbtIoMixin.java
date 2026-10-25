package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NbtIo.readUnnamedTag(DataInput, int depth, NbtAccounter) (method_10626, private static); 1.20.2 dropped the depth.
 * Counts the reads the driver makes on its own thread.
 */
@Mixin(net.minecraft.class_2507.class)
public abstract class NbtIoMixin {
    @Inject(method = "method_10626(Ljava/io/DataInput;ILnet/minecraft/class_2505;)Lnet/minecraft/class_2520;", at = @At("HEAD"), remap = false)
    private static void cbsmx1$read(CallbackInfoReturnable<net.minecraft.class_2520> cir) {
        if (Driver.ON_DRIVER.get()) {
            Driver.count("nbt-read-head");
        }
    }
}
