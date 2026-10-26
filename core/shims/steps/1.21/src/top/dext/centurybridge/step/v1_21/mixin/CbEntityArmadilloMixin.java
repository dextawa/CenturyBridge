package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * Armadillo.shouldPanic() of 1.20.6 (on fire or freezing), given back (covers in s6fin-entity.json) with 1.20.6's
 * body. 1.20.6's armadillo panic behaviour asked it; 1.21's panics on the damage type that hurt it
 * (DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES) and asks nothing of the armadillo.
 */
@Mixin(value = net.minecraft.class_9069.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityArmadilloMixin {

    public boolean method_56997() {
        net.minecraft.class_9069 self = (net.minecraft.class_9069) (Object) this;
        return self.method_5809() || self.method_40071();
    }
}
