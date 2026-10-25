package cb.smx1.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** PathNavigation.canUpdatePath() (method_6358, protected; the same method on 1.20.2) */
@Mixin(net.minecraft.class_1408.class)
public interface NavigationInvoker {
    @Invoker(value = "method_6358", remap = false)
    boolean cbsmx1$canUpdatePath();
}
