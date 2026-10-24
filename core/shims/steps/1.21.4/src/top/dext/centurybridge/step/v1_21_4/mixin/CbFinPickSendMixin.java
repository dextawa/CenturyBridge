package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.facade.ServerboundPickItemPacket;
import top.dext.centurybridge.step.v1_21_4.rt.CbClientRest;

/**
 * A 1.21.3 mod's ServerboundPickItemPacket sent on the client's connection (Connection.send(packet, listener, flush), which every
 * send reaches): 1.21.4 has no such packet, so the client performs what 1.21.3's server did on receiving it -- the slot swapped
 * into the suitable hotbar slot, which becomes selected -- as a SWAP click on the inventory menu and a carried-item change, which
 * 1.21.4's server applies the same way (CbClientRest.superPickItem, MultiPlayerGameMode.handlePickItem's bridge). On the game
 * thread, as 1.21.3's server handled it on its own; the send listener then hears success.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_2535.class)
public abstract class CbFinPickSendMixin {

    @Inject(method = "method_52906", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPickItem(net.minecraft.class_2596<?> packet, net.minecraft.class_7648 listener, boolean flush, CallbackInfo ci) {
        if (packet instanceof ServerboundPickItemPacket pick
                && ((net.minecraft.class_2535) (Object) this).method_36122() == net.minecraft.class_2598.field_11941) {
            ci.cancel();
            net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
            mc.execute(() -> {
                if (mc.field_1761 == null) {
                    // 1.21.3: a game packet outside the play protocol failed to encode
                    throw new IllegalStateException("ServerboundPickItemPacket sent outside the play protocol");
                }
                CbClientRest.superPickItem(mc.field_1761, pick.method_12293());
                if (listener != null) {
                    listener.method_45083();
                }
            });
        }
    }
}
