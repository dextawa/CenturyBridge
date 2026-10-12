package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2987;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_5455;
import net.minecraft.class_634;
import net.minecraft.class_7699;
import net.minecraft.class_7701;
import net.minecraft.class_7832;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNet;

/**
 * ClientPlayNetworkHandler, two 1.20.1 pieces 1.20.2 moved out of the play phase:
 *
 * onFeatures(FeaturesS2CPacket): 1.20.2 sends the enabled features during configuration and hands
 * them to this handler's constructor; the play handler has no onFeatures. 1.20.1's body is put back
 * (on the client thread, set the enabled features from the packet) for converted calls; vanilla
 * 1.20.2 never calls it, so a subclass override is not told of the features vanilla sets up front.
 *
 * GameJoinS2CPacket.registryManager(): 1.20.2 syncs the registries in the configuration phase
 * instead of sending them with the join packet. When the handler handles a join packet, the
 * registries it holds for this connection are what that packet carried in 1.20.1 (CbNet.comp_93).
 */
@Mixin(class_634.class)
public abstract class CbNetClientPacketListenerBridge {

    @Shadow
    @Final
    @Mutable
    private class_7699 field_45600;

    @Shadow
    @Final
    private class_5455.class_6890 field_25063;

    /**
     * was onFeatures(FeaturesS2CPacket). Off the client thread 1.20.1 re-delivered the packet to this
     * handler on the client thread and aborted the caller (forceMainThread); the packet's own apply
     * cannot do that here -- in 1.20.2 it applies to configuration listeners -- so the handler's
     * method is re-run instead, under the same check vanilla makes before re-delivering.
     */
    public void method_45728(class_7832 packet) {
        class_310 client = class_310.method_1551();
        if (!client.method_18854()) {
            class_634 self = (class_634) (Object) this;
            client.method_40000(() -> {
                if (self.method_52413(packet)) {
                    method_45728(packet);
                }
            });
            throw class_2987.field_13400;
        }
        field_45600 = class_7701.field_40180.method_45387(packet.comp_1113());
    }

    @Inject(method = "method_11120", at = @At("HEAD"))
    private void cb$joinRegistries(class_2678 packet, CallbackInfo ci) {
        CbNet.cbRecordJoinRegistries(packet, field_25063);
    }
}
