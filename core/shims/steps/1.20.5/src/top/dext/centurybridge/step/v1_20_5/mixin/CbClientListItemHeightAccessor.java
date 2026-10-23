package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractSelectionList.itemHeight, which 1.20.4's OptionsList constructor took as an argument (rt/CbClientGui) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_350.class)
public interface CbClientListItemHeightAccessor {

    @Mutable
    @Accessor("field_22741")
    void cb$setItemHeight(int itemHeight);
}
