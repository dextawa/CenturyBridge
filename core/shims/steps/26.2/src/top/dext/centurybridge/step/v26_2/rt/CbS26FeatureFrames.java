package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;

/**
 * 26.1.2 FeatureRenderDispatcher.renderSolidFeatures() / renderTranslucentFeatures(), which 26.2 replaced by the
 * prepared frame the dispatcher keeps (FeatureRenderDispatcher.preparedFrame, begun by prepareFrame(storage) in
 * LevelRenderer.render): javap of both, 26.1.2 drew every submitted node collection's solid (translucent) features from
 * the dispatcher's submit storage; 26.2's PreparedFrame.executeSolid() (executeTranslucent()) draws the same
 * collections' solid (translucent) phases of the frame being drawn. 26.2's translucent phase also holds the shape
 * outlines and gizmos, which 26.1.2 drew right after it from the main pass. Outside a prepared frame (no context)
 * nothing is drawn, as 26.1.2 drew nothing from an empty storage.
 * <p>Each body makes one game call, so a mixin anchored on the 26.1.2 call follows to 26.2's (MixinBridge's
 * redirected-call rule): 26.2's main pass calls executeSolid / executeTranslucent where 26.1.2's called these.
 */
@Environment(EnvType.CLIENT)
public final class CbS26FeatureFrames {
    private CbS26FeatureFrames() {
    }

    /** 26.1.2 FeatureRenderDispatcher.renderSolidFeatures() */
    public static void renderSolidFeatures(FeatureRenderDispatcher dispatcher) {
        FeatureRenderDispatcher.PreparedFrame frame = dispatcher.preparedFrame;
        if (frame.context != null) {
            frame.executeSolid();
        }
    }

    /** 26.1.2 FeatureRenderDispatcher.renderTranslucentFeatures() */
    public static void renderTranslucentFeatures(FeatureRenderDispatcher dispatcher) {
        FeatureRenderDispatcher.PreparedFrame frame = dispatcher.preparedFrame;
        if (frame.context != null) {
            frame.executeTranslucent();
        }
    }
}
