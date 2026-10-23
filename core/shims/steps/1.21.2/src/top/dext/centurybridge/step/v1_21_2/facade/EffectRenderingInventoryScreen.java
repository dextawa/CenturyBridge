package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * EffectRenderingInventoryScreen (class_485) of 1.21.1: a container screen that drew the player's active
 * effects beside itself. 1.21.2 made class_485 the EffectsInInventory helper a screen holds (InventoryScreen
 * and CreativeModeInventoryScreen render it after their own render). This is 1.21.1's screen as 1.21.2's
 * screens are built: an EffectsInInventory of this screen, rendered after the container screen's render, and
 * asked whether the effects can be seen.
 */
@Environment(EnvType.CLIENT)
public abstract class EffectRenderingInventoryScreen<T extends net.minecraft.class_1703> extends net.minecraft.class_465<T> {

    private final net.minecraft.class_485 effects;

    public EffectRenderingInventoryScreen(T menu, net.minecraft.class_1661 inventory, net.minecraft.class_2561 title) {
        super(menu, inventory, title);
        this.effects = new net.minecraft.class_485(this);
    }

    @Override
    public void method_25394(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick) {
        super.method_25394(graphics, mouseX, mouseY, partialTick);
        this.effects.method_64514(graphics, mouseX, mouseY, partialTick);
    }

    /** canSeeEffects() */
    public boolean method_38934() {
        return this.effects.method_38934();
    }
}
