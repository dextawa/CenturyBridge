package top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendering.v1;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateEntity;

/**
 * 1.21.1 Fabric API's ArmorRenderer (classRenames): render(matrices, vertexConsumers, stack, entity, slot, light,
 * contextModel) and shouldRenderDefaultHeadItem(entity, stack). 1.21.2's passes the humanoid render state where 1.21.1
 * passed the entity, and has no shouldRenderDefaultHeadItem.
 *
 * <p>register hands 1.21.2's registry a 1.21.2 renderer running the 1.21.1 one with the living entity the state was
 * extracted from (CbRenderStateEntity -- every state the game extracts holds it); the context model is the same object
 * (HumanoidModel, typed by the entity in 1.21.1 and by the state in 1.21.2: raw here, the same erasure). A state built without an entity is never drawn for a 1.21.1 renderer:
 * 1.21.1 only ever called it with an entity. The renderer is also kept per item, so the head layer can ask
 * shouldRenderDefaultHeadItem as 1.21.1 Fabric API's HeadFeatureRendererMixin did (CbFapiRender.hidesHeadItem, CbRenderHeadLayerMixin).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ArmorRenderer {

    static void register(ArmorRenderer renderer, net.minecraft.class_1935... items) {
        Objects.requireNonNull(renderer, "renderer is null");
        net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer.register(
            (matrices, vertexConsumers, stack, state, slot, light, contextModel) -> {
                if (CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1309 entity) {
                    renderer.render(matrices, vertexConsumers, stack, entity, slot, light, contextModel);
                }
            }, items);
        for (net.minecraft.class_1935 item : items) {
            top.dext.centurybridge.step.v1_21_2.rt.CbFapiRender.armorRenderer(item.method_8389(), renderer);
        }
    }

    /** renderPart(matrices, vertexConsumers, light, stack, model, texture): 1.21.2's, the same */
    static void renderPart(net.minecraft.class_4587 matrices, net.minecraft.class_4597 vertexConsumers, int light, net.minecraft.class_1799 stack,
                           net.minecraft.class_3879 model, net.minecraft.class_2960 texture) {
        net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, texture);
    }

    void render(net.minecraft.class_4587 matrices, net.minecraft.class_4597 vertexConsumers, net.minecraft.class_1799 stack, net.minecraft.class_1309 entity,
                net.minecraft.class_1304 slot, int light, @SuppressWarnings("rawtypes") net.minecraft.class_572 contextModel);

    default boolean shouldRenderDefaultHeadItem(net.minecraft.class_1309 entity, net.minecraft.class_1799 stack) {
        return true;
    }
}
