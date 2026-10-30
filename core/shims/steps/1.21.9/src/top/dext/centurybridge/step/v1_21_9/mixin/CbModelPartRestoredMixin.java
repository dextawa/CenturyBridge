package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_630;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * ModelPart.copyFrom(other), which 1.21.9 dropped (javap: absent from 1.21.9's class_630): 1.21.8's body copies the
 * nine pose fields (scale x/y/z, rotation x/y/z, position x/y/z), every one still a public field of 1.21.9's
 * ModelPart. 1.21.9 never calls it.
 */
@Mixin(value = class_630.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbModelPartRestoredMixin {

    /** ModelPart.copyFrom(other) */
    public void method_17138(class_630 other) {
        class_630 self = (class_630) (Object) this;
        self.field_37938 = other.field_37938;
        self.field_37939 = other.field_37939;
        self.field_37940 = other.field_37940;
        self.field_3654 = other.field_3654;
        self.field_3675 = other.field_3675;
        self.field_3674 = other.field_3674;
        self.field_3657 = other.field_3657;
        self.field_3656 = other.field_3656;
        self.field_3655 = other.field_3655;
    }
}
