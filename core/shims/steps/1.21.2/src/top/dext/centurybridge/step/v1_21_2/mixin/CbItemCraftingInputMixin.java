package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_9694;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.facade.StackedContents;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeClasses;

/**
 * The 1.21.1 StackedContents a CraftingInput kept (1.21.2 keeps a StackedItemContents instead): one per input, as
 * 1.21.1 built it once in the constructor, made at the first ask (CbItemRecipeClasses.method_59988).
 */
@Mixin(class_9694.class)
public abstract class CbItemCraftingInputMixin implements CbItemRecipeClasses.LegacyStacked {

    @Unique
    private StackedContents cb$stacked;

    @Override
    public StackedContents cb$stacked() {
        return cb$stacked;
    }

    @Override
    public void cb$setStacked(StackedContents stacked) {
        this.cb$stacked = stacked;
    }
}
