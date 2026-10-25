package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_12279;
import net.minecraft.class_1928;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbLegacyServer;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's MinecraftServer.getGameRules() restored on 1.21.11's MinecraftServer (the overworld's rules, 1.21.10's body;
 * a converted mod calls it and a mod's server subclass overrides it), and 1.21.11's game rule hook
 * (onGameRuleChanged(rule, value), run by every GameRules.set with a server: the game rule command, the edit screen's
 * apply, assignFrom, a 1.21.10 value's set) running a 1.21.10 mod rule's own change callback, as 1.21.10's
 * Value.onChanged did through the rule's Type.
 */
@Mixin(value = MinecraftServer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGameRulesServerMixin implements CbLegacyServer {
    /** 1.21.10 MinecraftServer.getGameRules(): this.overworld().getGameRules() */
    @Override
    public class_1928 method_3767() {
        return CbGameRules.serverRules((MinecraftServer) (Object) this);
    }

    @Inject(method = "method_74058", at = @At("TAIL"))
    private <T> void cb$legacyRuleCallback(class_12279<T> rule, T value, CallbackInfo ci) {
        CbGameRules.onRuleChanged((MinecraftServer) (Object) this, rule);
    }
}
