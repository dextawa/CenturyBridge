package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * OptionsList.addSmall(left, right): 1.20.4's took two options and made their widgets as a half-width row (the
 * right one may be null); 1.20.5's addSmall(left, right) takes two widgets, and its addSmall(OptionInstance...)
 * makes option rows pairwise (a lone last option gets a null partner), so the pair is that call. 1.20.4's
 * addBig(option) is rt/CbClientGui.method_20406.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_353.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientOptionsListMixin {

    @Shadow
    public abstract void method_20408(net.minecraft.class_7172<?>... options);

    /** 1.20.4 OptionsList.addSmall(left, right): the half-width row of the two options' widgets (right may be null) */
    public void method_20407(net.minecraft.class_7172<?> left, net.minecraft.class_7172<?> right) {
        if (right == null) {
            this.method_20408(left);
        } else {
            this.method_20408(left, right);
        }
    }
}
