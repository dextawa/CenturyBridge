package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1856;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemIngredients;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's getItems() (a stack of each item, cached) restored as a member of Ingredient, beside 1.21.2's getItems() of
 * the same name that returns the item holders; 1.21.2's test(stack) reads it (CbItemIngredientMixin). Priority below
 * every mod's so a mod's mixin may anchor anywhere in it (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1856.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemIngredientStacksMixin implements CbItemIngredients.IngredientStacks {

    @Unique
    private class_1799[] cb$stacks;

    @Unique
    private List<class_6880<class_1792>> cb$stacksOf;

    /** 1.21.1's getItems(): a stack of each of the ingredient's items, rebuilt when 1.21.2's item list is */
    @CbVanillaReads("CbItemIngredientMixin: test(stack) answers from it (cb$testReadsItems)")
    @Override
    public class_1799[] method_8105() {
        List<class_6880<class_1792>> items = ((class_1856) (Object) this).method_8105();
        if (this.cb$stacks == null || this.cb$stacksOf != items) {
            this.cb$stacks = CbItemIngredients.stacksOf(items);
            this.cb$stacksOf = items;
        }
        return this.cb$stacks;
    }
}
