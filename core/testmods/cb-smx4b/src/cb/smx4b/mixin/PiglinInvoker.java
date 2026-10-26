package cb.smx4b.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.class_4836.class)
public interface PiglinInvoker {
    @Invoker(value = "method_30238", remap = false)
    void cbsmx4b$playAngrySound();
}
