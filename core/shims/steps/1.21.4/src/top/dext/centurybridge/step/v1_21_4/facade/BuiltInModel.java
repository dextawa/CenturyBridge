package top.dext.centurybridge.step.v1_21_4.facade;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemRender;

/**
 * 1.21.3 BuiltInModel (class_1090, gone in 1.21.4): the baked "builtin/entity" model -- no quads, its transforms and
 * particle, drawn by BlockEntityWithoutLevelRenderer (isCustomRenderer). ItemRenderer.render(.., model) does so again
 * for it (CbItemRender).
 */
@Environment(EnvType.CLIENT)
public class BuiltInModel implements net.minecraft.class_1087, CbItemRender.CbLegacyCustomRenderer, CbItemRender.CbLegacyOverrides {

    /** itemTransforms */
    public final net.minecraft.class_809 field_5404;
    /** particleTexture */
    public final net.minecraft.class_1058 field_16594;
    /** usesBlockLight */
    public final boolean field_21862;

    public BuiltInModel(net.minecraft.class_809 transforms, net.minecraft.class_1058 particle, boolean usesBlockLight) {
        this.field_5404 = transforms;
        this.field_16594 = particle;
        this.field_21862 = usesBlockLight;
    }

    @Override
    public List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 side, net.minecraft.class_5819 random) {
        return Collections.emptyList();
    }

    @Override
    public boolean method_4708() {
        return false;
    }

    @Override
    public boolean method_4712() {
        return true;
    }

    @Override
    public boolean method_24304() {
        return this.field_21862;
    }

    /** isCustomRenderer: drawn by BlockEntityWithoutLevelRenderer */
    public boolean method_4713() {
        return true;
    }

    @Override
    public boolean cb$isCustomRenderer() {
        return this.method_4713();
    }

    @Override
    public BakedOverrides cb$getOverrides() {
        return BakedOverrides.field_4292;
    }

    @Override
    public net.minecraft.class_1058 method_4711() {
        return this.field_16594;
    }

    @Override
    public net.minecraft.class_809 method_4709() {
        return this.field_5404;
    }
}
