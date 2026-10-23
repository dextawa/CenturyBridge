package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** The client's side of CbRegistries: the render thread, and the registries of the world it shows. */
@Environment(EnvType.CLIENT)
public final class CbRegistriesClient implements CbRegistries.Client {

    private final net.minecraft.class_310 minecraft;

    public CbRegistriesClient(net.minecraft.class_310 minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public boolean onThread() {
        return minecraft.method_18854();
    }

    @Override
    public net.minecraft.class_5455 access() {
        net.minecraft.class_638 level = minecraft.field_1687;
        return level == null ? null : level.method_30349();
    }

    @Override
    public net.minecraft.class_1937 level() {
        return minecraft.field_1687;
    }
}
