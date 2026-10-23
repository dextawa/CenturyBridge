package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Screen's narration scheduling and narrator button, for 1.20.6's narrationEnabled() (CbClientGui) */
@Mixin(net.minecraft.class_437.class)
public interface CbClientScreenAccess {

    @Invoker("method_37059")
    void cb$scheduleNarration(long delay, boolean updateSuppressTime);

    @Accessor("field_33818")
    static long cb$narratorEnabledDelay() {
        throw new AssertionError();
    }

    @Accessor("field_52252")
    net.minecraft.class_5676<?> cb$narratorButton();
}
