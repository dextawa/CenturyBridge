package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2's LevelRenderState.haveGlowingEntities, restored on 26.2's LevelRenderState by mixin/CbS26GlowingStateMixin
 * (mods read and write the field itself); the layer reaches it through this interface. 26.1.2 (javap): reset() clears
 * it, LevelRenderer.extractVisibleEntities sets it when an extracted entity appears glowing and entity outlines are
 * shown, and the frame composites the entity outline target only when it is set (renderLevel's post chain,
 * addMainPass's outline target). 26.2 dropped it and composites when an outline feature was submitted
 * (PreparedFrame.hasAnyOutline); mixin/CbS26GlowingFrameMixin ORs the flag into that answer, so a 26.1.2 mod that set
 * it to draw its own outlines (into RenderBuffers.outlineBufferSource) has them composited as on 26.1.2.
 */
@Environment(EnvType.CLIENT)
public interface CbS26GlowingEntities {
    boolean cbS26$haveGlowingEntities();

    void cbS26$setHaveGlowingEntities(boolean value);
}
