package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** implemented on 1.21.9's LevelRenderer by CbWorldRenderEventsMixin: the renderer's restored frame context */
@Environment(EnvType.CLIENT)
public interface CbWorldRenderHolder {
    CbWorldRenderContextImpl cb$worldRenderContext();
}
