package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod deciding the librarian's enchanted book: VillagerTrades.EnchantBookForTrades.getRandomEnchantment
 * (class_3853$class_1648.method_59891) answers Mending while Driver.forceMending is set, and every answer is recorded.
 */
@Mixin(targets = "net.minecraft.class_3853$class_1648")
public abstract class VillagerBookMixin {
    @Inject(method = "method_59891(Lnet/minecraft/class_5819;Lnet/minecraft/class_7699;)Lnet/minecraft/class_1887;", at = @At("HEAD"),
        cancellable = true, remap = false)
    private void cbsmx6$force(CallbackInfoReturnable<net.minecraft.class_1887> cir) {
        Driver.bookAsked++;
        if (Driver.forceMending) {
            cir.setReturnValue(net.minecraft.class_1893.field_9101);
        }
    }

    @Inject(method = "method_59891(Lnet/minecraft/class_5819;Lnet/minecraft/class_7699;)Lnet/minecraft/class_1887;", at = @At("RETURN"),
        remap = false)
    private void cbsmx6$record(CallbackInfoReturnable<net.minecraft.class_1887> cir) {
        Driver.lastPick = cir.getReturnValue();
    }
}
