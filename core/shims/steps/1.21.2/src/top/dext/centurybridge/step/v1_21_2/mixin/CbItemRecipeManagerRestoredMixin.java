package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Optional;
import net.minecraft.class_1799;
import net.minecraft.class_1860;
import net.minecraft.class_1863;
import net.minecraft.class_1937;
import net.minecraft.class_2371;
import net.minecraft.class_3956;
import net.minecraft.class_8786;
import net.minecraft.class_9695;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeManager;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipes;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1's RecipeManager.getRemainingItemsFor(type, input, level), restored with 1.21.1's body so a mod's mixin on it
 * finds its method and anchors: the matching recipe's remainders (Recipe.getRemainingItems, CbItemRecipes), else a
 * copy of the input's stacks. 1.21.2's ResultSlot asks it on a server level (CbItemResultSlotRemainingMixin) and
 * follows it only where it answers otherwise than that body. Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_1863.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbItemRecipeManagerRestoredMixin implements CbItemRecipeManager.RemainingItemsFor {

    /** getRemainingItemsFor(type, input, level) */
    @CbVanillaReads("CbItemResultSlotRemainingMixin: ResultSlot.getRemainingItems (the crafting result slot's take) answers from it")
    @Override
    public <I extends class_9695, T extends class_1860<I>> class_2371<class_1799> method_8128(class_3956<T> type, I input,
            class_1937 level) {
        Optional<class_8786<T>> found = ((class_1863) (Object) this).method_8132(type, input, level);
        if (found.isPresent()) {
            return CbItemRecipes.method_8111(found.get().comp_1933(), input);
        }
        class_2371<class_1799> out = class_2371.method_10213(input.method_59983(), class_1799.field_8037);
        for (int i = 0; i < out.size(); i++) {
            out.set(i, input.method_59984(i));
        }
        return out;
    }
}
