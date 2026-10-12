package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyPlayerSpawn;

/**
 * Player spawns for a 1.20.1 play listener that implements onPlayerSpawn (CbNetLegacyPlayerSpawn: a
 * ClientPlayNetworkHandler subclass or a ClientPlayPacketListener implementation declaring it). 1.20.1's
 * server spawned players with PlayerSpawnS2CPacket, which went to onPlayerSpawn, and every other entity
 * with EntitySpawnS2CPacket, which went to onEntitySpawn; 1.20.2 sends players as entity spawns of type
 * PLAYER. Such a packet is handed to the listener's onPlayerSpawn, as 1.20.1's own packet was -- also
 * when the listener declares onEntitySpawn itself, which 1.20.1 never handed a player.
 */
@Mixin(net.minecraft.class_2604.class)
public abstract class CbNetLegacyPlayerSpawnRoute {

    @Inject(method = "method_11178", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPlayerSpawn(net.minecraft.class_2602 listener, CallbackInfo ci) {
        net.minecraft.class_2604 self = (net.minecraft.class_2604) (Object) this;
        if (listener instanceof CbNetLegacyPlayerSpawn legacy && self.method_11169() == net.minecraft.class_1299.field_6097) {
            legacy.method_11097(self);
            ci.cancel();
        }
    }
}
