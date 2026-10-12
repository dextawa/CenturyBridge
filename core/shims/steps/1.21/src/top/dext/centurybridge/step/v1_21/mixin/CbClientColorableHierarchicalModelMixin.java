package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbClientModels;

/**
 * ColorableHierarchicalModel kept 1.20.6's setColor(r, g, b) as three floats and multiplied the floats renderToBuffer got by
 * them; 1.21 keeps an ARGB int and multiplies ints. A 1.20.6 setColor keeps its floats here (and the int,
 * for 1.21's readers); while they are set, renderToBuffer multiplies in floats as 1.20.6 did. 1.21's own
 * setColor(int) clears them.
 */
@Mixin(net.minecraft.class_4594.class)
public abstract class CbClientColorableHierarchicalModelMixin implements CbClientModels.CbLegacyColorable {

    @Shadow
    private int field_52152;

    @Unique
    private boolean cb$legacy;

    @Unique
    private float cb$r;

    @Unique
    private float cb$g;

    @Unique
    private float cb$b;

    @Override
    public void cb$setLegacyColor(float r, float g, float b) {
        this.field_52152 = CbClientModels.cbOpaque(r, g, b);
        this.cb$r = r;
        this.cb$g = g;
        this.cb$b = b;
        this.cb$legacy = true;
    }

    @Inject(method = "method_22956(I)V", at = @At("HEAD"))
    private void cb$intColor(int color, CallbackInfo ci) {
        this.cb$legacy = false;
    }

    @Redirect(method = "method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5253$class_5254;method_27763(II)I"))
    private int cb$tint(int passed, int kept) {
        return this.cb$legacy ? CbClientModels.cbLegacyTint(passed, this.cb$r, this.cb$g, this.cb$b)
            : net.minecraft.class_5253.class_5254.method_27763(passed, kept);
    }
}
