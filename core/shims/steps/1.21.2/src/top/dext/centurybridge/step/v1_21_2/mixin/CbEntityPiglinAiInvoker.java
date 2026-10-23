package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_4836;
import net.minecraft.class_4838;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** PiglinAi's protected canAdmire, for 1.21.1 PiglinAi.mobInteract on a client level (CbEntityLevels). */
@Mixin(class_4838.class)
public interface CbEntityPiglinAiInvoker {

    @Invoker("method_27086")
    static boolean cb$canAdmire(class_4836 piglin, class_1799 stack) {
        throw new AssertionError();
    }
}
