package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 WardenModel's emissive part lists: getTendrilsLayerModelParts / getHeartLayerModelParts /
 * getBioluminescentLayerModelParts / getPulsatingSpotsLayerModelParts. 1.21.2 keeps the same lists and returns them
 * from the same-named methods, which take the render state and ignore it.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_7280.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderModelWardenMixin {

    @Shadow
    @Final
    private java.util.List<net.minecraft.class_630> field_38451;

    @Shadow
    @Final
    private java.util.List<net.minecraft.class_630> field_38452;

    @Shadow
    @Final
    private java.util.List<net.minecraft.class_630> field_38453;

    @Shadow
    @Final
    private java.util.List<net.minecraft.class_630> field_38454;

    public java.util.List<net.minecraft.class_630> method_42738() {
        return this.field_38451;
    }

    public java.util.List<net.minecraft.class_630> method_42739() {
        return this.field_38452;
    }

    public java.util.List<net.minecraft.class_630> method_42740() {
        return this.field_38453;
    }

    public java.util.List<net.minecraft.class_630> method_42741() {
        return this.field_38454;
    }
}
