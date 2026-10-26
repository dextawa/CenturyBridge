package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 ModelLayerLocation.getModel() / getLayer(): 1.21.2 made it a record of the same two values */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_5601.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderModelLayerLocationMixin {

    public net.minecraft.class_2960 method_35743() {
        return ((net.minecraft.class_5601) (Object) this).comp_2995();
    }

    public String method_35744() {
        return ((net.minecraft.class_5601) (Object) this).comp_2996();
    }
}
