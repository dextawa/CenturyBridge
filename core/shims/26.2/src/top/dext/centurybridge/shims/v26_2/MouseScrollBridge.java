package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/** 1.20.1 mouseScrolled(x, y, amount) over the four-argument form (see the intermediary trees). */
@Mixin(net.minecraft.client.gui.components.events.GuiEventListener.class)
public interface MouseScrollBridge {
    default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return ((net.minecraft.client.gui.components.events.GuiEventListener) this).mouseScrolled(mouseX, mouseY, 0.0D, amount);
    }
}
