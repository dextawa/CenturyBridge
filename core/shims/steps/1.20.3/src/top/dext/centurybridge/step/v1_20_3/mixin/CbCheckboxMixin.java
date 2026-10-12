package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * A checkbox made with a 1.20.2 constructor (x, y, width, height, message, selected[, showLabel]) is
 * drawn the way 1.20.2 drew it: the box 20 wide and as tall as the widget, the label (when shown) at
 * x + 24, centred on the height. 1.20.3 sizes the box from the font and always shows the label; a
 * checkbox 1.20.3 code made is left to that.
 */
@Mixin(net.minecraft.class_4286.class)
public abstract class CbCheckboxMixin extends net.minecraft.class_4264 implements CbGui.CbLegacyCheckbox {

    @Shadow
    @Final
    private static net.minecraft.class_2960 field_45348;

    @Shadow
    @Final
    private static net.minecraft.class_2960 field_45349;

    @Shadow
    @Final
    private static net.minecraft.class_2960 field_45350;

    @Shadow
    @Final
    private static net.minecraft.class_2960 field_45351;

    /** selected */
    @Shadow
    private boolean field_19230;

    @Unique
    private boolean cb$legacy;

    @Unique
    private boolean cb$showLabel;

    private CbCheckboxMixin() {
        super(0, 0, 0, 0, null);
    }

    @Override
    public void cb$setLegacy(boolean showLabel) {
        cb$legacy = true;
        cb$showLabel = showLabel;
    }

    @Inject(method = "method_48579", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(net.minecraft.class_332 graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!cb$legacy) {
            return;
        }
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        RenderSystem.enableDepthTest();
        net.minecraft.class_327 font = mc.field_1772;
        graphics.method_51422(1.0F, 1.0F, 1.0F, this.field_22765);
        RenderSystem.enableBlend();
        net.minecraft.class_2960 sprite = field_19230
            ? (this.method_25370() ? field_45348 : field_45349)
            : (this.method_25370() ? field_45350 : field_45351);
        graphics.method_52706(sprite, this.method_46426(), this.method_46427(), 20, this.field_22759);
        graphics.method_51422(1.0F, 1.0F, 1.0F, 1.0F);
        if (cb$showLabel) {
            graphics.method_27535(font, this.method_25369(), this.method_46426() + 24,
                this.method_46427() + (this.field_22759 - 8) / 2,
                14737632 | net.minecraft.class_3532.method_15386(this.field_22765 * 255.0F) << 24);
        }
        ci.cancel();
    }
}
