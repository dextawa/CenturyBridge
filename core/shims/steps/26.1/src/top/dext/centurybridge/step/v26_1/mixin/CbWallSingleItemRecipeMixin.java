package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbWallRecipes;

/**
 * 1.21.11's SingleItemRecipe took a group in its constructor and answered it from group(); 26.1's keeps none
 * and leaves group() to its subclasses (stonecutting answers ""). The group a 1.21.11 constructor gave is kept
 * here and answered by group() -- given back for a 1.21.11 subclass that relied on SingleItemRecipe's -- and by
 * StonecutterRecipe's (CbWallStonecutterRecipeMixin).
 */
@Mixin(SingleItemRecipe.class)
public abstract class CbWallSingleItemRecipeMixin implements CbWallRecipes.SingleItemGroup {

    @Unique
    private String cb$group;

    @Shadow
    protected abstract ItemStackTemplate result();

    @Override
    public ItemStack cb$resultStack() {
        return this.result().create();
    }

    @Override
    public void cb$setGroup(String group) {
        this.cb$group = group;
    }

    @Override
    public String cb$group() {
        return this.cb$group;
    }

    public String group() {
        return this.cb$group == null ? "" : this.cb$group;
    }
}
