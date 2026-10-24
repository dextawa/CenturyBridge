package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * ClientConfigurationPacketListenerImpl: 1.20.4's registryAccess() -- the client's static registries with
 * the registry data received so far. 1.20.5 collects the received data and builds that access once, when
 * configuration finishes; asked earlier, it is built the same way from what has been collected.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8674.class)
public abstract class CbFinClientConfigListenerMixin {

    @Shadow
    private <T> T method_57044(Function<net.minecraft.class_5912, T> withResources) {
        throw new AssertionError();
    }

    @Shadow
    private net.minecraft.class_5455.class_6890 method_57043(net.minecraft.class_5912 resources) {
        throw new AssertionError();
    }

    /** was registryAccess() */
    protected net.minecraft.class_5455.class_6890 method_29091() {
        return this.method_57044(this::method_57043);
    }
}
