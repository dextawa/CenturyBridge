package top.dext.centurybridge.shims.v1_20_4;

import net.minecraft.class_329;
import net.minecraft.class_342;
import org.spongepowered.asm.mixin.Mixin;

/**
 * class_342 (HandledScreen): background rendering helpers changed signature at 1.20.2.
 *
 * method_1870(): no-arg fill -> (boolean): pass false (= not redraw from shader)
 * method_1872(): no-arg blur -> (boolean): pass false
 * method_1883(int): set slot -> (int, boolean): second param = whether to animate; pass false
 */
@Mixin(class_342.class)
public abstract class HandledScreenBridge {

    public void method_1870() {
        ((class_342) (Object) this).method_1870(false);
    }

    public void method_1872() {
        ((class_342) (Object) this).method_1872(false);
    }

    public void method_1883(int slotIndex) {
        ((class_342) (Object) this).method_1883(slotIndex, false);
    }
}
