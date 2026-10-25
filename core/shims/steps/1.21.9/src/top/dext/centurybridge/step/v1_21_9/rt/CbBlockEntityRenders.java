package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_11683;
import net.minecraft.class_11954;
import net.minecraft.class_12075;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_827;

/**
 * 1.21.8's BlockEntityRenderer (render(blockEntity, partialTick, poseStack, buffers, light, overlay, cameraPos)) on
 * 1.21.9's (createRenderState / extractRenderState / submit).
 *
 * 1.21.9's BlockEntityRenderDispatcher extracts a render state per block entity and frame; the layer stamps what
 * 1.21.8's render was handed -- the block entity, the partial tick, the camera position -- on that state
 * (CbBlockEntityStateMixin, CbBlockEntityDispatchMixin). A 1.21.8 renderer's render is reached from 1.21.9's
 * submit (overrideHooks, bridge mode) with those, the state's light (the light 1.21.8's dispatcher computed the
 * same way) and no overlay (1.21.8's dispatcher passed OverlayTexture.NO_OVERLAY), drawing into a CbSubmit.Capture
 * submitted when it returns. A renderer implementing the interface itself gets 1.21.9's createRenderState (a
 * plain BlockEntityRenderState, abstractFills); one extending a vanilla renderer keeps the vanilla state, and its
 * super.render(..) runs the vanilla renderer's own extraction and submission (superBridge).
 */
@Environment(EnvType.CLIENT)
public final class CbBlockEntityRenders {

    private CbBlockEntityRenders() {
    }

    /** what the layer keeps on a block entity render state (CbBlockEntityStateMixin) */
    public interface Info {
        void cb$legacy(class_2586 blockEntity, float partialTick, class_243 cameraPos);

        class_2586 cb$blockEntity();

        float cb$partialTick();

        class_243 cb$cameraPos();
    }

    /** a 1.21.8 block entity renderer's own render */
    public interface Legacy {
        void method_3569(class_2586 blockEntity, float partialTick, class_4587 pose, class_4597 buffers, int light, int overlay,
                         class_243 cameraPos);
    }

    /** the vanilla renderer above a 1.21.8 subclass (accessors of its super implementations) */
    public interface Super {
        class_11954 cb$super$method_74335();

        void cb$super$method_74331(class_2586 blockEntity, class_11954 state, float partialTick, class_243 cameraPos,
                                   class_11683.class_11792 crumbling);

        void cb$super$method_3569(class_11954 state, class_4587 pose, class_11659 collector, class_12075 camera);
    }

    /** createRenderState() for a renderer implementing the interface itself */
    public static class_11954 cbCreate(class_827<?, ?> self) {
        return new class_11954();
    }

    /** submit(state, poseStack, collector, camera) -> the old render */
    public static void cbSubmit(Legacy self, class_11954 state, class_4587 pose, class_11659 collector, class_12075 camera) {
        Info info = (Info) state;
        if (info.cb$blockEntity() == null) {
            return;
        }
        CbSubmit.Capture cap = CbSubmit.capture(collector, camera);
        try {
            self.method_3569(info.cb$blockEntity(), info.cb$partialTick(), pose, cap, state.field_62676, class_4608.field_21444,
                info.cb$cameraPos());
        } finally {
            cap.flush();
        }
    }

    /** a 1.21.8 subclass's super.render(..): the vanilla renderer extracts its own state and submits it */
    public static void cbSuperRender(Super self, class_2586 blockEntity, float partialTick, class_4587 pose, class_4597 buffers,
                                     int light, int overlay, class_243 cameraPos) {
        class_11954 state = self.cb$super$method_74335();
        self.cb$super$method_74331(blockEntity, state, partialTick, cameraPos, null);
        state.field_62676 = light;
        ((Info) state).cb$legacy(blockEntity, partialTick, cameraPos);
        self.cb$super$method_3569(state, pose, CbSubmit.collector(buffers), CbSubmit.camera(buffers));
    }

    // ------------------------------------------------------------------ calls of render(..) on a renderer

    /** renderer.render(blockEntity, ..): the renderer extracts its state and submits it into the caller's buffers */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_3569(class_827 renderer, class_2586 blockEntity, float partialTick, class_4587 pose, class_4597 buffers,
                                   int light, int overlay, class_243 cameraPos) {
        class_11954 state = renderer.method_74335();
        renderer.method_74331(blockEntity, state, partialTick, cameraPos, null);
        state.field_62676 = light;
        ((Info) state).cb$legacy(blockEntity, partialTick, cameraPos);
        renderer.method_3569(state, pose, CbSubmit.collector(buffers), CbSubmit.camera(buffers));
    }

    public static void method_3569(net.minecraft.class_822 renderer, class_2586 blockEntity, float partialTick, class_4587 pose,
                                   class_4597 buffers, int light, int overlay, class_243 cameraPos) {
        method_3569((class_827) renderer, blockEntity, partialTick, pose, buffers, light, overlay, cameraPos);
    }

    public static void method_3569(net.minecraft.class_826 renderer, class_2586 blockEntity, float partialTick, class_4587 pose,
                                   class_4597 buffers, int light, int overlay, class_243 cameraPos) {
        method_3569((class_827) renderer, blockEntity, partialTick, pose, buffers, light, overlay, cameraPos);
    }

    public static void method_3569(net.minecraft.class_838 renderer, class_2586 blockEntity, float partialTick, class_4587 pose,
                                   class_4597 buffers, int light, int overlay, class_243 cameraPos) {
        method_3569((class_827) renderer, blockEntity, partialTick, pose, buffers, light, overlay, cameraPos);
    }

    // ------------------------------------------------------------------ lambdas (1.21.8's BlockEntityRenderer had one abstract method)

    /** a 1.21.8 renderer written as a lambda or method reference */
    @FunctionalInterface
    public interface LegacyFn {
        void method_3569(class_2586 blockEntity, float partialTick, class_4587 pose, class_4597 buffers, int light, int overlay,
                         class_243 cameraPos);
    }

    @SuppressWarnings("rawtypes")
    public static class_827 adapt(LegacyFn fn) {
        return new Lambda(fn);
    }

    @SuppressWarnings("rawtypes")
    static final class Lambda implements class_827, Legacy {
        private final LegacyFn fn;

        Lambda(LegacyFn fn) {
            this.fn = fn;
        }

        @Override
        public class_11954 method_74335() {
            return new class_11954();
        }

        @Override
        public void method_3569(class_11954 state, class_4587 pose, class_11659 collector, class_12075 camera) {
            cbSubmit(this, state, pose, collector, camera);
        }

        @Override
        public void method_3569(class_2586 blockEntity, float partialTick, class_4587 pose, class_4597 buffers, int light, int overlay,
                                class_243 cameraPos) {
            fn.method_3569(blockEntity, partialTick, pose, buffers, light, overlay, cameraPos);
        }
    }
}
