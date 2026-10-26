package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The frame's copy of 26.1.2's haveGlowingEntities (rt/CbS26GlowingEntities), set on 26.2's
 * FeatureRenderDispatcher.PreparedFrame when LevelRenderer builds the main pass (mixin/CbS26GlowingFrameMixin), before
 * either of hasAnyOutline's two readers (addMainPass's outline target, render's outline post chain).
 */
@Environment(EnvType.CLIENT)
public interface CbS26OutlineFrame {
    void cbS26$setLegacyOutline(boolean value);
}
