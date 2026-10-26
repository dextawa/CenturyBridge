package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_3176;
import net.minecraft.class_3806;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * DedicatedServer.isSpawningAnimals() / areNpcsEnabled() of 1.21.1: the server.properties switches
 * spawn-animals / spawn-npcs (still read, CbRestServerPropertiesMixin) and super's answer, which is
 * MinecraftServer's constant true (CbRestMinecraftServerMixin) -- 1.21.1's body.
 */
@Mixin(value = class_3176.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestDedicatedServerMixin implements CbRestServer.SpawnFlags {

    @Shadow
    public abstract class_3806 method_16705();

    @Override
    public boolean method_3796() {
        return CbRestServer.spawnAnimals(this.method_16705());
    }

    @Override
    public boolean method_3736() {
        return CbRestServer.spawnNpcs(this.method_16705());
    }
}
