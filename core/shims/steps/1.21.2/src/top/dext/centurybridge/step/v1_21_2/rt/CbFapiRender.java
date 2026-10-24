package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Members of 1.21.1 Fabric API's rendering modules that 1.21.2's Fabric API dropped, on what it keeps.
 *
 * BlockRenderLayerMap.putItem / putItems: 1.21.1's implementation (BlockRenderLayerMapImpl) checked its arguments and
 * mapped Block.byItem(item) to the layer as a block (the item handler forwarded to the block handler) -- which
 * 1.21.2's putBlock does.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiRender {

    private CbFapiRender() {
    }

    public static void putItem(net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap map, net.minecraft.class_1792 item,
                               net.minecraft.class_1921 renderLayer) {
        if (item == null) {
            throw new IllegalArgumentException("Request to map null item to BlockRenderLayer");
        }
        if (renderLayer == null) {
            throw new IllegalArgumentException("Request to map item " + item.toString() + " to null BlockRenderLayer");
        }
        map.putBlock(net.minecraft.class_2248.method_9503(item), renderLayer);
    }

    public static void putItems(net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap map, net.minecraft.class_1921 renderLayer,
                                net.minecraft.class_1792... items) {
        for (net.minecraft.class_1792 item : items) {
            putItem(map, item, renderLayer);
        }
    }

    /** the 1.21.1 ArmorRenderer registered for each item (compat ArmorRenderer.register) */
    private static final java.util.Map<net.minecraft.class_1792, top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendering.v1.ArmorRenderer> ARMOR =
        new java.util.concurrent.ConcurrentHashMap<>();

    public static void armorRenderer(net.minecraft.class_1792 item, top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendering.v1.ArmorRenderer renderer) {
        ARMOR.put(item, renderer);
    }

    /**
     * Whether the head layer skips the head item: 1.21.1 Fabric API's HeadFeatureRendererMixin cancelled the layer when
     * the item's armour renderer answered false to shouldRenderDefaultHeadItem(entity, stack). Asked of 1.21.1 renderers
     * only (1.21.2's ArmorRenderer has no such method), with the entity the state was extracted from.
     */
    public static boolean hidesHeadItem(net.minecraft.class_10042 state) {
        net.minecraft.class_1799 stack = state.field_53467;
        if (stack == null || stack.method_7960()) {
            return false;
        }
        top.dext.centurybridge.step.v1_21_2.compat.fapi.client.rendering.v1.ArmorRenderer renderer = ARMOR.get(stack.method_7909());
        return renderer != null && CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1309 entity
            && !renderer.shouldRenderDefaultHeadItem(entity, stack);
    }
}
