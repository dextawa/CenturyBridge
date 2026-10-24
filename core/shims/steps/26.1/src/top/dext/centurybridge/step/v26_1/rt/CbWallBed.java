package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * 1.21.11's BedRenderer.getExtents(consumer) on 26.1's getExtents(part, consumer).
 * <p>
 * 1.21.11's bed item was one special model: getExtents gave the head model's extents posed as the bed's
 * head (translateToHead(pose, false, SOUTH)) and then the foot model's posed as its foot (true). 26.1's
 * bed item is a composite of two special models, one per part, each placed by its own transformation --
 * exactly those two poses -- and getExtents(part, consumer) gives the part's extents unposed.
 * <p>
 * So a 1.21.11 override, asked for one part, answers its whole bed with that part's pose undone (the
 * composite poses it again: both parts together give the override's points once per part); its
 * super.getExtents(consumer), and a mod's call, give both parts posed, head first.
 */
@Environment(EnvType.CLIENT)
public final class CbWallBed {

    private CbWallBed() {
    }

    /** 1.21.11's getExtents(consumer), declared by a mod's bed renderer */
    public interface LegacyExtents {
        void getExtents(Consumer<Vector3fc> consumer);
    }

    /** super's 26.1 getExtents, from a mod bed renderer's super.getExtents(consumer) (the accessor keeps the intermediary name) */
    public interface SuperExtents {
        void cb$super$method_72160(BedPart part, Consumer<Vector3fc> consumer);
    }

    private static final Matrix4fc HEAD = pose(false);
    private static final Matrix4fc FOOT = pose(true);
    private static final Matrix4fc HEAD_INVERSE = new Matrix4f(HEAD).invert();
    private static final Matrix4fc FOOT_INVERSE = new Matrix4f(FOOT).invert();

    /** 1.21.11's translateToHead(pose, foot, SOUTH), the pose getExtents gave each part in */
    private static Matrix4fc pose(boolean foot) {
        PoseStack pose = new PoseStack();
        pose.translate(0.0F, 0.5625F, foot ? -1.0F : 0.0F);
        pose.mulPose(Axis.XP.rotationDegrees(90.0F));
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.ZP.rotationDegrees(180.0F + Direction.SOUTH.toYRot()));
        pose.translate(-0.5F, -0.5F, -0.5F);
        return new Matrix4f(pose.last().pose());
    }

    private static Consumer<Vector3fc> transformed(Matrix4fc matrix, Consumer<Vector3fc> consumer) {
        return v -> consumer.accept(matrix.transformPosition(v, new Vector3f()));
    }

    /** 26.1's getExtents(part, consumer) of a mod bed renderer: its 1.21.11 extents with the part's pose undone */
    public static void getExtents(LegacyExtents self, BedPart part, Consumer<Vector3fc> consumer) {
        self.getExtents(transformed(part == BedPart.HEAD ? HEAD_INVERSE : FOOT_INVERSE, consumer));
    }

    /** a mod's super.getExtents(consumer): super's head extents posed as the head, then its foot's posed as the foot */
    public static void superExtents(SuperExtents self, Consumer<Vector3fc> consumer) {
        self.cb$super$method_72160(BedPart.HEAD, transformed(HEAD, consumer));
        self.cb$super$method_72160(BedPart.FOOT, transformed(FOOT, consumer));
    }

    /** a mod's call of 1.21.11's getExtents(consumer): a 1.21.11 override's own answer, else both parts posed */
    public static void callExtents(BedRenderer renderer, Consumer<Vector3fc> consumer) {
        if (renderer instanceof LegacyExtents legacy) {
            legacy.getExtents(consumer);
            return;
        }
        renderer.getExtents(BedPart.HEAD, transformed(HEAD, consumer));
        renderer.getExtents(BedPart.FOOT, transformed(FOOT, consumer));
    }
}
