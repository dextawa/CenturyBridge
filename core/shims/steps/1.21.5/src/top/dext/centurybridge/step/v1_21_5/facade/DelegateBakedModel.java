package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.rt.CbBakedModels;

/**
 * 1.21.4 DelegateBakedModel (class_10200, gone in 1.21.5; javap -c -p 1.21.4): abstract, its parent model in field_54188
 * (protected final there; public here: access wideners never reach a CenturyBridge class), every BakedModel method
 * forwarded to the parent. The parent may be a 1.21.5 BlockStateModel, so each forward is the 1.21.4 call a converted
 * mod makes on any model (rt/CbBakedModels). 1.21.5's collectParts / particleIcon come from this model's own 1.21.4
 * methods (CbBakedModels.collectParts), so a subclass overriding getQuads is what 1.21.5 renders.
 */
@Environment(EnvType.CLIENT)
public abstract class DelegateBakedModel implements CbBakedModels.LegacyBakedModel, CbBakedModels.LegacyItemProps {
    /** parent */
    public final net.minecraft.class_1087 field_54188;

    public DelegateBakedModel(net.minecraft.class_1087 parent) {
        this.field_54188 = parent;
    }

    @Override
    public List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 side, net.minecraft.class_5819 random) {
        return CbBakedModels.getQuads(this.field_54188, state, side, random);
    }

    @Override
    public boolean method_4708() {
        return CbBakedModels.useAmbientOcclusion(this.field_54188);
    }

    @Override
    public boolean method_4712() {
        return CbBakedModels.isGui3d(this.field_54188);
    }

    @Override
    public boolean method_24304() {
        return CbBakedModels.usesBlockLight(this.field_54188);
    }

    @Override
    public net.minecraft.class_1058 method_4711() {
        return CbBakedModels.getParticleIcon(this.field_54188);
    }

    @Override
    public net.minecraft.class_809 method_4709() {
        return CbBakedModels.getTransforms(this.field_54188);
    }

    /** 1.21.5 collectParts: this model's own 1.21.4 quads */
    @Override
    public void method_68513(net.minecraft.class_5819 random, List<net.minecraft.class_10889> parts) {
        CbBakedModels.collectParts(this, random, parts);
    }

    /** 1.21.5 particleIcon: this model's getParticleIcon */
    @Override
    public net.minecraft.class_1058 method_68511() {
        return CbBakedModels.particleIcon(this);
    }
}
