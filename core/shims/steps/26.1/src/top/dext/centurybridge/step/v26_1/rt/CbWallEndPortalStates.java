package top.dext.centurybridge.step.v26_1.rt;

import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.core.Direction;

/**
 * EndPortalRenderState.facesToShow: 1.21.11 typed it EnumSet&lt;Direction&gt; and let it be replaced; 26.1 types it
 * Set&lt;Direction&gt; and keeps it final, still holding the EnumSet its constructor makes. A read answers that set; a
 * write puts the given faces into it (the renderer reads the set's content, which is what a replaced set changed).
 */
@Environment(EnvType.CLIENT)
public final class CbWallEndPortalStates {

    private CbWallEndPortalStates() {
    }

    public static EnumSet<Direction> facesToShow(EndPortalRenderState state) {
        Set<Direction> faces = state.facesToShow;
        return faces instanceof EnumSet<Direction> set ? set : faces.isEmpty() ? EnumSet.noneOf(Direction.class) : EnumSet.copyOf(faces);
    }

    public static void setFacesToShow(EndPortalRenderState state, EnumSet<Direction> faces) {
        if (faces == state.facesToShow) {
            return;
        }
        state.facesToShow.clear();
        state.facesToShow.addAll(faces);
    }
}
