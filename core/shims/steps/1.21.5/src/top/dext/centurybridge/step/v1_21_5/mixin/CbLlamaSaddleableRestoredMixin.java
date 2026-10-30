package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1501;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * Llama's 1.21.4 isSaddleable(): false (javap -c -p 1.21.4 class_1501.method_6765), over AbstractHorse's restored one
 * (CbHorseSaddleableRestoredMixin). 1.21.5 keeps llamas unsaddled through the saddle's Equippable, whose allowed entity
 * types exclude them (its canUseSlot answers true for every slot).
 */
@Mixin(value = class_1501.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbLlamaSaddleableRestoredMixin {

    @CbVanillaReads("CbSaddleSlotHooksMixin: AbstractHorse.canUseSlot(SADDLE) answers a converted subclass's override")
    public boolean method_6765() {
        return false;
    }
}
