package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1735;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: Slot.setByPlayer gained an explicit previous-stack
 * parameter. Old single-arg overload delegates with the current stack as the
 * previous value (matching the old implicit semantics). Work order: L2 x18.
 */
@Mixin(class_1735.class)
public abstract class SlotBridge {

    public void method_48931(class_1799 stack) {
        class_1735 self = (class_1735) (Object) this;
        self.method_48931(stack, self.method_7677());
    }
}
