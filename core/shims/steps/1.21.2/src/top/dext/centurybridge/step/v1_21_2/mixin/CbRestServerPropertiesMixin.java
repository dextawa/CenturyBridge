package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Properties;
import net.minecraft.class_3806;
import net.minecraft.class_3808;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestServer;

/**
 * DedicatedServerProperties.spawnAnimals / spawnNpcs of 1.21.1: the "spawn-animals" and "spawn-npcs"
 * entries of server.properties, read (default true) and written back the way Settings.get(key, boolean)
 * reads every property, so the file keeps them as it did. 1.21.2 dropped both.
 */
@Mixin(class_3806.class)
public abstract class CbRestServerPropertiesMixin extends class_3808<class_3806> implements CbRestServer.DedicatedFlags {

    @Unique
    private boolean cb$spawnAnimals;

    @Unique
    private boolean cb$spawnNpcs;

    private CbRestServerPropertiesMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$readSpawnFlags(Properties properties, CallbackInfo ci) {
        this.cb$spawnAnimals = this.method_16740("spawn-animals", true);
        this.cb$spawnNpcs = this.method_16740("spawn-npcs", true);
    }

    @Override
    public boolean cb$spawnAnimals() {
        return this.cb$spawnAnimals;
    }

    @Override
    public boolean cb$spawnNpcs() {
        return this.cb$spawnNpcs;
    }
}
