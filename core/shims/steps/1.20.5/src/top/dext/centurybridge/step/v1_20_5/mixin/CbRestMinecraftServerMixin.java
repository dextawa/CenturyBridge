package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;
import top.dext.centurybridge.step.v1_20_5.rt.CbLootData;

/**
 * 1.20.4 MinecraftServer.getLootData() (its resources' loot data manager), which 1.20.5 replaced by the
 * reloadable registries: back as the server's loot data viewed as a 1.20.4 manager (CbLootData.server),
 * so 1.20.4 calls, a mod override and its super call all bind to it.
 * <p>
 * 1.20.4 logTickTime(nanos): a protected hook, empty on MinecraftServer, that tickServer called at its end
 * with the tick's duration (1.20.4's integrated server fed the debug chart from it). 1.20.5 logs through
 * its tick time logger instead. The hook comes back with its 1.20.4 body and is called where 1.20.5's
 * tickServer logs the tick method's time, with the same duration -- so a 1.20.4 server subclass's override
 * runs every tick as it did.
 */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbRestMinecraftServerMixin {

    /** was getLootData() */
    public LootDataManager method_3857() {
        return CbLootData.server((net.minecraft.server.MinecraftServer) (Object) this);
    }

    /** was logTickTime(nanos): 1.20.4's body */
    protected void method_53620(long nanos) {
    }

    @Inject(method = "method_56602", at = @At("HEAD"))
    private void cbRest$logTickTime(long tickStart, CallbackInfo ci) {
        this.method_53620(net.minecraft.class_156.method_648() - tickStart);
    }
}
