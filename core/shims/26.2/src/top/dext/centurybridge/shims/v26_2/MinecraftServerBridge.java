package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v26_2.Trackers;

/**
 * Remembers the running server so bridges that need the world's dynamic
 * registries (enchantments are data-driven since 1.21 and have no static
 * registry) can reach them from 1.20.1-shaped static calls that carry no
 * level argument.
 */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class MinecraftServerBridge {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$track(CallbackInfo ci) {
        Trackers.SERVER = (net.minecraft.server.MinecraftServer) (Object) this;
    }
}
