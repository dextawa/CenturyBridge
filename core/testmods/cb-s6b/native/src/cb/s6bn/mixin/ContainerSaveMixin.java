package cb.s6bn.mixin;

import cb.s6bn.Counter;
import net.minecraft.class_11368;
import net.minecraft.class_11372;
import net.minecraft.class_1277;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.6: HEAD of SimpleContainer.storeAsItemList / fromItems, which InventoryCarrier's write / read call */
@Mixin(class_1277.class)
public class ContainerSaveMixin {
    @Inject(method = "method_7660(Lnet/minecraft/class_11372$class_11373;)V", at = @At("HEAD"))
    private void cbS6bn$save(class_11372.class_11373<?> out, CallbackInfo ci) {
        Counter.bump("cb.s6bn.container.save");
    }

    @Inject(method = "method_7659(Lnet/minecraft/class_11368$class_11369;)V", at = @At("HEAD"))
    private void cbS6bn$load(class_11368.class_11369<?> in, CallbackInfo ci) {
        Counter.bump("cb.s6bn.container.load");
    }
}
