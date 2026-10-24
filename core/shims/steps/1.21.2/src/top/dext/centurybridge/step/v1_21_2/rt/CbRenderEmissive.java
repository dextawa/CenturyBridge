package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's LivingEntityEmissiveLayer(renderer, texture, alphaFunction, drawSelector) and its two functional interfaces
 * on 1.21.2's.
 *
 * <p>1.21.1's layer drew the warden's emissive texture with RenderType.entityTranslucentEmissive(texture), its alpha
 * from AlphaFunction.apply(warden, partialTick, ageInTicks), on the parts DrawSelector.getPartsToDraw(model) chose.
 * 1.21.2's layer takes the render-type function as a fifth constructor argument (WardenRenderer passes
 * RenderType::entityTranslucentEmissive: superCtorReshapes pushes the same) and calls AlphaFunction.apply(state,
 * ageInTicks) / DrawSelector.getPartsToDraw(model, state). A mod's 1.21.1 lambda is built as the 1.21.1 shape and
 * adapted (lambdaAdapters): the alpha function gets the warden the state was extracted from and the frame's partial
 * tick, the selector the model. A mod's own 1.21.1-shaped call (instanceRedirects) reaches a 1.21.1 function directly,
 * or asks a 1.21.2 one with the warden's render state.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderEmissive {

    /** 1.21.1 LivingEntityEmissiveLayer.AlphaFunction's abstract method */
    @FunctionalInterface
    public interface LegacyAlpha {
        float apply(net.minecraft.class_7260 warden, float partialTick, float ageInTicks);
    }

    /** 1.21.1 LivingEntityEmissiveLayer.DrawSelector's abstract method */
    @FunctionalInterface
    public interface LegacyParts {
        List<net.minecraft.class_630> getPartsToDraw(net.minecraft.class_583<?> model);
    }

    private record Alpha(LegacyAlpha legacy) implements net.minecraft.class_7288.class_7289<net.minecraft.class_10042> {
        @Override
        public float apply(net.minecraft.class_10042 state, float ageInTicks) {
            if (!(CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_7260 warden)) {
                throw new IllegalStateException("1.21.1 LivingEntityEmissiveLayer.AlphaFunction: a render state no warden was extracted into");
            }
            return this.legacy.apply(warden, CbRenderTex.partialTick(), ageInTicks);
        }
    }

    @SuppressWarnings("rawtypes")
    private record Parts(LegacyParts legacy) implements net.minecraft.class_7288.class_7311 {
        @Override
        public List<net.minecraft.class_630> getPartsToDraw(net.minecraft.class_583 model, net.minecraft.class_10042 state) {
            return this.legacy.getPartsToDraw(model);
        }
    }

    private CbRenderEmissive() {
    }

    public static net.minecraft.class_7288.class_7289<?> alpha(LegacyAlpha legacy) {
        return new Alpha(legacy);
    }

    public static net.minecraft.class_7288.class_7311<?, ?> parts(LegacyParts legacy) {
        return new Parts(legacy);
    }

    /** the render type 1.21.1's layer drew with */
    public static Function<net.minecraft.class_2960, net.minecraft.class_1921> renderType() {
        return net.minecraft.class_1921::method_42600;
    }

    /** a 1.21.1-shaped AlphaFunction.apply(warden, partialTick, ageInTicks) call */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static float apply(net.minecraft.class_7288.class_7289 function, net.minecraft.class_7260 warden, float partialTick, float ageInTicks) {
        if (function instanceof Alpha a) {
            return a.legacy().apply(warden, partialTick, ageInTicks);
        }
        net.minecraft.class_10017 state = CbRenderLiving.stateOf(CbRenderLiving.rendererOf(warden), warden, partialTick);
        return function.apply((net.minecraft.class_10042) state, ageInTicks);
    }

    /**
     * a 1.21.1-shaped DrawSelector.getPartsToDraw(model) call: a 1.21.1 selector directly; a 1.21.2 one with no state
     * (1.21.1's call had none to give; vanilla's selectors read only the model)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<net.minecraft.class_630> getPartsToDraw(net.minecraft.class_7288.class_7311 selector, net.minecraft.class_583 model) {
        if (selector instanceof Parts p) {
            return p.legacy().getPartsToDraw(model);
        }
        return selector.getPartsToDraw(model, null);
    }
}
