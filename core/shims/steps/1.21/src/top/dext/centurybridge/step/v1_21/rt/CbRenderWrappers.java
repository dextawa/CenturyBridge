package top.dext.centurybridge.step.v1_21.rt;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderDecalAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderDoubleAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderMultipleAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderOutlineAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderSpriteAccess;

/**
 * 1.21's own wrapping VertexConsumers, as 1.20.6's were towards endVertex() and defaultColor(..): each of them
 * hands the elements it is given on to what it wraps, so a vertex 1.20.6 code ends on the wrapper ends on each
 * wrapped consumer -- 1.20.6's sprite expander, decal, outline generator and multi-consumers called endVertex()
 * on theirs. A default color went on through the sprite expander and the multi-consumers; 1.20.6's decal and
 * outline generator ignored it (the decal wrote white, the outline generator its own color), as 1.21's do.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderWrappers {

    private CbRenderWrappers() {
    }

    /** the consumers a 1.20.6 endVertex() on a 1.21 wrapper goes on to, or null if it is no wrapper of 1.21's */
    public static List<net.minecraft.class_4588> cbWrapped(net.minecraft.class_4588 consumer) {
        List<net.minecraft.class_4588> forColor = cbWrappedForColor(consumer);
        if (forColor != null) {
            return forColor;
        }
        if (consumer instanceof CbRenderDecalAccess decal) {
            return List.of(decal.cb$delegate());
        }
        if (consumer instanceof CbRenderOutlineAccess outline) {
            return List.of(outline.cb$delegate());
        }
        return null;
    }

    /**
     * the consumers a 1.20.6 defaultColor(..) / unsetDefaultColor() on a 1.21 wrapper goes on to: empty for the
     * decal and the outline generator (they ignored it), null if it is no wrapper of 1.21's
     */
    public static List<net.minecraft.class_4588> cbWrappedForColor(net.minecraft.class_4588 consumer) {
        if (consumer instanceof CbRenderSpriteAccess sprite) {
            return List.of(sprite.cb$delegate());
        }
        if (consumer instanceof CbRenderDoubleAccess pair) {
            return List.of(pair.cb$first(), pair.cb$second());
        }
        if (consumer instanceof CbRenderMultipleAccess many) {
            return Arrays.asList(many.cb$delegates());
        }
        if (consumer instanceof CbRenderDecalAccess || consumer instanceof CbRenderOutlineAccess) {
            return List.of();
        }
        return null;
    }
}
