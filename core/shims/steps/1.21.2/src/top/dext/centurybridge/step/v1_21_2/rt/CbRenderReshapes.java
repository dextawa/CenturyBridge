package top.dext.centurybridge.step.v1_21_2.rt;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Values for the callReshapes of private client members 1.21.2 reshaped (s7render.json): what 1.21.2's shape takes
 * that 1.21.1's callers did not pass, or a 1.21.1 value converted to the form 1.21.2 takes. Mods reach these
 * members through invokers; MixinBridge follows the same entries for selectors and handler arguments.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderReshapes {
    private CbRenderReshapes() {
    }

    /** GameRenderer.getFov(camera, partialTick, useFovSetting): 1.21.1 answered a double, 1.21.2 a float */
    public static double fovDouble(float fov) {
        return fov;
    }

    /**
     * ItemFrameRenderer.getFrameModelResourceLoc / getLightVal: 1.21.1 took the frame and tested
     * frame.getType() == EntityType.GLOW_ITEM_FRAME, 1.21.2 takes that test's answer (the render state's isGlowFrame)
     */
    public static boolean glowFrame(net.minecraft.class_1533 frame) {
        return frame.method_5864() == net.minecraft.class_1299.field_28401;
    }

    /**
     * RenderType's clouds builder: 1.21.1's createClouds(colorOnly) drew with NO_CULL; 1.21.2's takes a second flag
     * that picks CULL when true and NO_CULL when false
     */
    public static boolean cloudsCull() {
        return false;
    }

    /**
     * Font's private drawInternal / renderText: 1.21.2 added a last flag (the depth direction of the background and
     * shadow layers) that its 1.21.1-shaped public drawInBatch passes as true, as 1.21.1 drew
     */
    public static boolean fontDefaultDepth() {
        return true;
    }

    /**
     * LevelRenderer.renderHitOutline: 1.21.2 takes the outline colour; 1.21.1 drew renderShape(.., 0, 0, 0, 0.4f),
     * black at alpha 0.4 = ARGB 0x66000000
     */
    public static int hitOutlineColor() {
        return 0x66000000;
    }

    /**
     * Frustum.cubeInFrustum: 1.21.1 answered FrustumIntersection.testAab (inside or intersecting), 1.21.2 answers
     * intersectAab -- INSIDE (-2), INTERSECT (-1), or the index of the plane the box is outside of (>= 0)
     */
    public static boolean inFrustum(int intersection) {
        return intersection < 0;
    }

    /**
     * SectionOcclusionGraph's private search helpers take the client's loaded empty sections in 1.21.2 and skip
     * them; 1.21.1 skipped none, so the set is empty
     */
    public static LongOpenHashSet noEmptySections() {
        return new LongOpenHashSet();
    }

    /** Window's fullscreen toggle passes 1.21.2's Tracy frame capture on to updateDisplay; 1.21.1 had none (null is skipped) */
    public static net.minecraft.class_10219 noFrameCapture() {
        return null;
    }

    /**
     * EntityRenderer.shouldShowName(entity) and its overrides: 1.21.2 takes the entity's squared distance to the
     * camera as well, which its extractRenderState computes as EntityRenderDispatcher.distanceToSqr(entity) -- so
     * does a 1.21.1 call
     */
    public static double distanceToCameraSq(net.minecraft.class_1297 entity) {
        return net.minecraft.class_310.method_1551().method_1561().method_23168(entity);
    }

    /**
     * EntityRenderDispatcher.render(entity, x, y, z, yRot, partialTick, poseStack, buffers, light) (instanceRedirects):
     * 1.21.2's public render(entity, x, y, z, partialTick, poseStack, buffers, light) is the same draw; it takes no
     * yaw because each renderer's extractRenderState reads the entity's own rotation (1.21.1's living renderers
     * already ignored the passed yaw for the body rotation)
     */
    public static void method_3954(net.minecraft.class_898 dispatcher, net.minecraft.class_1297 entity, double x, double y, double z,
                                   float yRot, float partialTick, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light) {
        dispatcher.method_62424(entity, x, y, z, partialTick, poseStack, buffers, light);
    }

    /**
     * AnimationUtils.animateCrossbowCharge(rightArm, leftArm, entity, rightHanded): 1.21.1 read the charge duration
     * (CrossbowItem.getChargeDuration(entity.getUseItem(), entity)) and the ticks using the item from the entity;
     * 1.21.2 takes both, as HumanoidMobRenderer extracts them
     */
    public static float crossbowChargeDuration(net.minecraft.class_1309 entity) {
        return (float) net.minecraft.class_1764.method_7775(entity.method_6030(), entity);
    }

    public static int ticksUsingItem(net.minecraft.class_1309 entity) {
        return entity.method_6048();
    }

    /** AnimationUtils.swingWeaponDown(rightArm, leftArm, mob, ..): 1.21.1 read mob.getMainArm(), 1.21.2 takes the arm */
    public static net.minecraft.class_1306 mainArm(net.minecraft.class_1308 mob) {
        return mob.method_6068();
    }

    /** DebugRenderer.render takes the culling frustum in 1.21.2: the level renderer's, as its level render passes it */
    public static net.minecraft.class_4604 levelFrustum() {
        return net.minecraft.class_310.method_1551().field_1769.method_62222();
    }

    /** the same for each renderer's own shouldShowName erasure (a converter takes the old argument's exact type) */
    public static double distanceToCameraSq(net.minecraft.class_1309 entity) {
        return distanceToCameraSq((net.minecraft.class_1297) entity);
    }

    public static double distanceToCameraSq(net.minecraft.class_1308 entity) {
        return distanceToCameraSq((net.minecraft.class_1297) entity);
    }

    public static double distanceToCameraSq(net.minecraft.class_1531 entity) {
        return distanceToCameraSq((net.minecraft.class_1297) entity);
    }

    public static double distanceToCameraSq(net.minecraft.class_1533 entity) {
        return distanceToCameraSq((net.minecraft.class_1297) entity);
    }

    /**
     * Material.buffer / BannerRenderer.renderPatterns: 1.21.2 takes whether the foil is an item's (glint) or an
     * entity's (entity glint); 1.21.1's Material.buffer always drew the item glint (getFoilBufferDirect(.., true, foil))
     */
    public static boolean itemGlint() {
        return true;
    }

    /**
     * new MapRenderer(textureManager, decorationTextures): 1.21.1's renderer kept its own map textures on that texture
     * manager; 1.21.2's takes a MapTextureManager, so it gets one of its own on the same texture manager
     */
    public static net.minecraft.class_10093 mapTextures(net.minecraft.class_1060 textureManager) {
        return new net.minecraft.class_10093(textureManager);
    }

    /**
     * new UndeadHorseRenderer(context, layer): 1.21.1 picked the zombie or skeleton horse texture from the entity's
     * type when drawing; 1.21.2's constructor takes the choice -- the skeleton horse's layer is the skeleton one
     */
    public static boolean skeletonHorse(net.minecraft.class_5601 layer) {
        return net.minecraft.class_5602.field_27600.equals(layer);
    }
}
