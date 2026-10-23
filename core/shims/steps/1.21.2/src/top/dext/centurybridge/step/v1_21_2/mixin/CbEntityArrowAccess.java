package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1665;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** AbstractArrow's protected isInGround / setInGround, 1.21.1's protected inGround field (CbEntityTail). */
@Mixin(class_1665.class)
public interface CbEntityArrowAccess {

    @Invoker("method_65059")
    boolean cb$isInGround();

    @Invoker("method_65058")
    void cb$setInGround(boolean inGround);
}
