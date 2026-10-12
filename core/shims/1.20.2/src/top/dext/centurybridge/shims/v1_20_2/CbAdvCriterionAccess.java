package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Criterion's two components, read as fields: a converted 1.20.1 Criterion subclass that
 * overrides getTrigger() also answers 1.20.2's triggerInstance() (adapterHooks), so its
 * super.getTrigger() -- 1.20.1's field read -- must not go through the accessor again.
 */
@Mixin(net.minecraft.class_175.class)
public interface CbAdvCriterionAccess {

    @Accessor("comp_1923")
    net.minecraft.class_179<?> cb$trigger();

    @Accessor("comp_1924")
    net.minecraft.class_184 cb$instance();
}
