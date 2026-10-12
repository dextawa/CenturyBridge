package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_2195;
import net.minecraft.class_2246;
import net.minecraft.class_2445;
import net.minecraft.class_2513;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2's PumpkinBlock was a StemGrownBlock and named its stems; 1.20.3's is a plain Block. The two
 * methods come back with their 1.20.2 bodies (Blocks.PUMPKIN_STEM / ATTACHED_PUMPKIN_STEM are still a
 * StemBlock and an AttachedStemBlock), for the mods that call them and the subclasses that override them.
 */
@Mixin(class_2445.class)
public abstract class CbWorldPumpkinMixin {

    public class_2513 method_10679() {
        return (class_2513) class_2246.field_46286;
    }

    public class_2195 method_10680() {
        return (class_2195) class_2246.field_46284;
    }
}
