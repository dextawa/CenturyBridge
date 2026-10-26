package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.class_10034;
import net.minecraft.class_11659;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3302;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_4720;
import net.minecraft.class_572;

/**
 * Fabric API client members 1.21.9's Fabric API (0.134) reshaped or dropped, for 1.21.8 code (javap of both releases'
 * modules: fabric-rendering-v1, fabric-model-loading-api-v1).
 */
@Environment(EnvType.CLIENT)
public final class CbFapi {
    private CbFapi() {
    }

    // ------------------------------------------------------------------ fabric-rendering-v1: FabricRenderState

    /** a render state class of 0.136 code overriding getDataOrDefault (0.134 dropped the default method) */
    public interface LegacyDataOrDefault {
        Object getDataOrDefault(RenderStateDataKey<?> key, Object defaultValue);
    }

    /** FabricRenderState.getDataOrDefault(key, default) for 0.136 code: the state's own override, else 0.136's default body */
    @SuppressWarnings("unchecked")
    public static <T> T getDataOrDefault(FabricRenderState state, RenderStateDataKey<T> key, T defaultValue) {
        if (state instanceof LegacyDataOrDefault own) {
            return (T) own.getDataOrDefault(key, defaultValue);
        }
        return getDataOrDefaultBase(state, key, defaultValue);
    }

    /** 0.136's default body (getData, else the default): what an override's super call reaches */
    public static <T> T getDataOrDefaultBase(FabricRenderState state, RenderStateDataKey<T> key, T defaultValue) {
        T value = state.getData(key);
        return value != null ? value : defaultValue;
    }

    // ------------------------------------------------------------------ fabric-rendering-v1: ArmorRenderer

    /** ArmorRenderer.renderPart(pose, buffers, light, stack, model, texture): 0.136's body */
    public static void renderPart(class_4587 pose, class_4597 buffers, int light, class_1799 stack, class_3879 model, class_2960 texture) {
        class_1921 type = class_1921.method_25448(texture);
        class_4588 consumer = stack.method_7958()
            ? class_4720.method_24037(buffers.method_73477(class_1921.method_27949()), buffers.method_73477(type))
            : buffers.method_73477(type);
        model.method_62100(pose, consumer, light, class_4608.field_21444, -1);
    }

    /** an ArmorRenderer in 0.136's shape (draws into buffers) */
    public interface LegacyArmorRenderer {
        void render(class_4587 pose, class_4597 buffers, class_1799 stack, class_10034 state, class_1304 slot, int light,
                    class_572<class_10034> model);
    }

    /** 0.134's render(pose, collector, ..) over a 0.136 renderer: its buffers are a capture of the collector, submitted after it */
    public static void cbArmorRender(LegacyArmorRenderer self, class_4587 pose, class_11659 collector, class_1799 stack, class_10034 state,
                                     class_1304 slot, int light, class_572<class_10034> model) {
        CbSubmit.Capture buffers = CbSubmit.capture(collector, CbSubmit.currentCamera());
        self.render(pose, buffers, stack, state, slot, light, model);
        buffers.flush();
    }

    public static ArmorRenderer adaptArmorRenderer(LegacyArmorRenderer fn) {
        return (pose, collector, stack, state, slot, light, model) -> cbArmorRender(fn, pose, collector, stack, state, slot, light, model);
    }

    // ------------------------------------------------------------------ fabric-model-loading-api-v1: DataLoader

    /** a PreparableModelLoadingPlugin.DataLoader in 0.136's shape (load(manager, executor)) */
    public interface LegacyDataLoader<T> {
        CompletableFuture<T> load(class_3300 manager, Executor executor);
    }

    /** 0.134 hands the loader the reload's SharedState; a 0.136 loader had the resource manager it holds */
    public static class_3300 manager(class_3302.class_11558 state) {
        return state.method_72361();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static PreparableModelLoadingPlugin.DataLoader adaptDataLoader(LegacyDataLoader fn) {
        return (state, executor) -> fn.load(manager(state), executor);
    }

    // ------------------------------------------------------------------ fabric-particles-v1: FabricSpriteProvider

    /** SpriteSet.first() that a 0.136 FabricSpriteProvider lacks: its sprite for age 0 (CbParticles.first) */
    public static net.minecraft.class_1058 first(net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider sprites) {
        return CbParticles.first(sprites);
    }
}
