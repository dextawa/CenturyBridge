package top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer;

/** Fabric API 1.21.5's renderer-v1 material.MaterialFinder (removed in 1.21.6), members as 1.21.5's (javap). */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface MaterialFinder extends MaterialView {
    MaterialFinder blendMode(BlendMode blendMode);

    MaterialFinder emissive(boolean emissive);

    MaterialFinder disableDiffuse(boolean disable);

    MaterialFinder ambientOcclusion(net.fabricmc.fabric.api.util.TriState mode);

    MaterialFinder glintMode(GlintMode mode);

    MaterialFinder shadeMode(net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode mode);

    MaterialFinder copyFrom(MaterialView material);

    MaterialFinder clear();

    RenderMaterial find();
}
