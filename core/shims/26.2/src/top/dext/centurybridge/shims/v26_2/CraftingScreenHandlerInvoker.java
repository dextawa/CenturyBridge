package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** CraftingMenu.slotChangedCraftingGrid is protected static: Statics reaches it through here. */
@Mixin(net.minecraft.world.inventory.CraftingMenu.class)
public interface CraftingScreenHandlerInvoker {

    @Invoker("slotChangedCraftingGrid")
    static void cb$updateResult(net.minecraft.world.inventory.AbstractContainerMenu handler,
            net.minecraft.server.level.ServerLevel world, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.inventory.CraftingContainer input, net.minecraft.world.inventory.ResultContainer result,
            net.minecraft.world.item.crafting.RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe> recipe) {
        throw new AssertionError();
    }
}
