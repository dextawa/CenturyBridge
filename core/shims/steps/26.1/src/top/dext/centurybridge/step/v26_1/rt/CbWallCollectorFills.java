package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;

/**
 * What 26.1's submit node collectors newly demand of a 1.21.11 implementation (abstractFills):
 * <ul>
 * <li>OrderedSubmitNodeCollector.submitBlockModel(pose, renderType, parts, tints, light, overlay, outline): 1.21.11's
 *     took one block model and one colour (r, g, b) for its tinted quads. The implementation's own 1.21.11 method gets
 *     the parts as a block model and the colour of tint index 0 (white without tints).</li>
 * <li>OrderedSubmitNodeCollector.submitBreakingBlockModel(pose, model, seed, progress): 1.21.11 drew the block breaking
 *     overlay straight into the game's crumbling buffers, never through a collector; 26.1 submits it. It goes to the
 *     game's own submit node storage, which draws it as 26.1 draws every breaking overlay.</li>
 * <li>SubmitNodeCollector.ParticleGroupRenderer.isEmpty(): 26.1 skips empty groups; 1.21.11 prepared and drew every
 *     group, so a 1.21.11 group is never empty.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallCollectorFills {

    private static final MethodType OLD_SUBMIT_BLOCK_MODEL = MethodType.methodType(void.class, PoseStack.class, RenderType.class, BlockStateModel.class,
        float.class, float.class, float.class, int.class, int.class, int.class);

    private static final ClassValue<MethodHandle> OLD = new ClassValue<>() {
        @Override
        protected MethodHandle computeValue(Class<?> type) {
            for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                try {
                    java.lang.reflect.Method m = c.getDeclaredMethod("submitBlockModel", OLD_SUBMIT_BLOCK_MODEL.parameterArray());
                    m.setAccessible(true);
                    return MethodHandles.lookup().unreflect(m).asType(OLD_SUBMIT_BLOCK_MODEL.insertParameterTypes(0, Object.class));
                } catch (NoSuchMethodException e) {
                    // up the hierarchy
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
            throw new IllegalStateException(type.getName() + " implements 1.21.11's OrderedSubmitNodeCollector without its submitBlockModel");
        }
    };

    private CbWallCollectorFills() {
    }

    /** 26.1 submitBlockModel(pose, renderType, parts, tints, light, overlay, outline) on a 1.21.11 implementation */
    public static void submitBlockModel(OrderedSubmitNodeCollector collector, PoseStack pose, RenderType renderType, List<BlockStateModelPart> parts,
                                        int[] tints, int light, int overlay, int outline) {
        int color = tints != null && tints.length > 0 ? tints[0] : -1;
        try {
            OLD.get(collector.getClass()).invokeExact((Object) collector, pose, renderType, (BlockStateModel) new Parts(parts),
                ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), light, overlay, outline);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /** 26.1 submitBreakingBlockModel(pose, model, seed, progress) on a 1.21.11 implementation: the game's storage draws it */
    public static void submitBreakingBlockModel(OrderedSubmitNodeCollector collector, PoseStack pose, BlockStateModel model, long seed, int progress) {
        Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher().getSubmitNodeStorage().submitBreakingBlockModel(pose, model, seed, progress);
    }

    /** 26.1 ParticleGroupRenderer.isEmpty() of a 1.21.11 group: never (1.21.11 drew every group) */
    public static boolean isEmpty(SubmitNodeCollector.ParticleGroupRenderer group) {
        return false;
    }

    /** the parts 26.1 submits, as the one block model 1.21.11's submitBlockModel took */
    private record Parts(List<BlockStateModelPart> parts) implements BlockStateModel {
        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> out) {
            out.addAll(parts);
        }

        @Override
        public Material.Baked particleMaterial() {
            return parts.isEmpty() ? null : parts.get(0).particleMaterial();
        }

        @Override
        public int materialFlags() {
            int flags = 0;
            for (BlockStateModelPart p : parts) {
                flags |= p.materialFlags();
            }
            return flags;
        }
    }
}
