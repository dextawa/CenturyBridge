package top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer;

/**
 * Fabric API 1.21.5's renderer-v1 material.RenderMaterial (removed in 1.21.6): an immutable material, canonical per
 * value as Indigo's were (RenderMaterialImpl.byIndex), so == compares them as in 1.21.5. STANDARD_ID as 1.21.5's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface RenderMaterial extends MaterialView {
    net.minecraft.class_2960 STANDARD_ID = net.minecraft.class_2960.method_60655("fabric", "standard");
}
