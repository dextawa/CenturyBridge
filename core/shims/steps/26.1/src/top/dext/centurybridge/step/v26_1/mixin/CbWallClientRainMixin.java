package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLevelDataClient;

/**
 * 1.21.11's handleGameEvent set the client level data's raining flag before the rain level: true on
 * START_RAINING (the first setRainLevel call), false on STOP_RAINING (the second). Injected at those calls,
 * on the main thread, after the packet's thread hand-off.
 */
@Mixin(ClientPacketListener.class)
public abstract class CbWallClientRainMixin {

    @Shadow
    private ClientLevel level;

    @Inject(method = "handleGameEvent", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/multiplayer/ClientLevel;setRainLevel(F)V", ordinal = 0))
    private void cb$startRaining(ClientboundGameEventPacket packet, CallbackInfo ci) {
        ((CbWallLevelDataClient.RainFlag) this.level.getLevelData()).cb$setRaining(true);
    }

    @Inject(method = "handleGameEvent", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/multiplayer/ClientLevel;setRainLevel(F)V", ordinal = 1))
    private void cb$stopRaining(ClientboundGameEventPacket packet, CallbackInfo ci) {
        ((CbWallLevelDataClient.RainFlag) this.level.getLevelData()).cb$setRaining(false);
    }
}
