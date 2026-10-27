package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_1928;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbLegacyServer;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10's MinecraftServer.getGameRules() restored on 1.21.11's MinecraftServer (the overworld's rules, 1.21.10's body;
 * a converted mod calls it and a mod's server subclass overrides it). Restored members only (CbVanillaReads.PRIORITY);
 * 1.21.11's game rule hook is CbGameRulesServerMixinHooks.
 */
@Mixin(value = MinecraftServer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbGameRulesServerMixin implements CbLegacyServer {
    /** 1.21.10 MinecraftServer.getGameRules(): this.overworld().getGameRules() */
    @Override
    public class_1928 method_3767() {
        return CbGameRules.serverRules((MinecraftServer) (Object) this);
    }
}
