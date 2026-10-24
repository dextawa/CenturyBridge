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
}
