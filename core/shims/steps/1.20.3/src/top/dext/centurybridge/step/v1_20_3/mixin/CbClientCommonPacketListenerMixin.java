package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbNet;

/**
 * ClientCommonPacketListenerImpl.handleResourcePackPush: the pushed pack's id is kept, the pack a 1.20.2-made
 * ServerboundResourcePackPacket answers about ({@link CbNet#cbResponsePackId}).
 */
@Mixin(net.minecraft.class_8673.class)
public abstract class CbClientCommonPacketListenerMixin {

    @Inject(method = "method_52784", at = @At("HEAD"))
    private void cb$recordPushedPack(net.minecraft.class_2720 packet, CallbackInfo ci) {
        CbNet.cbPushed(packet.comp_2158());
    }
}
