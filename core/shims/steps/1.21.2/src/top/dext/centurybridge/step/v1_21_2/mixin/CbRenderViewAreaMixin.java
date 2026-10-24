package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderSections;

/** 1.21.1 ViewArea.repositionCamera(x, z) on 1.21.2's (see CbRenderSections) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_769.class)
public abstract class CbRenderViewAreaMixin implements CbRenderSections.LegacyViewArea {

    /** 1.21.1 repositionCamera(x, z): 1.21.2's on the camera's section */
    @Override
    public void method_3330(double x, double z) {
        ((net.minecraft.class_769) (Object) this).method_3330(CbRenderSections.section(x, z));
    }
}
