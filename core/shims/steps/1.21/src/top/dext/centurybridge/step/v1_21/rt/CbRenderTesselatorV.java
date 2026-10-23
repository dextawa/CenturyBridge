package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.20.6's Tesselator.getBuilder() / end() on 1.21's Tesselator (CbRenderTesselatorMixin), for CenturyBridge code */
@Environment(EnvType.CLIENT)
public interface CbRenderTesselatorV {

    /** getBuilder() */
    net.minecraft.class_287 method_1349();

    /** end() */
    void method_1350();
}
