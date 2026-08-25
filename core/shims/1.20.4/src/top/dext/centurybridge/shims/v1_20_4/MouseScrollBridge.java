package top.dext.centurybridge.shims.v1_20_4;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.2 added a horizontal amount to Element.mouseScrolled. A 1.20.1
 * override's super.mouseScrolled(x, y, amount) resolves to this default on
 * every Element (Screen, widgets, lists); the converter's overrideHooks
 * synthesize the four-argument entry vanilla calls.
 */
@Mixin(net.minecraft.class_364.class)
public interface MouseScrollBridge {
    default boolean method_25401(double mouseX, double mouseY, double amount) {
        return ((net.minecraft.class_364) this).method_25401(mouseX, mouseY, 0.0D, amount);
    }
}
