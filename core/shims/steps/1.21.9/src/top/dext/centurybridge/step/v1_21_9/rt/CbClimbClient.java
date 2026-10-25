package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1079;
import net.minecraft.class_11677;
import net.minecraft.class_11697;
import net.minecraft.class_11701;
import net.minecraft.class_11786;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_572;
import net.minecraft.class_6379;
import net.minecraft.class_7677;
import net.minecraft.class_804;
import org.joml.Matrix4f;

/**
 * 1.21.8 client calls whose 1.21.9 shape takes something else for the same value, or a value 1.21.8 code had no
 * part in (the game's own atlas manager and skin cache, a display context 1.21.8 did not have). The climbed layers
 * reach most of these (ShapeRenderer, materials, map and armor renderers, the renderer context).
 */
@Environment(EnvType.CLIENT)
public final class CbClimbClient {

    private CbClimbClient() {
    }

    // ------------------------------------------------------------------ CommonListenerCookie

    /** new CommonListenerCookie(..): a connection starting the 1.21.8 way -- a new level load tracker, no players carried over, no insecure-chat warning seen */
    public static net.minecraft.class_11653 loadTracker() {
        return new net.minecraft.class_11653();
    }

    public static Map<?, ?> noPlayers() {
        return Map.of();
    }

    public static boolean no() {
        return false;
    }

    // ------------------------------------------------------------------ ShapeRenderer

    /** 1.21.8's ShapeRenderer took the PoseStack and drew with its top pose (last()); 1.21.9 takes that pose */
    public static class_4587.class_4665 pose(class_4587 poseStack) {
        return poseStack.method_23760();
    }

    /** ShapeRenderer.renderFace: 1.21.8 drew with poseStack.last().pose(); 1.21.9 takes that matrix */
    public static Matrix4f matrix(class_4587 poseStack) {
        return poseStack.method_23760().method_23761();
    }

    // ------------------------------------------------------------------ the game's own resources

    /** Material.buffer: 1.21.8 looked the sprite up in the game's atlases; 1.21.9 takes the MaterialSet (the game's AtlasManager) */
    public static class_11701 materials() {
        return class_310.method_1551().method_72703();
    }

    /** new MapRenderer / EntityRendererProvider.Context: the game's AtlasManager, which holds 1.21.8's map decoration atlas */
    public static class_11697 atlasManager() {
        return class_310.method_1551().method_72703();
    }

    /** new EntityRendererProvider.Context: the game's player skin render cache (1.21.9's player renderers read skins through it) */
    public static class_11786 skinCache() {
        return class_310.method_1551().method_73362();
    }

    // ------------------------------------------------------------------ models

    /**
     * new HumanoidArmorLayer(parent, inner, outer, ..): 1.21.8 used the inner model for the legs slot and the outer one
     * for head, chest and feet (getArmorModel / usesInnerModel); 1.21.9's ArmorModelSet is (head, chest, legs, feet)
     */
    public static <A extends class_572<?>> class_11677<A> armorSet(A inner, A outer) {
        return new class_11677<>(outer, outer, inner, outer);
    }

    /** new ItemTransforms(8 transforms): the ninth display context (ON_SHELF) is 1.21.9's; a model without it has the identity, as 1.21.9's loader gives one */
    public static class_804 noTransform() {
        return class_804.field_4284;
    }

    // ------------------------------------------------------------------ sprites

    /** SpriteResourceLoader.create(Collection): 1.21.9 takes a Set of the same section types (order kept) */
    public static Set<class_7677<?>> sections(Collection<class_7677<?>> sections) {
        return sections instanceof Set<class_7677<?>> s ? s : new LinkedHashSet<>(sections);
    }

    /** SpriteLoader.DEFAULT_METADATA_SECTIONS (1.21.8: Set.of(AnimationMetadataSection.TYPE)) */
    public static Set<class_7677<?>> field_45632() {
        return Set.of(class_1079.field_55537);
    }

    // ------------------------------------------------------------------ RenderTarget.viewWidth / viewHeight

    /**
     * 1.21.8's viewWidth / viewHeight: written with width / height by createBuffers (1.21.8's only writer) and read by
     * nothing of the game. A 1.21.8 write is kept beside the target until the target's size changes (createBuffers
     * set both again); otherwise the value is the size.
     */
    private static final Map<class_276, int[]> VIEW = new WeakHashMap<>();

