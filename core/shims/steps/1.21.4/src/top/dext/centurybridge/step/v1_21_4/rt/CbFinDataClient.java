package top.dext.centurybridge.step.v1_21_4.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2403;

/** the client half of 1.21.3's createStandardGenerator (CbFinData): 1.21.4's client data providers */
@Environment(EnvType.CLIENT)
public final class CbFinDataClient {

    private CbFinDataClient() {
    }

    static void addClientProviders(class_2403 generator) {
        net.minecraft.client.data.Main.method_65774(generator, true);
    }
}
