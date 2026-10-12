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

/** MenuTooltipPositioner: as {@link CbBelowOrAbovePositionerMixin}, the widget followed again */
@Mixin(net.minecraft.class_8092.class)
public abstract class CbMenuPositionerMixin implements CbGui.CbWidgetTooltipArea {

    @Shadow
    @Final
    @Mutable
    private net.minecraft.class_8030 field_46723;

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
            field_46723 = cb$widget.method_48202();
        }
    }
}
