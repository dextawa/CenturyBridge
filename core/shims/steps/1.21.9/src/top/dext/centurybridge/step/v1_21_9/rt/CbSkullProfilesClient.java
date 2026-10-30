package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11755;
import net.minecraft.class_310;

/** the client's ProfileResolver (Minecraft.services().profileResolver()), for {@link CbSkullProfiles} */
@Environment(EnvType.CLIENT)
final class CbSkullProfilesClient {

    private CbSkullProfilesClient() {
    }

    static class_11755 resolver() {
        class_310 mc = class_310.method_1551();
        return mc == null ? null : mc.method_73361().comp_4624();
    }
}
