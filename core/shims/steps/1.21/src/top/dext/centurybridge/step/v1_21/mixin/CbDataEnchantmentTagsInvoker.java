package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** EnchantmentTagsProvider.tooltipOrder(registries, keys...), 1.21's, for 1.20.6's tooltipOrder (CbData) */
@Mixin(net.minecraft.class_9674.class)
public interface CbDataEnchantmentTagsInvoker {

    @Invoker("method_59781")
    void cb$tooltipOrder(net.minecraft.class_7225.class_7874 registries, net.minecraft.class_5321<net.minecraft.class_1887>[] keys);
}
