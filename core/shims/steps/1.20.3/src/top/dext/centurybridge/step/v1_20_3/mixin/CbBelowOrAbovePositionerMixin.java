package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * BelowOrAboveWidgetTooltipPositioner took the widget in 1.20.2 and read its position each time it placed
 * a tooltip; 1.20.3 takes a rectangle once. One made with a 1.20.2 constructor follows its widget again.
 */
@Mixin(net.minecraft.class_7999.class)
public abstract class CbBelowOrAbovePositionerMixin implements CbGui.CbWidgetTooltipArea {

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_8030 field_46722;

    @Unique
    private net.minecraft.class_339 cb$widget;

    @Override
    public void cb$follow(net.minecraft.class_339 widget) {
        cb$widget = widget;
    }

    @Inject(method = "method_47944", at = @At("HEAD"))
    private void cb$widgetArea(int screenWidth, int screenHeight, int mouseX, int mouseY, int tooltipWidth, int tooltipHeight,
                               CallbackInfoReturnable<org.joml.Vector2ic> cir) {
        if (cb$widget != null) {
            field_46722 = cb$widget.method_48202();
        }
    }
}
