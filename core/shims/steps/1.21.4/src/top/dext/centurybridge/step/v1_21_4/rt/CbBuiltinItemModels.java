package top.dext.centurybridge.step.v1_21_4.rt;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.compat.fapi.rendering.v1.BuiltinItemRendererRegistry;

/**
 * centurybridge:legacy_builtin, a 1.21.4 special model renderer for the items a 1.21.3 mod modelled "builtin/entity":
 * 1.21.3 drew them through BlockEntityWithoutLevelRenderer, where Fabric API handed the stack to the item's
 * BuiltinItemRendererRegistry renderer -- after the model's display transform and the -0.5 centring, which is where
 * 1.21.4 calls a special renderer too. An item without a renderer draws nothing, as in 1.21.3 (the BEWLR knew no such
 * item). The converter writes the item definition (ResourceTranslator: a "minecraft:special" model, this type, the
 * mod's model as base for its transforms and particle).
 */
@Environment(EnvType.CLIENT)
public final class CbBuiltinItemModels {

    private CbBuiltinItemModels() {
    }

    public static final net.minecraft.class_2960 LEGACY_BUILTIN = net.minecraft.class_2960.method_60655("centurybridge", "legacy_builtin");

    public record Unbaked() implements net.minecraft.class_10515.class_10516 {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public net.minecraft.class_10515<?> method_65698(net.minecraft.class_5599 models) {
            return new Renderer();
        }

        @Override
        public MapCodec<Unbaked> method_65696() {
            return CODEC;
        }
    }

    /** the stack's argument is the stack itself: the Fabric renderer takes it */
    static final class Renderer implements net.minecraft.class_10515<net.minecraft.class_1799> {
        @Override
        public void method_65694(net.minecraft.class_1799 stack, net.minecraft.class_811 mode, net.minecraft.class_4587 pose,
                net.minecraft.class_4597 buffers, int light, int overlay, boolean foil) {
            if (stack == null) {
                return;
            }
            BuiltinItemRendererRegistry.DynamicItemRenderer r = BuiltinItemRendererRegistry.INSTANCE.get(stack.method_7909());
            if (r != null) {
                r.render(stack, mode, pose, buffers, light, overlay);
            }
        }

        @Override
        public net.minecraft.class_1799 method_65695(net.minecraft.class_1799 stack) {
            return stack.method_7972();
        }
    }
}
