package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1657;
import net.minecraft.class_2621;
import net.minecraft.class_8934;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 moved loot generation off LootableContainerBlockEntity and onto the
 * LootableInventory interface (class_8934.method_54873). Same contract, same
 * argument, different home -- so the old call site works again by delegating
 * through the interface the block entity now implements.
 */
@Mixin(class_2621.class)
public abstract class LootableContainerBridge {

    /** 1.20.1 checkLootInteraction(PlayerEntity): now LootableInventory.generateLoot. */
    public void method_11289(class_1657 player) {
        ((class_8934) (Object) this).method_54873(player);
    }
}