    public static synchronized int field_1480(class_276 target) {
        int[] v = VIEW.get(target);
        return v != null && v[1] == target.field_1482 && v[2] == target.field_1481 ? v[0] : target.field_1482;
    }

    public static synchronized void field_1480(class_276 target, int value) {
        view(target)[0] = value;
    }

    public static synchronized int field_1477(class_276 target) {
        int[] v = VIEW.get(target);
        return v != null && v[1] == target.field_1482 && v[2] == target.field_1481 ? v[3] : target.field_1481;
    }

    public static synchronized void field_1477(class_276 target, int value) {
        view(target)[3] = value;
    }

    /** {viewWidth, width at the write, height at the write, viewHeight} */
    private static int[] view(class_276 target) {
        int[] v = VIEW.get(target);
        if (v == null || v[1] != target.field_1482 || v[2] != target.field_1481) {
            v = new int[] {target.field_1482, target.field_1482, target.field_1481, target.field_1481};
            VIEW.put(target, v);
        }
        return v;
    }

    // ------------------------------------------------------------------ Screen.NarratableSearchResult (a record on 1.21.9)

    public static class_6379 field_33825(class_437.class_6390 result) {
        return result.comp_4463();
    }

    public static int field_33826(class_437.class_6390 result) {
        return result.comp_4464();
    }

    public static class_6379.class_6380 field_33827(class_437.class_6390 result) {
        return result.comp_4465();
    }

    // ------------------------------------------------------------------ ArmedModel.translateToHand

    /**
     * ArmedModel.translateToHand(arm, pose): 1.21.9 also passes the entity's render state. 1.21.8's models read none
     * (HumanoidModel, IllagerModel, AllayModel, VexModel translate by their own parts, as 1.21.9's still do); the one
     * 1.21.9 model that reads it (CopperGolemModel, new in 1.21.9) gets a fresh state of its kind
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void method_2803(net.minecraft.class_3881 model, net.minecraft.class_1306 arm, class_4587 pose) {
        model.method_2803(model instanceof net.minecraft.class_11651 ? new net.minecraft.class_11679() : null, arm, pose);
    }

    public static void method_2803(net.minecraft.class_572<?> model, net.minecraft.class_1306 arm, class_4587 pose) {
        method_2803((net.minecraft.class_3881) model, arm, pose);
    }

    public static void method_2803(net.minecraft.class_575<?> model, net.minecraft.class_1306 arm, class_4587 pose) {
        method_2803((net.minecraft.class_3881) model, arm, pose);
    }

    public static void method_2803(net.minecraft.class_617 model, net.minecraft.class_1306 arm, class_4587 pose) {
        method_2803((net.minecraft.class_3881) model, arm, pose);
    }

    public static void method_2803(net.minecraft.class_7308 model, net.minecraft.class_1306 arm, class_4587 pose) {
        method_2803((net.minecraft.class_3881) model, arm, pose);
    }

    // ------------------------------------------------------------------ override bridges

    /** a 1.21.8 SkyRenderer.renderSunriseAndSunset(pose, buffers, angle, color) override */
    public interface LegacySunrise {
        void method_62306(class_4587 pose, net.minecraft.class_4597.class_4598 buffers, float angle, int color);
    }

    /**
     * 1.21.9 draws the fan itself; a 1.21.8 override drew into the buffer source LevelRenderer gave it -- the game's
     * main one (renderBuffers().bufferSource()), ended once the sky's parts were drawn -- so it gets that and the
     * batch is ended after it
     */
    public static void cbSunrise(LegacySunrise self, class_4587 pose, float angle, int color) {
        net.minecraft.class_4597.class_4598 buffers = class_310.method_1551().method_22940().method_23000();
        self.method_62306(pose, buffers, angle, color);
        buffers.method_22993();
    }

    /** a 1.21.8 RecipeBookComponent.hasClickedOutside(x, y, left, top, width, height, button) override */
    public interface LegacyClickedOutside {
        boolean method_2598(double x, double y, int left, int top, int width, int height, int button);
    }

    /** 1.21.9 no longer passes the button; it is the click being handled (CbInput.currentButton) */
    public static boolean cbClickedOutside(LegacyClickedOutside self, double x, double y, int left, int top, int width, int height) {
        return self.method_2598(x, y, left, top, width, height, CbInput.currentButton());
    }
}
