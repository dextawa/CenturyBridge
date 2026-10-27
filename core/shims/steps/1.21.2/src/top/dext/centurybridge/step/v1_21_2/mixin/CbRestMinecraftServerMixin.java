package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * MinecraftServer.isSpawningAnimals() / areNpcsEnabled() of 1.21.1 (covers): both true, 1.21.1's own
 * bodies, which a 1.21.1 subclass (and the dedicated server, CbRestDedicatedServerMixin) override.
 *
 * 1.21.1's updateMobSpawningFlags gave every level setSpawnSettings(isSpawningMonsters(),
 * isSpawningAnimals()); 1.21.2's passes only the first. After it runs, each level gets 1.21.1's call
 * (CbRestLevelMixin), so the friendly flag is the server's isSpawningAnimals again -- true unless a
 * 1.21.1 subclass or spawn-animals=false says otherwise, which leaves 1.21.2's behaviour unchanged
 * (CbRestMinecraftServerMixinHooks, default priority).
 */
@Mixin(value = MinecraftServer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestMinecraftServerMixin implements CbRestServer.SpawnFlags, CbPrioritySplit.ServerSpawn {

    @Override
    public boolean method_3796() {
        return true;
    }

    @Override
    public boolean method_3736() {
        return true;
    }

    /** 1.21.1's isSpawningAnimals called virtually, for 1.21.2's updateMobSpawningFlags (CbRestMinecraftServerMixinHooks) */
    @Override
    public boolean cb$v1_21_2$isSpawningAnimals() {
        return this.method_3796();
    }
}
