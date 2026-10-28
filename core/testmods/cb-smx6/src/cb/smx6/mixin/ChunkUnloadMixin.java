package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.20.6 mod's injector in ChunkMap's unload body, the scheduleUnload lambda method_18843(ChunkHolder,
 * CompletableFuture, long, ChunkAccess), at its LevelChunk.setLoaded(false) call -- the shape of Kilt's
 * ChunkMapInject.callChunkUnloadEvent: it captures the lambda's arguments and reads the holder, the position and the
 * chunk, never the future (1.21: s121.json mixinTargets onto method_60440(ChunkHolder, long), the chunk from the
 * holder's latest chunk).
 */
@Mixin(targets = "net.minecraft.class_3898")
public abstract class ChunkUnloadMixin {

    @Inject(method = "method_18843", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2818;method_12226(Z)V"))
    private void cbsmx6$unloading(net.minecraft.class_3193 holder, java.util.concurrent.CompletableFuture<?> future, long pos,
                                  net.minecraft.class_2791 chunk, CallbackInfo ci) {
        Driver.unloadAsked++;
        if (pos == Driver.unloadWatch) {
            Driver.unloadSeen = (holder.method_13994().method_8324() == pos ? "holder" : "?")
                + "/" + (chunk instanceof net.minecraft.class_2818 && chunk.method_12004().method_8324() == pos ? "chunk" : String.valueOf(chunk));
        }
    }
}
