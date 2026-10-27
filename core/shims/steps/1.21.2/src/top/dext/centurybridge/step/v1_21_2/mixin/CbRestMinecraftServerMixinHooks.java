package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_3218;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/**
 * The injector into 1.21.2's own MinecraftServer.updateMobSpawningFlags that goes with {@link CbRestMinecraftServerMixin}
 * (its restored 1.21.1 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.1 injectors can anchor
 * inside them): each level gets 1.21.1's setSpawnSettings(isSpawningMonsters(), isSpawningAnimals()) after it runs.
 */
@Mixin(MinecraftServer.class)
public abstract class CbRestMinecraftServerMixinHooks {

    @Shadow
    public abstract Iterable<class_3218> method_3738();

    @Shadow
    public abstract boolean method_3783();

    @Inject(method = "method_27729", at = @At("TAIL"))
    private void cb$legacySpawnSettings(CallbackInfo ci) {
        boolean monsters = this.method_3783();
        boolean animals = ((CbPrioritySplit.ServerSpawn) (Object) this).cb$v1_21_2$isSpawningAnimals();
        for (class_3218 level : this.method_3738()) {
            ((CbRestServer.LevelSpawnSettings) level).method_8424(monsters, animals);
        }
    }
}
