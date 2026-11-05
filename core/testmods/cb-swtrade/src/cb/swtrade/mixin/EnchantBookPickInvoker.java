package cb.swtrade.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.20.6 EnchantBookForEmeralds.getRandomEnchantment(random, features) (private method_59891), called directly */
@Mixin(targets = "net.minecraft.class_3853$class_1648")
public interface EnchantBookPickInvoker {
    @Invoker(value = "method_59891", remap = false)
    net.minecraft.class_1887 cbswtrade$pick(net.minecraft.class_5819 random, net.minecraft.class_7699 features);
}
