package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ExecutionContext's command budget: the limit it started with and what is left of it */
@Mixin(net.minecraft.class_8854.class)
public interface CbExecutionContextAccess {

    /** commandLimit */
    @Accessor("field_46731")
    int cb$commandLimit();

    /** commandQuota */
    @Accessor("field_46735")
    int cb$commandQuota();
}
