package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbRealms;

/**
 * RealmsWorldSlotButton made with its 1.20.2 constructor ({@link CbRealms#cbSlotButtonBuilt}):
 * <ul>
 * <li>tick() (method_37007), 1.20.2's: the server pulled from the supplier and, when there is one, the
 *     button's state and message set from it -- 1.20.3's setServerData;</li>
 * <li>the action prompt goes to the 1.20.2 tooltip setter while the button is hovered and active, as
 *     1.20.2's drawSlotFrame did, instead of the button's own tooltip.</li>
 * </ul>
 * A button 1.20.3 code made has neither and keeps 1.20.3's behaviour.
 */
@Mixin(net.minecraft.class_4367.class)
public abstract class CbRealmsWorldSlotButtonMixin implements CbRealms.CbLegacySlotButton {

    @Unique
    private Supplier<net.minecraft.class_4877> cb$server;

    @Unique
    private Consumer<net.minecraft.class_2561> cb$tooltipSetter;

    @Unique
    private net.minecraft.class_2561 cb$prompt;

    @Override
    public void cb$legacy(Supplier<net.minecraft.class_4877> server, Consumer<net.minecraft.class_2561> tooltipSetter) {
        cb$server = server;
        cb$tooltipSetter = tooltipSetter;
    }

    public void method_37007() {
        if (cb$server == null) {
            return;
        }
        net.minecraft.class_4877 server = cb$server.get();
        if (server != null) {
            ((net.minecraft.class_4367) (Object) this).method_54571(server);
        }
    }

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
        if (cb$tooltipSetter == null) {
            tooltip.method_54384(hovered, focused, area);
        } else if (hovered && ((net.minecraft.class_339) (Object) this).field_22763 && cb$prompt != null) {
            cb$tooltipSetter.accept(cb$prompt);
        }
    }
}
