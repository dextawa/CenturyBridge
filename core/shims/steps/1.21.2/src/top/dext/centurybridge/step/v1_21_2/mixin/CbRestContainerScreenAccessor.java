package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractContainerScreen.leftPos / topPos (protected): where a 1.21.1 recipe book component draws its ghost recipe */
@Environment(EnvType.CLIENT)
@Mixin(class_465.class)
public interface CbRestContainerScreenAccessor {

    @Accessor("field_2776")
    int cb$leftPos();

    @Accessor("field_2800")
    int cb$topPos();
}
