package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's Model.renderToBuffer over 1.21.2's final one.
 *
 * 1.21.1: Model.renderToBuffer(PoseStack, VertexConsumer, light, overlay, color) was abstract; every model drew its
 * own parts (HierarchicalModel its root(), ListModel its parts(), AgeableListModel its head and body parts with the
 * baby scaling, a mod's EntityModel subclass whatever it drew). 1.21.2: a model is built around one root part, and the
 * final renderToBuffer draws it. A 1.21.1 model class that draws itself (one that declares renderToBuffer -- a mod
 * model, or the model bases put back as facades, HierarchicalModel & co.) is built with an empty root
 * ({@link #emptyRoot}: 1.21.1 models had no root, and nothing it holds then reaches 1.21.2's root-wide work -- resetPose
 * touches no part, as 1.21.1 never reset one), and 1.21.2's renderToBuffer hands the call to its own
 * (CbRenderModelMixin, {@link #drawsItself}).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderModelBase {

    private static final String RENDER = "method_2828";

    private static final ClassValue<Boolean> DRAWS_ITSELF = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    c.getDeclaredMethod(RENDER, net.minecraft.class_4587.class, net.minecraft.class_4588.class, int.class, int.class, int.class);
                    // the first declaration up the hierarchy: Model's own (CbRenderModelMixin's) draws the 1.21.2 root
                    return c != net.minecraft.class_3879.class;
                } catch (NoSuchMethodException e) {
                    // keep climbing
                }
            }
            return false;
        }
    };

    private CbRenderModelBase() {
    }

    /** a root part with no cubes and no children */
    public static net.minecraft.class_630 emptyRoot() {
        return new net.minecraft.class_630(java.util.List.of(), java.util.Map.of());
    }

    /** whether a model's class (or a class above it, below Model) declares 1.21.1's renderToBuffer */
    public static boolean drawsItself(Object model) {
        return DRAWS_ITSELF.get(model.getClass());
    }

    /** 1.21.1 Model.renderToBuffer, publicly (CbRenderModelMixin) */
    public interface Legacy {
        void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color);
    }

    /**
     * 1.21.1 KeyframeAnimations.animate(HierarchicalModel, definition, time, scale, cache) (staticRedirects): 1.21.2's
     * takes any Model, and finds the parts through the model's getAnyDescendantWithName, which the HierarchicalModel
     * facade answers from root() as 1.21.1's animate did.
     */
    public static void method_41827(top.dext.centurybridge.step.v1_21_2.facade.HierarchicalModel<?> model, net.minecraft.class_7184 definition,
                                    long time, float scale, org.joml.Vector3f cache) {
        net.minecraft.class_7187.method_41827(model, definition, time, scale, cache);
    }
}
