package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.special.BedSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.BedPart;
import top.dext.centurybridge.step.v26_1.facade.LegacyBedSpecialRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * 1.21.11's bed item rendering on 26.1's: BedRenderer.getExtents(consumer) / submitSpecial and the whole-bed
 * BedSpecialRenderer (+ Unbaked) 1.21.11 made (facade/LegacyBedSpecialRenderer).
 * <p>
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

    /**
     * 1.21.11's BedRenderer.submitSpecial(pose, collector, light, overlay, material, outline), declared by a mod's bed
     * renderer (adapterHooks: a mod class under BedRenderer declaring it implements this).
     */
    public interface LegacySubmitSpecial {
        void submitSpecial(PoseStack pose, SubmitNodeCollector collector, int light, int overlay, SpriteId sprite, int outline);
    }

    /** a mod's call of 1.21.11's submitSpecial: a 1.21.11 override's own drawing, else 1.21.11's body ({@link #superSubmitSpecial}) */
    public static void submitSpecial(BedRenderer renderer, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, SpriteId sprite,
                                     int outline) {
        if (renderer instanceof LegacySubmitSpecial legacy) {
            legacy.submitSpecial(pose, collector, light, overlay, sprite, outline);
            return;
        }
        superSubmitSpecial(renderer, pose, collector, light, overlay, sprite, outline);
    }

    /**
     * 1.21.11's BedRenderer.submitSpecial body (javap class_825.method_65557): the head model posed as the head
     * (submitPiece(.., headModel, SOUTH, .., false, null, outline): push, translateToHead(pose, false, SOUTH), draw,
     * pop), then the foot model posed as the foot (true), both with the one bed sprite, light, overlay and outline,
     * no crumbling. 26.1's submitPiece(part, ...) draws the part's model with the same arguments, unposed.
     */
    public static void superSubmitSpecial(BedRenderer renderer, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, SpriteId sprite,
                                          int outline) {
        pose.pushPose();
        pose.mulPose(HEAD);
        renderer.submitPiece(BedPart.HEAD, sprite, pose, collector, light, overlay, null, outline);
        pose.popPose();
        pose.pushPose();
        pose.mulPose(FOOT);
        renderer.submitPiece(BedPart.FOOT, sprite, pose, collector, light, overlay, null, outline);
        pose.popPose();
    }

    /** the BedSpecialRenderer.Unbaked records a 1.21.11 constructor made: the whole bed, 1.21.11's one special model */
    private static final Set<BedSpecialRenderer.Unbaked> WHOLE = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    /**
     * 1.21.11 new BedSpecialRenderer.Unbaked(texture): 1.21.11's record held only the texture and baked the whole bed
     * (head and foot, BedRenderer.submitSpecial). 26.1's record also names the part it draws; the record made here
     * names the head (its first part) and is remembered as a whole bed, which it bakes into wherever a 1.21.11 caller
     * bakes it ({@link #bake}, the special model wrapper's {@link CbWallItemModels#specialWrapper}).
     */
    public static BedSpecialRenderer.Unbaked unbaked(Identifier texture) {
        BedSpecialRenderer.Unbaked unbaked = new BedSpecialRenderer.Unbaked(texture, BedPart.HEAD);
        WHOLE.add(unbaked);
        return unbaked;
    }

    /** 1.21.11 new BedSpecialRenderer.Unbaked(color): this(Sheets.colorToResourceSprite(color)) */
    public static BedSpecialRenderer.Unbaked unbaked(DyeColor color) {
        return unbaked(Sheets.colorToResourceSprite(color));
    }

    /** whether a 1.21.11 constructor made this record (the whole bed) */
    public static boolean whole(BedSpecialRenderer.Unbaked unbaked) {
        return WHOLE.contains(unbaked);
    }

    /** 1.21.11 new BedSpecialRenderer(bedRenderer, material): the renderer of the whole bed */
    public static BedSpecialRenderer renderer(BedRenderer bedRenderer, SpriteId sprite) {
        return new LegacyBedSpecialRenderer(bedRenderer, sprite);
    }

    /**
     * 1.21.11's BedSpecialRenderer.Unbaked.bake(context) (javap class_10500$class_10501.method_65698): new
     * BedSpecialRenderer(new BedRenderer(context), BED_MAPPER.apply(texture)), the whole bed; a record a 1.21.11
     * constructor did not make (26.1's data, one part) bakes as 26.1 bakes it.
     */
    public static SpecialModelRenderer<?> bake(BedSpecialRenderer.Unbaked unbaked, SpecialModelRenderer.BakingContext context) {
        if (whole(unbaked)) {
            return new LegacyBedSpecialRenderer(new BedRenderer(context), Sheets.BED_MAPPER.apply(unbaked.texture()));
        }
        return unbaked.bake(context);
    }
}
