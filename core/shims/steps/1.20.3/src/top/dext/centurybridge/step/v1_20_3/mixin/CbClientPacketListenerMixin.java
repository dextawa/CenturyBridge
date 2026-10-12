package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * 1.20.2's ClientPacketListener.handleSetSpawn ended by telling the ReceivingLevelScreen on show that the
 * level's loading packets had arrived (loadingPacketsReceived()). 1.20.3 drives its own screens through
 * the supplier it builds them with and no longer tells the screen; a screen a 1.20.2 mod built with the
 * 1.20.2 constructor waits for exactly this call ({@link CbGui.CbLegacyLoading}), so it is made again.
 * On a screen 1.20.3 built it only sets the record, which nothing of 1.20.3 reads.
 */
@Mixin(net.minecraft.class_634.class)
public abstract class CbClientPacketListenerMixin {

    @Inject(method = "method_11142", at = @At("TAIL"))
    private void cb$loadingPacketsReceived(net.minecraft.class_2759 packet, CallbackInfo ci) {
        if (net.minecraft.class_310.method_1551().field_1755 instanceof CbGui.CbLegacyReceivingLevel screen) {
            screen.method_40040();
        }
    }
}
