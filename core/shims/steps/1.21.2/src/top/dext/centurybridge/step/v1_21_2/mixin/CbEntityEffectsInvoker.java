package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Collection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** LivingEntity's protected 1.21.2 onEffectsRemoved, called virtually by CbEntityLiving. */
@Mixin(net.minecraft.class_1309.class)
public interface CbEntityEffectsInvoker {

    @Invoker("method_6129")
    void cb$onEffectsRemoved(Collection<net.minecraft.class_1293> effects);
}
