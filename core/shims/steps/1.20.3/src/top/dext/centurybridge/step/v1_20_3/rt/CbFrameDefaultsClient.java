package top.dext.centurybridge.step.v1_20_3.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * frameDefaults of this step's carrying override hooks on client classes: what a 1.20.2 override's
 * rewritten super call passes, with no frame of vanilla's call, for an argument 1.20.3 added.
 */
@Environment(EnvType.CLIENT)
public final class CbFrameDefaultsClient {

    /**
     * SoundEventListener.onPlaySound(sound, accessor) -> (.., range): 1.20.2's SubtitleOverlay showed every
     * subtitle it was handed, wherever the listener stood; 1.20.3's Subtitle keeps the range and shows it
     * only within it -- and an infinite range always (Subtitle.isAudibleFrom: Float.isInfinite)
     */
    public static float soundRange() {
        return Float.POSITIVE_INFINITY;
    }

    private CbFrameDefaultsClient() {
    }
}
