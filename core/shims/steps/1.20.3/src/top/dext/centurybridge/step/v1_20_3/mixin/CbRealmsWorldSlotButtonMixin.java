package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_3.rt.CbRealms;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * RealmsWorldSlotButton made with its 1.20.2 constructor ({@link CbRealms#cbSlotButtonBuilt}):
 * <ul>
 * <li>tick() (method_37007), 1.20.2's: the server pulled from the supplier and, when there is one, the
 *     button's state and message set from it -- 1.20.3's setServerData;</li>
 * <li>the action prompt goes to the 1.20.2 tooltip setter while the button is hovered and active, as
 *     1.20.2's drawSlotFrame did, instead of the button's own tooltip (CbRealmsWorldSlotButtonMixinHooks,
 *     default priority).</li>
 * </ul>
 * A button 1.20.3 code made has neither and keeps 1.20.3's behaviour.
 */
@Mixin(value = net.minecraft.class_4367.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRealmsWorldSlotButtonMixin implements CbRealms.CbLegacySlotButton, CbPrioritySplit.SlotButtonTooltip {

    @Unique
    private Supplier<net.minecraft.class_4877> cb$server;

    @Unique
    private Consumer<net.minecraft.class_2561> cb$tooltipSetter;

    @Override
    public void cb$legacy(Supplier<net.minecraft.class_4877> server, Consumer<net.minecraft.class_2561> tooltipSetter) {
        cb$server = server;
        cb$tooltipSetter = tooltipSetter;
    }

    @Override
    public Consumer<net.minecraft.class_2561> cb$v1_20_3$tooltipSetter() {
        return cb$tooltipSetter;
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
}
