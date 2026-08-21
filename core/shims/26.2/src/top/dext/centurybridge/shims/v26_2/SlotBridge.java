package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Slot.setByPlayer gained an explicit previous-stack
 * parameter. Old single-arg overload delegates with the current stack as the
 * previous value (matching the old implicit semantics). Work order: L2 x18.
 */
@Mixin(net.minecraft.world.inventory.Slot.class)
public abstract class SlotBridge {

    public void setByPlayer(net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.inventory.Slot self = (net.minecraft.world.inventory.Slot) (Object) this;
        self.setByPlayer(stack, self.getItem());
    }
}
