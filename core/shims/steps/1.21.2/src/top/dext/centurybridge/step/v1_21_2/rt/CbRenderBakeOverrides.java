package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Item overrides of 1.21.1 baked models. 1.21.1's SimpleBakedModel / BuiltInModel / SimpleBakedModel.Builder took the
 * ItemOverrides (1.21.2's BakedOverrides, the same class) in their constructors and answered them from getOverrides();
 * 1.21.2 dropped the argument, and BakedModel.overrides() answers EMPTY unless a model says otherwise -- while
 * 1.21.2's ItemRenderer still resolves an item model through it. The constructors (superCtorReshapes, the argument
 * dropped and handed over right after construction) keep the overrides on the model (CbRenderBakeOverridesMixin and
 * friends), which answers them again; a Builder hands its overrides to the model it builds, as 1.21.1's did.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBakeOverrides {

    /** a model keeping 1.21.1 overrides */
    public interface Holder {
        void cb$overrides(net.minecraft.class_806 overrides);
    }

    private CbRenderBakeOverrides() {
    }

    public static void simple(net.minecraft.class_1093 model, net.minecraft.class_806 overrides) {
        ((Holder) model).cb$overrides(overrides);
    }

    public static void builtIn(net.minecraft.class_1090 model, net.minecraft.class_806 overrides) {
        ((Holder) model).cb$overrides(overrides);
    }

    public static void builder(net.minecraft.class_1093.class_1094 builder, net.minecraft.class_806 overrides) {
        ((Holder) builder).cb$overrides(overrides);
    }

    /** what a model with kept overrides answers: them, or 1.21.2's EMPTY when none were given */
    public static net.minecraft.class_806 answer(net.minecraft.class_806 kept) {
        return kept != null ? kept : net.minecraft.class_806.field_4292;
    }
}
