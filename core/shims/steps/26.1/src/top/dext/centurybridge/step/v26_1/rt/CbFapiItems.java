package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/**
 * Fabric API's stack-aware recipe remainder: 1.21.11's FabricItem.getRecipeRemainder(stack) /
 * FabricItemStack.getRecipeRemainder() answered an ItemStack (EMPTY for none); 26.1's
 * getCraftingRemainder answers an ItemStackTemplate (null for none -- 26.1's recipes create() a non-null one
 * and take EMPTY otherwise). A 1.21.11 call takes 26.1's answer converted (the item's or stack's own
 * override answers, as the virtual call did); a 1.21.11 override is reached through 26.1's shape with its
 * stack converted back (overrideHooks); a 1.21.11 super call runs 1.21.11's default body.
 */
public final class CbFapiItems {
    private CbFapiItems() {
    }

    public static ItemStack stack(ItemStackTemplate template) {
        return template == null ? ItemStack.EMPTY : template.create();
    }

    public static ItemStackTemplate template(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(stack);
    }

    /** FabricItem.getRecipeRemainder(stack), called on any item */
    public static ItemStack getRecipeRemainder(FabricItem item, ItemStack stack) {
        return stack(item.getCraftingRemainder(stack));
    }

    /** a 1.21.11 super.getRecipeRemainder(stack): 1.21.11's default, the item's own remainder */
    public static ItemStack superRecipeRemainder(FabricItem item, ItemStack stack) {
        return stack(((Item) item).getCraftingRemainder());
    }

    /** FabricItemStack.getRecipeRemainder(), called on any stack */
    public static ItemStack getRecipeRemainder(FabricItemStack stack) {
        return stack(stack.getCraftingRemainder());
    }

    /** a 1.21.11 FabricItemStack.super.getRecipeRemainder(): 1.21.11's default, the item asked with the stack */
    public static ItemStack superRecipeRemainder(FabricItemStack stack) {
        ItemStack self = (ItemStack) (Object) stack; // FabricItemStack is injected into ItemStack (final) at run time
        // Item implements FabricItem at run time (Fabric API's injected interface, a mixin)
        return stack(((FabricItem) self.getItem()).getCraftingRemainder(self));
    }
}
