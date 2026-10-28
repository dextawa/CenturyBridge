package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_634;

/** client side of rt/CbVariants: the client connection's registries, when no integrated server runs */
@Environment(EnvType.CLIENT)
public final class CbStepClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CbVariants.client = () -> {
            class_634 connection = class_310.method_1551().method_1562();
            return connection == null ? null : connection.method_29091();
        };
    }
}
