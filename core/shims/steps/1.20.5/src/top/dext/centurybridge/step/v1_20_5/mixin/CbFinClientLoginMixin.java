package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinClient;

/**
 * ClientPacketListener.handleLogin: the server's enforcesSecureChat onto the server entry, where 1.20.4 kept it
 * (1.20.4 set it from the server data packet, which no longer carries it).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_634.class)
public abstract class CbFinClientLoginMixin {

    @Inject(method = "method_11120", at = @At("RETURN"))
    private void cbS4$secureChat(net.minecraft.class_2678 packet, CallbackInfo ci) {
        net.minecraft.class_642 data = ((net.minecraft.class_634) (Object) this).method_45734();
        if (data != null) {
            ((CbFinClient.ServerDataSlot) data).cbS4$enforcesSecureChat(packet.comp_2200());
        }
    }
}
