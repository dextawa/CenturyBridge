package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** 1.21.1's GameRenderer.reloadShaders(provider), restored on GameRenderer (mixin/CbRestGameRendererShadersRestored) */
@Environment(EnvType.CLIENT)
public interface CbRestReloadShaders {
    void cb$reloadShaders(net.minecraft.class_5912 provider);
}
