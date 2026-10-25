package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1720;
import net.minecraft.class_3913;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.20.1's AbstractFurnaceMenu.getBurnProgress() / getLitProgress() back (s1misc), 1.20.1 bodies:
 * the progress in pixels of the screen's arrow (0..24) and flame (0..13). 1.20.2 answers fractions
 * (0..1) under the same names; a converted menu overriding the pixel versions answers 1.20.2's
 * through CbMenus.
 */
@Mixin(value = class_1720.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbFurnaceMenuLegacy {

    @Shadow
    @Final
    private class_3913 field_17286;

    public int method_17363() {
        int progress = this.field_17286.method_17390(2);
        int total = this.field_17286.method_17390(3);
        return total != 0 && progress != 0 ? progress * 24 / total : 0;
    }

    public int method_17364() {
        int duration = this.field_17286.method_17390(1);
        if (duration == 0) {
            duration = 200;
        }
        return this.field_17286.method_17390(0) * 13 / duration;
    }
}
