package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** LevelRenderer's restored 0.136 frame context (mixin/CbWorldRenderHomeMixin adds it) */
@Environment(EnvType.CLIENT)
public interface CbWorldRenderHomeHolder {
    CbWorldRenderHomeContext cb$homeContext();
}
