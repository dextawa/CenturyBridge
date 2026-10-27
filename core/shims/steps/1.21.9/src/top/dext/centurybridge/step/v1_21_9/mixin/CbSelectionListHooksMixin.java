package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_332;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbSelectionLists;

/**
 * The injections into 1.21.9's AbstractSelectionList that go with CbSelectionListRestoredMixin's restored 1.21.8
 * members. The header, where a 1.21.8 list has one (its six-argument constructor, CbSelectionLists.cbHeader): 1.21.8
 * put the rows headerHeight lower, counted it in the content height (getMaxPosition) and drew the header
 * (renderHeader(graphics, getRowLeft(), getY() + 4 - scroll)) before the rows; here the first row's top
 * (getFirstEntryY) and the content height move by headerHeight and renderHeader is called right before 1.21.9's
 * renderListItems. renderDecorations is called at the end of 1.21.9's renderWidget, where 1.21.8 called it last
 * (1.21.8's body is empty: only a 1.21.8 subclass override draws). A list without a header draws exactly as 1.21.9;
 * every injection only adds.
 */
@Mixin(class_350.class)
public abstract class CbSelectionListHooksMixin {


    @Inject(method = "method_73376()I", at = @At("RETURN"), cancellable = true)
    private void cb$headerFirstRow(CallbackInfoReturnable<Integer> cir) {
        if (((CbSelectionLists.LegacyHeader) this).cb$hasHeader()) {
            cir.setReturnValue(cir.getReturnValueI() + ((CbSelectionLists.LegacyHeader) this).cb$headerHeight());
        }
    }

    @Inject(method = "method_44395()I", at = @At("RETURN"), cancellable = true)
    private void cb$headerContentHeight(CallbackInfoReturnable<Integer> cir) {
        if (((CbSelectionLists.LegacyHeader) this).cb$hasHeader()) {
            cir.setReturnValue(cir.getReturnValueI() + ((CbSelectionLists.LegacyHeader) this).cb$headerHeight());
        }
    }

    @Inject(method = "method_48579(Lnet/minecraft/class_332;IIF)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_350;method_25311(Lnet/minecraft/class_332;IIF)V"))
    private void cb$headerHook(class_332 graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (((CbSelectionLists.LegacyHeader) this).cb$hasHeader()) {
            class_350<?> self = (class_350<?>) (Object) this;
            ((CbSelectionLists.LegacyHeader) this).cb$renderHeader(graphics, self.method_25342() + 2, self.method_46427() + 4 - (int) self.method_44387());
        }
    }

    @Inject(method = "method_48579(Lnet/minecraft/class_332;IIF)V", at = @At("TAIL"))
    private void cb$decorationsHook(class_332 graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ((CbSelectionLists.LegacyHeader) this).cb$renderDecorations(graphics, mouseX, mouseY);
    }
}
