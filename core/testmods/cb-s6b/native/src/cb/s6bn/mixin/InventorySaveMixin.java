package cb.s6bn.mixin;

import cb.s6bn.Counter;
import net.minecraft.class_11368;
import net.minecraft.class_11372;
import net.minecraft.class_1661;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.6: HEAD of Inventory.save / load (typed lists), on the path the layer's player save route sits on */
@Mixin(class_1661.class)
public class InventorySaveMixin {
    @Inject(method = "method_7384(Lnet/minecraft/class_11372$class_11373;)V", at = @At("HEAD"))
    private void cbS6bn$save(class_11372.class_11373<?> out, CallbackInfo ci) {
        Counter.bump("cb.s6bn.inventory.save");
    }

    @Inject(method = "method_7397(Lnet/minecraft/class_11368$class_11369;)V", at = @At("HEAD"))
    private void cbS6bn$load(class_11368.class_11369<?> in, CallbackInfo ci) {
        Counter.bump("cb.s6bn.inventory.load");
    }
}
