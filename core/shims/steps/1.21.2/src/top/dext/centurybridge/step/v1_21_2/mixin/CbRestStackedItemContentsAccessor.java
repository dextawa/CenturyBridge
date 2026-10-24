package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1662;
import net.minecraft.class_1792;
import net.minecraft.class_6880;
import net.minecraft.class_9875;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** StackedItemContents.raw (private): the item counts, read into a 1.21.1 StackedContents (CbRestRecipeMenus.counts) */
@Mixin(class_9875.class)
public interface CbRestStackedItemContentsAccessor {

    @Accessor("field_52506")
    class_1662<class_6880<class_1792>> cb$raw();
}
