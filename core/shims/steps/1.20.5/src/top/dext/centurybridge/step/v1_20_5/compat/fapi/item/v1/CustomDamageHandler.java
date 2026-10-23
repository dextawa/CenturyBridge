package top.dext.centurybridge.step.v1_20_5.compat.fapi.item.v1;

import java.util.function.Consumer;

/**
 * Fabric API 0.97.3's (1.20.4) CustomDamageHandler: damage(stack, amount, entity, breakCallback) with the
 * break callback a consumer of the entity. 1.20.5's takes the equipment slot too and a plain Runnable, so a
 * 1.20.4 handler -- a lambda, a class, a call -- keeps this shape (classRenames), and
 * FabricItemSettings.customDamage hands it to 1.20.5's settings adapted ({@link #adapt}).
 */
@FunctionalInterface
public interface CustomDamageHandler {

    int damage(net.minecraft.class_1799 stack, int amount, net.minecraft.class_1309 entity, Consumer<net.minecraft.class_1309> breakCallback);

    /**
     * This handler as 1.20.5's: the slot is not 1.20.4's to see, and the break callback it is handed runs
     * 1.20.5's (which already knows the entity and slot) whichever entity it is called with -- 1.20.4's
     * ItemStack.damage only ever called it with the damaging entity.
     */
    static net.fabricmc.fabric.api.item.v1.CustomDamageHandler adapt(CustomDamageHandler handler) {
        if (handler == null) {
            return null;
        }
        return (stack, amount, entity, slot, breakCallback) -> handler.damage(stack, amount, entity, e -> breakCallback.run());
    }
}
