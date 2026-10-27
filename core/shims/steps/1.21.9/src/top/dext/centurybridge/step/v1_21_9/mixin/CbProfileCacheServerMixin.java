package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_11561;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Where 1.21.8's MinecraftServer set and cleared its user cache's executor (the restored GameProfileCache.getAsync
 * completes on it, CbProfileCacheRestoredMixin): set to the server at the end of its constructor, when its Services
 * hold the vanilla cache (1.21.8: when profileCache() was non-null); cleared once stopServer has run (1.21.8 cleared it
 * right after each stopServer call in runServer's exits). Nothing 1.21.9 does is changed.
 */
@Mixin(MinecraftServer.class)
public abstract class CbProfileCacheServerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$setCacheExecutor(CallbackInfo ci) {
        MinecraftServer self = (MinecraftServer) (Object) this;
        class_11561 cache = self.method_73550().comp_4407();
        if (cache instanceof net.minecraft.class_3312) {
            ((top.dext.centurybridge.step.v1_21_9.rt.CbProfileCacheExecutor) cache).method_37157(self);
        }
    }

    @Inject(method = "method_3782()V", at = @At("TAIL"))
    private void cb$clearCacheExecutor(CallbackInfo ci) {
        class_11561 cache = ((MinecraftServer) (Object) this).method_73550().comp_4407();
        if (cache instanceof net.minecraft.class_3312) {
            ((top.dext.centurybridge.step.v1_21_9.rt.CbProfileCacheExecutor) cache).method_39753();
        }
    }
}
