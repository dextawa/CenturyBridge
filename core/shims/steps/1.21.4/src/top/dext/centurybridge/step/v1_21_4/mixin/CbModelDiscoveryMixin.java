package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelLoading;

/**
 * ModelDiscovery's 1.21.3 registerStandardModels / getTopModels (see {@link CbModelLoading}): the top models 1.21.3 kept by
 * model location, each also a root 1.21.4's discovery resolves.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10097.class)
public abstract class CbModelDiscoveryMixin implements CbModelLoading.CbLegacyDiscovery {

    @Shadow
    @Final
    private Map<net.minecraft.class_2960, net.minecraft.class_1100> field_53665;

    @Shadow
    @Final
    private Map<net.minecraft.class_2960, net.minecraft.class_1100> field_53668;

    @Shadow
    public abstract void method_65744(net.minecraft.class_10526 root);

    private final Map<net.minecraft.class_1091, net.minecraft.class_1100> cb$topModels = new LinkedHashMap<>();

    /** 1.21.3 addRoot(location, model) */
    @Override
    public void cb$addTop(net.minecraft.class_1091 location, net.minecraft.class_1100 model) {
        this.cb$topModels.put(location, model);
        this.method_65744(model);
    }

    /** was getTopModels() */
    @Override
    public Map<net.minecraft.class_1091, net.minecraft.class_1100> method_62637() {
        return this.cb$topModels;
    }

    @Override
    public Map<net.minecraft.class_2960, net.minecraft.class_1100> cb$inputModels() {
        return this.field_53665;
    }

    @Override
    public Map<net.minecraft.class_2960, net.minecraft.class_1100> cb$specialModels() {
        return this.field_53668;
    }

    /** was registerStandardModels(loadedModels) */
    public void method_62632(net.minecraft.class_9824.class_10095 loaded) {
        CbModelLoading.registerStandardModels(this, loaded);
    }
}
