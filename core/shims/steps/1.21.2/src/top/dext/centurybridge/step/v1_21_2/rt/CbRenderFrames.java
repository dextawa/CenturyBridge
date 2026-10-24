package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * frameDefaults of the area's frames hooks (mechanical.json): what a 1.21.1 override's rewritten super call passes for
 * an argument 1.21.2 added when the override was entered directly (no frame) -- the value 1.21.2's own caller passes
 * there, which is what the 1.21.1 body computed itself or took from the same place:
 * - shouldShowName(entity, distanceToCameraSq): 1.21.1's LivingEntityRenderer.shouldShowName computed
 *   entityRenderDispatcher.distanceToSqr(entity) itself; 1.21.2's extraction passes the same;
 * - SectionOcclusionGraph.update(..., loadedEmptySections): 1.21.2's LevelRenderer passes the client chunk cache's
 *   loaded empty sections;
 * - LevelRenderer.renderLevel(allocator, ...): 1.21.2's GameRenderer passes its cross-frame resource pool;
 * - DebugRenderer.render(pose, frustum, ...): the level renderer's culling frustum, as 1.21.2's level render passes it.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderFrames {

    /** GameRenderer's resource pool (CbRenderGameMixin) */
    public interface ResourcePool {
        net.minecraft.class_9922 cb$resourcePool();
    }

    private CbRenderFrames() {
    }

    public static double distanceToCamera(net.minecraft.class_897<?, ?> renderer, net.minecraft.class_1297 entity) {
        return net.minecraft.class_310.method_1551().method_1561().method_23168(entity);
    }

    /** the same for each renderer's own shouldShowName erasure (a frame default takes the old shape's exact arguments) */
    public static double distanceToCamera(net.minecraft.class_897<?, ?> renderer, net.minecraft.class_1309 entity) {
        return distanceToCamera(renderer, (net.minecraft.class_1297) entity);
    }

    public static double distanceToCamera(net.minecraft.class_897<?, ?> renderer, net.minecraft.class_1308 entity) {
        return distanceToCamera(renderer, (net.minecraft.class_1297) entity);
    }

    public static double distanceToCamera(net.minecraft.class_897<?, ?> renderer, net.minecraft.class_1531 entity) {
        return distanceToCamera(renderer, (net.minecraft.class_1297) entity);
    }

    public static double distanceToCamera(net.minecraft.class_897<?, ?> renderer, net.minecraft.class_1533 entity) {
        return distanceToCamera(renderer, (net.minecraft.class_1297) entity);
    }

    public static it.unimi.dsi.fastutil.longs.LongOpenHashSet loadedEmptySections(net.minecraft.class_8679 graph, boolean smartCull,
                                                                                    net.minecraft.class_4184 camera, net.minecraft.class_4604 frustum,
                                                                                    java.util.List<?> visible) {
        net.minecraft.class_638 level = net.minecraft.class_310.method_1551().field_1687;
        return level == null ? new it.unimi.dsi.fastutil.longs.LongOpenHashSet() : level.method_2935().method_62890();
    }

    public static net.minecraft.class_9922 resourcePool(net.minecraft.class_761 levelRenderer, net.minecraft.class_9779 deltaTracker, boolean renderBlockOutline,
                                                        net.minecraft.class_4184 camera, net.minecraft.class_757 gameRenderer, net.minecraft.class_765 lightTexture,
                                                        org.joml.Matrix4f frustumMatrix, org.joml.Matrix4f projectionMatrix) {
        return ((ResourcePool) gameRenderer).cb$resourcePool();
    }

    public static net.minecraft.class_4604 debugFrustum(net.minecraft.class_863 debugRenderer, net.minecraft.class_4587 pose,
                                                        net.minecraft.class_4597.class_4598 buffers, double x, double y, double z) {
        return net.minecraft.class_310.method_1551().field_1769.method_62222();
    }
}
