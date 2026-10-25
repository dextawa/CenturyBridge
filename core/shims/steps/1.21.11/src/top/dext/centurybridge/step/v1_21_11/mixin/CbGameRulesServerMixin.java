package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_12279;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.11's game rule hook (MinecraftServer.onGameRuleChanged(rule, value), run by every GameRules.set with a server:
 * the game rule command, the edit screen's apply, assignFrom, a 1.21.10 value's set) runs a 1.21.10 mod rule's own
 * change callback, as 1.21.10's Value.onChanged did through the rule's Type.
 */
@Mixin(MinecraftServer.class)
public abstract class CbGameRulesServerMixin {
    @Inject(method = "method_74058", at = @At("TAIL"))
    private <T> void cb$legacyRuleCallback(class_12279<T> rule, T value, CallbackInfo ci) {
        CbGameRules.onRuleChanged((MinecraftServer) (Object) this, rule);
    }
}
