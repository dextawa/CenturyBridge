package cb.smx4bn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.class_1657.class)
public interface PlayerInvoker {
    @Invoker(value = "method_6105", remap = false)
    void cbsmx4bn$hurtArmor(net.minecraft.class_1282 source, float damage);
}
