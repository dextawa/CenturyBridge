package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ClientPacketListener.handleAwardStats: 1.20.4 told the current screen when it was a StatsUpdateListener
 * (facade/StatsUpdateListener); 1.20.5 tells only a StatsScreen. Any other screen implementing the 1.20.4
 * interface is told after the statistics are stored, as 1.20.4 told it (StatsScreen already was).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_634.class)
public abstract class CbClientAwardStatsMixin {

    @Inject(method = "method_11129", at = @At("TAIL"))
    private void cb$statsUpdateListener(net.minecraft.class_2617 packet, CallbackInfo ci) {
        net.minecraft.class_437 screen = net.minecraft.class_310.method_1551().field_1755;
        if (screen instanceof top.dext.centurybridge.step.v1_20_5.facade.StatsUpdateListener listener
                && !(screen instanceof net.minecraft.class_447)) {
            listener.method_2300();
        }
    }
}
