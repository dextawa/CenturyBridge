package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Entity's protected onInsideBlock, called virtually by CbEntityInside (1.21.1's checkInsideBlocks body). */
@Mixin(net.minecraft.class_1297.class)
public interface CbEntityInsideInvoker {

    @Invoker("method_5622")
    void cb$onInsideBlock(net.minecraft.class_2680 state);
}
