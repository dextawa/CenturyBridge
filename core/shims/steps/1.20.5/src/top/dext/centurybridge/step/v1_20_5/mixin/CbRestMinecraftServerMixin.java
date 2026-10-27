package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;
import top.dext.centurybridge.step.v1_20_5.rt.CbLootData;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * 1.20.4 MinecraftServer.getLootData() (its resources' loot data manager), which 1.20.5 replaced by the
 * reloadable registries: back as the server's loot data viewed as a 1.20.4 manager (CbLootData.server),
 * so 1.20.4 calls, a mod override and its super call all bind to it.
 * <p>
 * 1.20.4 logTickTime(nanos): a protected hook, empty on MinecraftServer, that tickServer called at its end
 * with the tick's duration (1.20.4's integrated server fed the debug chart from it). 1.20.5 logs through
 * its tick time logger instead. The hook comes back with its 1.20.4 body and is called where 1.20.5's
 * tickServer logs the tick method's time, with the same duration (CbRestMinecraftServerMixinHooks, default
 * priority) -- so a 1.20.4 server subclass's override runs every tick as it did.
 */
@Mixin(value = net.minecraft.server.MinecraftServer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestMinecraftServerMixin implements CbPrioritySplit.ServerTickTime {

    /** was getLootData() */
    public LootDataManager method_3857() {
        return CbLootData.server((net.minecraft.server.MinecraftServer) (Object) this);
    }

    /** was logTickTime(nanos): 1.20.4's body */
    protected void method_53620(long nanos) {
    }

    /** 1.20.4's logTickTime called virtually, for 1.20.5's tickServer (CbRestMinecraftServerMixinHooks) */
    @Override
    public void cb$v1_20_5$logTickTime(long nanos) {
        this.method_53620(nanos);
    }
}
