package top.dext.centurybridge.step.v1_21_4.compat.fapi.modelloading;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBaking;

/**
 * Fabric API 1.21.3's DelegatingUnbakedModel (gone in 1.21.4): the unbaked model that is another model by id -- it resolves
 * that id and bakes to the baker's model of it. Body for body, as a 1.21.4 UnbakedModel (see {@link CbModelBaking}).
 */
@Environment(EnvType.CLIENT)
public final class DelegatingUnbakedModel implements net.minecraft.class_1100, CbModelBaking.CbLegacyUnbakedModel {
    private final net.minecraft.class_2960 delegate;

    public DelegatingUnbakedModel(net.minecraft.class_2960 delegate) {
        this.delegate = delegate;
    }

    @Override
    public void method_62326(net.minecraft.class_10526.class_10103 resolver) {
        resolver.method_62642(this.delegate);
    }

    @Override
    public net.minecraft.class_1087 method_4753(net.minecraft.class_7775 baker, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
            net.minecraft.class_3665 state) {
        return baker.method_45873(this.delegate, state);
    }

    @Override
    public net.minecraft.class_1087 method_4753(net.minecraft.class_10419 slots, net.minecraft.class_7775 baker, net.minecraft.class_3665 state,
            boolean ambientOcclusion, boolean usesBlockLight, net.minecraft.class_809 transforms) {
        return CbModelBaking.bake(this, slots, baker, state, ambientOcclusion, usesBlockLight, transforms);
    }
}
