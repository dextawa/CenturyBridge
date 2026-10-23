package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_4836;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Piglin's protected holdInOffHand, for 1.21.1 PiglinAi.mobInteract on a client level (CbEntityLevels). */
@Mixin(class_4836.class)
public interface CbEntityPiglinInvoker {

    @Invoker("method_24845")
    void cb$holdInOffHand(class_1799 stack);
}
