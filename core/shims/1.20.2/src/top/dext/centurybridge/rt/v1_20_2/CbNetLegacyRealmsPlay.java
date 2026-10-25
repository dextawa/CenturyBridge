package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_437;
import net.minecraft.class_4877;

/**
 * Added (adapterHooks) to a mod class extending RealmsMainScreen that overrides 1.20.1's instance
 * play(server, screen): 1.20.2's play is static, so nothing overrides it; vanilla's main screen calls
 * it with itself as the screen, and CbNetRealmsMainScreenBridgeHooks sends such a call to this override.
 */
@Environment(EnvType.CLIENT)
public interface CbNetLegacyRealmsPlay {

    void method_20853(class_4877 server, class_437 screen);
}
