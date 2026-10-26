package top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer;

/** Fabric API 1.21.5's renderer-v1 material.MaterialView (removed in 1.21.6), members as 1.21.5's (javap). */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface MaterialView {
    BlendMode blendMode();

    boolean emissive();

    boolean disableDiffuse();

    net.fabricmc.fabric.api.util.TriState ambientOcclusion();

    GlintMode glintMode();

    /** 1.21.5's material.ShadeMode is 1.21.6's mesh.ShadeMode (the step renames it: same constants) */
    net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode shadeMode();
}
