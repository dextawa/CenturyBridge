package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_10162;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Item.Properties.model, which 1.21.3's overrideModel(id) set (CbRestItems). */
@Mixin(class_1792.class_1793.class)
public interface CbRestItemPropertiesAccess {
    @Accessor("field_54119")
    void cb$setModel(class_10162<class_1792, class_2960> model);
}
