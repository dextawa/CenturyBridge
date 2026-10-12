package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * CriterionTrigger.Listener's instance component, read as a field: a converted 1.20.1 Listener
 * subclass that overrides getTriggerInstance() also answers 1.20.2's trigger() (adapterHooks), so
 * its super.getTriggerInstance() -- 1.20.1's field read -- must not go through the accessor again.
 */
@Mixin(net.minecraft.class_179.class_180.class)
public interface CbAdvListenerAccess {

    @Accessor("comp_1925")
    net.minecraft.class_184 cb$instance();
}
