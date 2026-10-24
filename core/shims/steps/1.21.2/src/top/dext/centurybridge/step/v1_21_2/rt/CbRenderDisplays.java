package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's DisplayRenderer sub-state shape over 1.21.2's render state.
 *
 * <p>1.21.1: DisplayRenderer.render asked the subclass for the display's sub-state (getSubState(display): the block /
 * item / text display's blockRenderState() / itemRenderState() / textRenderState()) and drew with
 * renderInner(display, subState, poseStack, buffers, light, interpolationProgress). 1.21.2 copies that same sub-state into
 * the display's render state when extracting it (BlockDisplayEntityRenderState.blockRenderState, ...) and draws with
 * renderInner(state, poseStack, buffers, light, interpolationProgress); the last argument is the same value in both
 * (Display.calculateInterpolationProgress(partialTick), state.interpolationProgress). Compared in both jars.
 *
 * <p>The 1.21.1 methods are put back on the vanilla renderers (CbRenderDisplayMixin and its subclass mixins): getSubState
 * answers 1.21.1's accessor, renderInner runs 1.21.2's on a state of the display with the given sub-state written in.
 * A mod renderer's 1.21.1 renderInner override answers 1.21.2's calls (overrideHooks, guarded): it gets the display the
 * state was extracted from and its sub-state from getSubState, as 1.21.1's render handed them.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderDisplays {

    /** DisplayRenderer's 1.21.1 root members (CbRenderDisplayMixin) */
    public interface Legacy {
        Object method_49921(net.minecraft.class_8113 display);

        void method_49052(net.minecraft.class_8113 display, Object subState, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                          int light, float interpolationProgress);
    }

    /** the 1.21.1 typed renderInner of a mod block / item / text display renderer */
    public interface LegacyBlock {
        void method_49054(net.minecraft.class_8113.class_8115 display, net.minecraft.class_8113.class_8115.class_8225 subState, net.minecraft.class_4587 poseStack,
                          net.minecraft.class_4597 buffers, int light, float interpolationProgress);
    }

    public interface LegacyItem {
        void method_49055(net.minecraft.class_8113.class_8122 display, net.minecraft.class_8113.class_8122.class_8226 subState, net.minecraft.class_4587 poseStack,
                          net.minecraft.class_4597 buffers, int light, float interpolationProgress);
    }

    public interface LegacyText {
        void method_49056(net.minecraft.class_8113.class_8123 display, net.minecraft.class_8113.class_8123.class_8230 subState, net.minecraft.class_4587 poseStack,
                          net.minecraft.class_4597 buffers, int light, float interpolationProgress);
    }

    private CbRenderDisplays() {
    }

    /** a state of the display (the renderer's own when it holds this extraction), the given sub-state written in */
    public static net.minecraft.class_10011 state(net.minecraft.class_8138<?, ?, ?> renderer, net.minecraft.class_8113 display, Object subState) {
        net.minecraft.class_10011 state = (net.minecraft.class_10011) CbRenderLiving.stateOf(renderer, display, CbRenderTex.partialTick());
        if (state instanceof net.minecraft.class_10003 block && subState instanceof net.minecraft.class_8113.class_8115.class_8225 s) {
            block.field_53273 = s;
        } else if (state instanceof net.minecraft.class_10038 item && subState instanceof net.minecraft.class_8113.class_8122.class_8226 s) {
            item.field_53433 = s;
        } else if (state instanceof net.minecraft.class_10071 text && subState instanceof net.minecraft.class_8113.class_8123.class_8230 s) {
            text.field_53588 = s;
        }
        return state;
    }

    private static net.minecraft.class_8113 display(Object state) {
        return CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_8113 d ? d : null;
    }

    // ---- 1.21.2's renderInner on a mod renderer: its 1.21.1 override, as 1.21.1's render called it ----

    public static void renderInner(Legacy self, net.minecraft.class_10011 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light, float interpolationProgress) {
        net.minecraft.class_8113 display = display(state);
        if (display == null) {
            throw new IllegalStateException("1.21.1 DisplayRenderer.renderInner: a render state no display was extracted into");
        }
        self.method_49052(display, self.method_49921(display), poseStack, buffers, light, interpolationProgress);
    }

    public static void renderBlock(LegacyBlock self, net.minecraft.class_10003 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light, float interpolationProgress) {
        if (display(state) instanceof net.minecraft.class_8113.class_8115 d) {
            self.method_49054(d, (net.minecraft.class_8113.class_8115.class_8225) ((Legacy) self).method_49921(d), poseStack, buffers, light, interpolationProgress);
        }
    }

    public static void renderItem(LegacyItem self, net.minecraft.class_10038 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                  int light, float interpolationProgress) {
        if (display(state) instanceof net.minecraft.class_8113.class_8122 d) {
            self.method_49055(d, (net.minecraft.class_8113.class_8122.class_8226) ((Legacy) self).method_49921(d), poseStack, buffers, light, interpolationProgress);
        }
    }

    public static void renderText(LegacyText self, net.minecraft.class_10071 state, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                  int light, float interpolationProgress) {
        if (display(state) instanceof net.minecraft.class_8113.class_8123 d) {
            self.method_49056(d, (net.minecraft.class_8113.class_8123.class_8230) ((Legacy) self).method_49921(d), poseStack, buffers, light, interpolationProgress);
        }
    }
}
