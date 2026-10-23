package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Options.percentValueOrOffLabel, the caption 1.20.6's blurriness slider used (CbClientGui.cbBlurrinessView) */
@Mixin(net.minecraft.class_315.class)
public interface CbClientOptionsAccess {

    @Invoker("method_59835")
    static net.minecraft.class_2561 cb$percentValueOrOffLabel(net.minecraft.class_2561 caption, double value) {
        throw new AssertionError();
    }
}
