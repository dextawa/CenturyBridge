package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_315;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbGraphicsMode;

/**
 * OptionInstance (a final class: a view cannot override get / set): the 1.21.10 graphicsMode view (rt/CbGraphicsMode)
 * answers its live value at get()'s return and passes set()'s value on to improvedTransparency at set()'s return. The
 * marker read is each handler's first instruction: every other instance pays one field read. set's own body
 * (validation, stored value, the view's callback) runs as for any option, and native 1.21.11 mixins on get / set run
 * for the view and for every other option. Default priority: it merges no member (not a restorer), and the priority gate
 * keeps injectors into W's own methods out of CbVanillaReads.PRIORITY.
 */
@Mixin(class_7172.class)
public abstract class CbOptionInstanceGraphicsMixin<T> implements CbGraphicsMode.View {
    @Shadow
    private T field_37868;

    @Unique
    private class_315 cb$graphicsOptions;

    @Override
    public void cb$v1_21_11$graphicsView(class_315 options) {
        this.cb$graphicsOptions = options;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cb$v1_21_11$store(Object value) {
        this.field_37868 = (T) value;
    }

    @Inject(method = "method_41753", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("unchecked")
    private void cb$graphicsGet(CallbackInfoReturnable<T> cir) {
        class_315 options = this.cb$graphicsOptions;
        if (options == null) {
            return;
        }
        cir.setReturnValue((T) CbGraphicsMode.get(options));
    }

    @Inject(method = "method_41748", at = @At("RETURN"))
    private void cb$graphicsSet(T value, CallbackInfo ci) {
        class_315 options = this.cb$graphicsOptions;
        if (options == null) {
            return;
        }
        CbGraphicsMode.set(options, this.field_37868);
    }
}
