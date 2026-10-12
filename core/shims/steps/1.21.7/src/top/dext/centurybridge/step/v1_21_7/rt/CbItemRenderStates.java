package top.dext.centurybridge.step.v1_21_7.rt;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.6's ItemStackRenderState on 1.21.7 (read off both releases' bytecode).
 *
 * <p>In 1.21.6 every state recorded its model identity: appendModelIdentityElement added to a list,
 * getModelIdentity() answered it, clearModelIdentity() emptied it, and clear() left it alone. 1.21.7 moved
 * that into a subclass, TrackingItemStackRenderState (getModelIdentity() there; no clearing), the only kind
 * its GUI draws, and made the base class drop what is appended. So:
 * <ul>
 * <li>a state a 1.21.6 mod makes ({@code new ItemStackRenderState()}, ctorRedirects) is a tracking state,
 *     and a 1.21.6 subclass extends TrackingItemStackRenderState (superRenames): their identity is kept
 *     exactly as 1.21.6 kept it, and they can be handed to 1.21.7's GUI item state;</li>
 * <li>getModelIdentity() / clearModelIdentity() are back on ItemStackRenderState (mixins): a tracking
 *     state answers and clears its own list. A plain state -- one 1.21.7 itself makes outside the GUI
 *     (entity and other world render states) -- records its identity again, but it is emptied whenever
 *     the state is cleared, which every update begins with: 1.21.6 never cleared those states, so their
 *     lists grew without bound, a leak 1.21.7 removed; here such a state answers the identity of its
 *     latest update instead of the growing history;</li>
 * <li>1.21.6's GuiItemRenderState took any state, 1.21.7's takes a tracking one ({@link #tracking}): a
 *     tracking state goes as it is, a plain one as a tracking view of itself ({@link View}).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbItemRenderStates {

    private CbItemRenderStates() {
    }

    /** {@code new ItemStackRenderState()} of a 1.21.6 mod: a state that keeps its model identity, as every 1.21.6 one did */
    public static net.minecraft.class_10444 newItemStackRenderState() {
        return new net.minecraft.class_11540();
    }

    /** a state for 1.21.7's GuiItemRenderState, which takes only one that keeps its model identity */
    public static net.minecraft.class_11540 tracking(net.minecraft.class_10444 state) {
        return state instanceof net.minecraft.class_11540 t ? t : new View(state);
    }

    /** a plain state as a tracking one: everything the state does, its identity what it recorded */
    @Environment(EnvType.CLIENT)
    static final class View extends net.minecraft.class_11540 {
        private final net.minecraft.class_10444 state;

        View(net.minecraft.class_10444 state) {
            this.state = state;
        }

        @Override
        public Object method_72239() {
            return ((CbModelIdentity) state).cb$modelIdentity();
        }

        /** 1.21.6's clearModelIdentity() (given back to ItemStackRenderState by this layer's mixins): the viewed state's */
        public void method_71658() {
            ((CbModelIdentity) state).cb$clearModelIdentity();
        }

        @Override
        public void method_65602(int count) {
            state.method_65602(count);
        }

        @Override
        public net.minecraft.class_10444.class_10446 method_65601() {
            return state.method_65601();
        }

        @Override
        public void method_65605() {
            state.method_65605();
        }

        @Override
        public void method_70947() {
            state.method_70947();
        }

        @Override
        public boolean method_70948() {
            return state.method_70948();
        }

        @Override
        public void method_70946(Object element) {
            state.method_70946(element);
        }

        @Override
        public boolean method_65606() {
            return state.method_65606();
        }

        @Override
        public boolean method_65608() {
            return state.method_65608();
        }

        @Override
        public net.minecraft.class_1058 method_65603(net.minecraft.class_5819 random) {
            return state.method_65603(random);
        }

        @Override
        public void method_67991(Consumer<org.joml.Vector3fc> consumer) {
            state.method_67991(consumer);
        }

        @Override
        public void method_65604(net.minecraft.class_4587 matrices, net.minecraft.class_4597 vertexConsumers, int light, int overlay) {
            state.method_65604(matrices, vertexConsumers, light, overlay);
        }

        @Override
        public net.minecraft.class_238 method_72173() {
            return state.method_72173();
        }

        @Override
        public void method_72172(boolean oversized) {
            state.method_72172(oversized);
        }

        @Override
        public boolean method_72174() {
            return state.method_72174();
        }
    }
}
