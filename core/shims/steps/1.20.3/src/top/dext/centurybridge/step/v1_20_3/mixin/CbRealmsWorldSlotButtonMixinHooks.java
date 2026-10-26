package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.function.Consumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;

/**
 * The injectors into 1.20.3's own RealmsWorldSlotButton methods that go with {@link CbRealmsWorldSlotButtonMixin} (its
 * restored 1.20.2 member stays there, at {@code CbVanillaReads.PRIORITY}): the action prompt goes to the 1.20.2 tooltip
 * setter while the button is hovered and active, as 1.20.2's drawSlotFrame did.
 */
@Mixin(net.minecraft.class_4367.class)
public abstract class CbRealmsWorldSlotButtonMixinHooks {

    @Unique
    private net.minecraft.class_2561 cb$prompt;

    @Inject(method = "method_54572", at = @At("HEAD"))
    private void cb$clearPrompt(net.minecraft.class_4367.class_4370 state, String name, CallbackInfo ci) {
        cb$prompt = null;
    }

    @ModifyArg(method = "method_54572", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_7919;method_47407(Lnet/minecraft/class_2561;)Lnet/minecraft/class_7919;"))
    private net.minecraft.class_2561 cb$recordPrompt(net.minecraft.class_2561 prompt) {
        cb$prompt = prompt;
        return prompt;
    }

    @Redirect(method = "method_48579", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_7919;method_54384(ZZLnet/minecraft/class_8030;)V"))
    private void cb$tooltip(net.minecraft.class_7919 tooltip, boolean hovered, boolean focused, net.minecraft.class_8030 area) {
        Consumer<net.minecraft.class_2561> tooltipSetter = ((CbPrioritySplit.SlotButtonTooltip) (Object) this).cb$v1_20_3$tooltipSetter();
        if (tooltipSetter == null) {
            tooltip.method_54384(hovered, focused, area);
        } else if (hovered && ((net.minecraft.class_339) (Object) this).field_22763 && cb$prompt != null) {
            tooltipSetter.accept(cb$prompt);
        }
    }
}
