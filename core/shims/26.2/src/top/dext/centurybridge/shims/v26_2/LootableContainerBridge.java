package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 moved loot generation off LootableContainerBlockEntity and onto the
 * LootableInventory interface (net.minecraft.world.RandomizableContainer.unpackLootTable). Same contract, same
 * argument, different home -- so the old call site works again by delegating
 * through the interface the block entity now implements.
 */
@Mixin(net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity.class)
public abstract class LootableContainerBridge {

    /** 1.20.1 checkLootInteraction(PlayerEntity): now LootableInventory.generateLoot. */
    public void method_11289(net.minecraft.world.entity.player.Player player) {
        ((net.minecraft.world.RandomizableContainer) (Object) this).unpackLootTable(player);
    }
}
