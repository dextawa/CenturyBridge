package top.dext.centurybridge.step.v1_21_4.compat.fapi.rendering.v1;

import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Fabric API rendering-v1 BuiltinItemRendererRegistry (removed in 1.21.4's Fabric API): dynamic renderers for items
 * whose model is "builtin/entity". Fabric API's 1.21.3 registry, behaviour for behaviour (one renderer per item, a
 * second registration is an error, BuiltinItemRenderer wrapped as a DynamicItemRenderer). 1.21.4 draws them through
 * CenturyBridge's centurybridge:legacy_builtin special model, which the converter puts in the item definitions of a
 * 1.21.3 mod's builtin/entity item models (CbBuiltinItemModels), and through the BlockEntityWithoutLevelRenderer facade.
 */
@Environment(EnvType.CLIENT)
public interface BuiltinItemRendererRegistry {

    BuiltinItemRendererRegistry INSTANCE = new Impl();

    /** @deprecated in Fabric API too: the ItemLike overloads */
    @Deprecated
    void register(net.minecraft.class_1792 item, BuiltinItemRenderer renderer);

    void register(net.minecraft.class_1935 item, BuiltinItemRenderer renderer);

    void register(net.minecraft.class_1935 item, DynamicItemRenderer renderer);

    DynamicItemRenderer get(net.minecraft.class_1935 item);

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    interface DynamicItemRenderer {
        void render(net.minecraft.class_1799 stack, net.minecraft.class_811 mode, net.minecraft.class_4587 matrices,
            net.minecraft.class_4597 vertexConsumers, int light, int overlay);
    }

    /** Fabric API's BuiltinItemRendererRegistryImpl */
    final class Impl implements BuiltinItemRendererRegistry {
        private final Map<net.minecraft.class_1792, DynamicItemRenderer> renderers = new IdentityHashMap<>();

        private Impl() {
        }

        @Override
        public void register(net.minecraft.class_1792 item, BuiltinItemRenderer renderer) {
            this.register((net.minecraft.class_1935) item, renderer);
        }

        @Override
        public void register(net.minecraft.class_1935 item, BuiltinItemRenderer renderer) {
            java.util.Objects.requireNonNull(renderer, "renderer is null");
            this.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> renderer.render(stack, matrices, vertexConsumers, light, overlay));
        }

        @Override
        public synchronized void register(net.minecraft.class_1935 item, DynamicItemRenderer renderer) {
            java.util.Objects.requireNonNull(item, "item is null");
            java.util.Objects.requireNonNull(item.method_8389(), "item is null");
            java.util.Objects.requireNonNull(renderer, "renderer is null");
            if (this.renderers.putIfAbsent(item.method_8389(), renderer) != null) {
                throw new IllegalArgumentException("Item " + net.minecraft.class_7923.field_41178.method_10221(item.method_8389()) + " already has a builtin renderer!");
            }
        }

        @Override
        public synchronized DynamicItemRenderer get(net.minecraft.class_1935 item) {
            java.util.Objects.requireNonNull(item.method_8389(), "item is null");
            return this.renderers.get(item.method_8389());
        }
    }
}
