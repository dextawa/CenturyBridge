package cb.s11.mixin;

import cb.s11.Driver;
import net.minecraft.class_1928;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** a 1.21.10 mod's mixin on GameRules.BooleanValue.set: counts boolean rule writes (vanilla's included on 1.21.10) */
@Mixin(class_1928.class_4310.class)
public abstract class RuleSetMixin {
    @Inject(method = "method_20758(ZLnet/minecraft/server/MinecraftServer;)V", at = @At("HEAD"), remap = false)
    private void cbs11$countSet(boolean value, MinecraftServer server, CallbackInfo ci) {
        Driver.boolSets++;
    }
}
