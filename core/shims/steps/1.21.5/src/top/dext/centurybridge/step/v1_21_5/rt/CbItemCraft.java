package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;

/**
 * 1.21.4's Item.onCraftedPostProcess(stack, level, player) is onCraftedPostProcess(stack, player) since 1.21.5, which
 * passes the player's level on to onCraftedBy where 1.21.4 passed the level it was given -- every 1.21.4 caller
 * (ItemStack.onCraftedBy, from the result slots) handed it the crafting player's level. A 1.21.4 override gets the new
 * shape (overrideHooks, bridge {@link #craftedPostProcess}) with that level; its super call and a 1.21.4 call drop the
 * level (callReshapes), as 1.21.5's body takes it from the player.
 */
public final class CbItemCraft {

    private CbItemCraft() {
    }

    /** the 1.21.4 shape a converted Item subclass overrides (adapter of the override hook) */
    public interface LegacyCraftedPostProcess {
        void method_54465(class_1799 stack, class_1937 level, class_1657 player);
    }

    /** 1.21.5's onCraftedPostProcess of a 1.21.4 override: the player's level, what every 1.21.4 caller passed */
    public static void craftedPostProcess(LegacyCraftedPostProcess self, class_1799 stack, class_1657 player) {
        self.method_54465(stack, player.method_37908(), player);
    }
}
