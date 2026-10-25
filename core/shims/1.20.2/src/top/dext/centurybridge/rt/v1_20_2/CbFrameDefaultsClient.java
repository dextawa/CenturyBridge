package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * frameDefaults of this layer's carrying override hooks on client classes (see CbFrameDefaults):
 * what a 1.20.1 override's rewritten super call passes, with no frame of vanilla's call, for an
 * argument 1.20.2 added.
 */
@Environment(EnvType.CLIENT)
public final class CbFrameDefaultsClient {

    /**
     * SpriteLoader.loadAndStitch(resources, atlas, mipLevel, executor) -> (.., metadataSections): 1.20.1's
     * sprite loader read the animation section of each sprite's metadata; 1.20.2's own four-argument
     * loadAndStitch (method_52849) passes DEFAULT_METADATA_SECTIONS, that one section
     */
    public static java.util.Collection<net.minecraft.class_3270<?>> spriteMetadataSections() {
        return net.minecraft.class_7766.field_45632;
    }

    /**
     * EditBox.moveCursor / moveCursorTo / moveCursorToStart / moveCursorToEnd -> (.., select): 1.20.1
     * extended the selection by the box's shiftPressed state (Screen.hasShiftDown() as of the box's last
     * keyPressed), which this layer keeps again (CbGuiEditBoxBridgeHooks) and the covered old shapes pass on
     */
    public static boolean editBoxShift(net.minecraft.class_342 self) {
        return ((CbLegacyEditBoxShift) self).cb$legacyShiftPressed();
    }

    private CbFrameDefaultsClient() {
    }
}
