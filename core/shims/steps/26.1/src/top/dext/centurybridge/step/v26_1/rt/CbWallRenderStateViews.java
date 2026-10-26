package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BedPart;

/**
 * 1.21.11 block entity render state fields 26.1 keeps as another field of the same state (javap of both renderers'
 * extractRenderState and submit):
 * <ul>
 * <li>BedRenderState.isHead: 1.21.11's extract set it to part == HEAD of the bed block; 26.1 keeps the part itself.
 *     A view both ways.</li>
 * <li>ChestRenderState.angle: 1.21.11's extract set it to the chest block's FACING.toYRot() and its submit turned the
 *     model by it; 26.1 keeps the facing and turns the model by it. Read: the facing's angle. Written: the horizontal
 *     facing nearest the angle (1.21.11 turned by any angle a mod wrote; 26.1 turns only by a facing).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallRenderStateViews {

    private CbWallRenderStateViews() {
    }

    /** 1.21.11 BedRenderState.isHead (get) */
    public static boolean bedIsHead(BedRenderState state) {
        return state.part == BedPart.HEAD;
    }

    /** 1.21.11 BedRenderState.isHead (put) */
    public static void setBedIsHead(BedRenderState state, boolean head) {
        state.part = head ? BedPart.HEAD : BedPart.FOOT;
    }

    /** 1.21.11 ChestRenderState.angle (get) */
    public static float chestAngle(ChestRenderState state) {
        return state.facing.toYRot();
    }

    /** 1.21.11 ChestRenderState.angle (put) */
    public static void setChestAngle(ChestRenderState state, float angle) {
        state.facing = Direction.fromYRot(angle);
    }
}
