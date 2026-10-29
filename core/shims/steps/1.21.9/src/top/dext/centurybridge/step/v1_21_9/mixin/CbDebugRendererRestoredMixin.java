package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_11631;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_634;
import net.minecraft.class_863;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8 DebugRenderer members 1.21.9 dropped when the debug renderers became debug screen entries (rebuilt by
 * refreshRendererList when the entry list changes) fed by subscriptions (javap -c both):
 * <ul>
 * <li>switchRenderChunkborder() / toggleRenderOctree(): 1.21.8 flipped its own flag and answered the new state; 1.21.9
 * keeps that state as the CHUNK_BORDERS / CHUNK_SECTION_OCTREE entries of Minecraft's debug entry list, flipped by
 * DebugScreenEntryList.toggleStatus (what 1.21.9's F3+G calls), which answers the new state; the renderer list follows
 * on the next frame (refreshRendererList, on the list's version).</li>
 * <li>clear(): 1.21.8 dropped what every renderer had been sent; 1.21.9 keeps that data in the connection's
 * ClientDebugSubscriber, whose clear() 1.21.9 runs on login and respawn (opened by the layer's access widener). No
 * connection, nothing was sent: nothing to drop.</li>
 * <li>render(pose, frustum, buffers, x, y, z): 1.21.8's opaque pass (chunk borders, octree, game test highlights);
 * 1.21.9's render(.., translucent = false) draws the opaque renderer list.</li>
 * </ul>
 * 1.21.9 never calls them. Open: renderAfterTranslucents(pose, buffers, x, y, z) (1.21.9's translucent pass needs the
 * frame's frustum, which the 1.21.8 call does not carry), the per-renderer fields (1.21.9 builds a new renderer per
 * refresh, only for the enabled entries) and the constructor.
 */
@Mixin(value = class_863.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbDebugRendererRestoredMixin {

    /** DebugRenderer.switchRenderChunkborder() */
    public boolean method_3713() {
        return class_310.method_1551().field_61504.method_72775(class_11631.field_61582);
    }

    /** DebugRenderer.toggleRenderOctree() */
    public boolean method_62979() {
        return class_310.method_1551().field_61504.method_72775(class_11631.field_61555);
    }

    /** DebugRenderer.clear() */
    public void method_20413() {
        class_634 connection = class_310.method_1551().method_1562();
        if (connection != null) {
            connection.field_62944.method_74730();
        }
    }

    /** DebugRenderer.render(pose, frustum, buffers, x, y, z): the opaque pass */
    public void method_23099(class_4587 pose, class_4604 frustum, class_4597.class_4598 buffers, double x, double y, double z) {
        // called through the class, not @Shadow-ed: 1.21.11 reshapes render, and a shadow of a reshaped member strips
        // the mixin when the layer climbs there; a call site climbs through the later steps' wiring
        ((class_863) (Object) this).method_23099(pose, frustum, buffers, x, y, z, false);
    }
}
