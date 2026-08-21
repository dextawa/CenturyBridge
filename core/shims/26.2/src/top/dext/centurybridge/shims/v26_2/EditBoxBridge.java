package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: widget ticking was removed; EditBox.tick() no longer
 * exists. A no-op preserves old callers (the cursor-blink state it used to
 * advance is now time-derived). Client only. Work order: L3 x18.
 */
@Mixin(net.minecraft.client.gui.components.EditBox.class)
public abstract class EditBoxBridge {

    public void method_1865() {
    }
}
