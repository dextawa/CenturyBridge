package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbGuiScroll;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.4's own AbstractTextAreaWidget.mouseClicked that goes with {@link CbGuiScrollTextAreaMixin}
 * (its restored 1.21.3 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.3 injectors can anchor
 * inside them): a widget with its own withinContentAreaPoint decides its clicks as 1.21.3's mouseClicked did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10415.class)
public abstract class CbGuiScrollTextAreaMixinHooks extends net.minecraft.class_7528 {

    protected CbGuiScrollTextAreaMixinHooks() {
        super(0, 0, 0, 0, null);
    }

    /**
     * 1.21.3 mouseClicked, for a widget with its own withinContentAreaPoint: a press on the scroll bar starts the
     * drag; the press is taken when it is on the bar or inside the content area as the widget defines it.
     */
    @Inject(method = "method_25402", at = @At("HEAD"), cancellable = true)
    private void cb$legacyContentAreaClick(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof CbGuiScroll.CbLegacyContentAreaPoint)) {
            return;
        }
        if (!this.field_22764) {
            cir.setReturnValue(false);
            return;
        }
        boolean within = ((CbPrioritySplitClient.TextAreaContentPoint) this).cb$v1_21_4$contentAreaPoint(x, y);
        boolean dragging = this.method_65505(x, y, button);
        boolean onBar = this.method_44392() && x >= (double) this.method_65507() && x <= (double) (this.method_65507() + net.minecraft.class_7528.field_55258)
            && y >= (double) this.method_46427() && y < (double) this.method_55443();
        cir.setReturnValue(dragging || within || onBar);
    }
}
