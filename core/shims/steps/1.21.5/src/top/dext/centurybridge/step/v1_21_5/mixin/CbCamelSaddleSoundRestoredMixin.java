package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_7689;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * Camel's 1.21.4 getSaddleSoundEvent(): CAMEL_SADDLE (javap -c -p 1.21.4 class_7689.method_45328), over the
 * Saddleable default (HORSE_SADDLE); 1.21.5's camel plays the same sound when its SADDLE slot is equipped
 * (class_7689.method_66667).
 */
@Mixin(value = class_7689.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbCamelSaddleSoundRestoredMixin {

    @CbVanillaReads("CbSaddleSoundHooksMixin: the SADDLE equip sound answers a converted subclass's override")
    public class_3414 method_45328() {
        return class_3417.field_40078.comp_349();
    }
}
