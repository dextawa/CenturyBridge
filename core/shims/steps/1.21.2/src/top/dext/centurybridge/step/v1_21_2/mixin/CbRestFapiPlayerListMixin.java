package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import net.minecraft.class_8792;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestFapiEvents;

/**
 * ServerPlayerEvents.JOIN / LEAVE of 1.21.1's Fabric API (0.116), fired where 0.116's PlayerManagerMixin fired
 * them: JOIN when PlayerList.placeNewPlayer returns, LEAVE at the start of PlayerList.remove.
 */
@Mixin(class_3324.class)
public abstract class CbRestFapiPlayerListMixin {

    @Inject(method = "method_14570", at = @At("RETURN"))
    private void cb$join(class_2535 connection, class_3222 player, class_8792 cookie, CallbackInfo ci) {
        CbRestFapiEvents.JOIN.invoker().onJoin(player);
    }

    @Inject(method = "method_14611", at = @At("HEAD"))
    private void cb$leave(class_3222 player, CallbackInfo ci) {
        CbRestFapiEvents.LEAVE.invoker().onLeave(player);
    }
}
