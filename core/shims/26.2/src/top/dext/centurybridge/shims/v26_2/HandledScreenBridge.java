package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * net.minecraft.client.gui.components.EditBox (HandledScreen): background rendering helpers changed signature at 1.20.2.
 *
 * moveCursorToStart(): no-arg fill -> (boolean): pass false (= not redraw from shader)
 * moveCursorToEnd(): no-arg blur -> (boolean): pass false
 * moveCursorTo(int): set slot -> (int, boolean): second param = whether to animate; pass false
 */
@Mixin(net.minecraft.client.gui.components.EditBox.class)
public abstract class HandledScreenBridge {

    public void moveCursorToStart() {
        ((net.minecraft.client.gui.components.EditBox) (Object) this).moveCursorToStart(false);
    }

    public void moveCursorToEnd() {
        ((net.minecraft.client.gui.components.EditBox) (Object) this).moveCursorToEnd(false);
    }

    public void moveCursorTo(int slotIndex) {
        ((net.minecraft.client.gui.components.EditBox) (Object) this).moveCursorTo(slotIndex, false);
    }
}
