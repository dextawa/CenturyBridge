package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_4904;
import org.spongepowered.asm.mixin.Mixin;

/**
 * RealmsObjectSelectionList.itemClicked: in 1.20.1 an empty method for a subclass to fill in and
 * call from its own mouse handling (the base never called it; 1.20.1's world template list called it
 * on itself from mouseClicked and kept the empty body); 1.20.2 dropped it. Put back as it was, so a
 * subclass's override and super call link.
 */
@Mixin(class_4904.class)
public abstract class CbNetRealmsSelectionListBridge {

    /** was itemClicked(int, int, double, double, int, int): nothing, as in 1.20.1 */
    public void method_25490(int a, int b, double c, double d, int e, int f) {
    }
}
