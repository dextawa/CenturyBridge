package top.dext.centurybridge.shims.v1_20_2;

import java.util.function.Consumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * AdvancementProvider hands each sub-provider a Consumer&lt;AdvancementEntry&gt;; a 1.20.1
 * sub-provider (same descriptor) feeds it advancement values. The consumer is wrapped to take both
 * (rt/v1_20_2/CbAdvancementConsumers); a 1.20.2 sub-provider sees no difference.
 */
@Mixin(net.minecraft.class_2409.class)
public abstract class CbAdvancementProviderConsumer {

    @ModifyArg(method = "method_46809", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_7785;method_10335(Lnet/minecraft/class_7225$class_7874;Ljava/util/function/Consumer;)V"),
        index = 1)
    private Consumer<net.minecraft.class_8779> cb$acceptLegacyAdvancements(Consumer<net.minecraft.class_8779> entries) {
        return top.dext.centurybridge.rt.v1_20_2.CbAdvancementConsumers.dual(entries);
    }
}
