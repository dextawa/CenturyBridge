package top.dext.centurybridge.step.v1_21_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_1.rt.CbClientConfigurationHandlers;

/**
 * The client's current configuration handler, for the networkHandler() a 1.21 ClientConfigurationNetworking
 * context is given (CbClientConfigurationHandlers): current from the handler's construction until its
 * configuration finishes (handleConfigurationFinished), the span Fabric API keeps its configuration addon for.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8674.class)
abstract class CbClientConfigurationHandlerTracking {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$beginConfiguration(net.minecraft.class_310 client, net.minecraft.class_2535 connection,
                                       net.minecraft.class_8675 connectionState, CallbackInfo ci) {
        CbClientConfigurationHandlers.begin((net.minecraft.class_8674) (Object) this);
    }

    @Inject(method = "method_52794", at = @At("TAIL"))
    private void cb$endConfiguration(net.minecraft.class_8733 packet, CallbackInfo ci) {
        CbClientConfigurationHandlers.end((net.minecraft.class_8674) (Object) this);
    }
}
