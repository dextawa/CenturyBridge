package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.client.networking.v1.ClientConfigurationNetworking;

/**
 * Tells the 1.20.4 configuration networking (compat/fapi ClientConfigurationNetworking) which configuration
 * network handler and connection the client configures with: 1.20.4's configuration channel handlers were
 * handed the handler, and its packet handlers were held back once the connection closed; 1.20.5's handler
 * context carries neither.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8674.class)
public abstract class CbFapiClientConfigurationMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$configuring(net.minecraft.class_310 client, net.minecraft.class_2535 connection, net.minecraft.class_8675 state,
                                CallbackInfo ci) {
        ClientConfigurationNetworking.configuring((net.minecraft.class_8674) (Object) this, connection);
    }
}
