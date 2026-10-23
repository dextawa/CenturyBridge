package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_3218;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/**
 * MinecraftServer.isSpawningAnimals() / areNpcsEnabled() of 1.21.1 (covers): both true, 1.21.1's own
 * bodies, which a 1.21.1 subclass (and the dedicated server, CbRestDedicatedServerMixin) override.
 *
 * 1.21.1's updateMobSpawningFlags gave every level setSpawnSettings(isSpawningMonsters(),
 * isSpawningAnimals()); 1.21.2's passes only the first. After it runs, each level gets 1.21.1's call
 * (CbRestLevelMixin), so the friendly flag is the server's isSpawningAnimals again -- true unless a
 * 1.21.1 subclass or spawn-animals=false says otherwise, which leaves 1.21.2's behaviour unchanged.
 */
@Mixin(MinecraftServer.class)
public abstract class CbRestMinecraftServerMixin implements CbRestServer.SpawnFlags {

    @Shadow
    public abstract Iterable<class_3218> method_3738();

    @Shadow
    public abstract boolean method_3783();

    @Override
    public boolean method_3796() {
        return true;
    }

    @Override
    public boolean method_3736() {
        return true;
    }

    @Inject(method = "method_27729", at = @At("TAIL"))
    private void cb$legacySpawnSettings(CallbackInfo ci) {
        boolean monsters = this.method_3783();
        boolean animals = this.method_3796();
        for (class_3218 level : this.method_3738()) {
            ((CbRestServer.LevelSpawnSettings) level).method_8424(monsters, animals);
        }
    }
}
