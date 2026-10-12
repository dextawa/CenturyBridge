package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ConfirmLinkScreen.confirmMessage(trusted, url) (protected static), the text 1.20.2's 5-argument constructor built */
@Mixin(net.minecraft.class_407.class)
public interface CbConfirmLinkScreenAccess {

    @Invoker("method_44658")
    static net.minecraft.class_5250 cb$confirmMessage(boolean trusted, String url) {
        throw new AssertionError("mixin invoker");
    }
}
