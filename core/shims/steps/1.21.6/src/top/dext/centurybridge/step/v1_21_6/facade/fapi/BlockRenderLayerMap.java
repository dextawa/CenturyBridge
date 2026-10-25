package top.dext.centurybridge.step.v1_21_6.facade.fapi;

/**
 * Fabric API 1.21.5's fabric-blockrenderlayer-v1 (net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap),
 * a module 1.21.6's Fabric API removed: its BlockRenderLayerMap moved to fabric-rendering-v1
 * (net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap) and takes a ChunkSectionLayer, the chunk
 * layers 1.21.6 split out of RenderType. A 1.21.5 chunk render type is the layer drawing with its pipeline
 * (RenderType.solid / cutoutMipped / cutout / translucent / tripwire are the pipelines of ChunkSectionLayer
 * SOLID ... TRIPWIRE, javap of both releases' RenderType and ChunkSectionLayer &lt;clinit&gt;). 1.21.5's null
 * checks and messages are kept.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface BlockRenderLayerMap {

    BlockRenderLayerMap INSTANCE = new Impl();

    void putBlock(net.minecraft.class_2248 block, net.minecraft.class_1921 type);

    void putBlocks(net.minecraft.class_1921 type, net.minecraft.class_2248... blocks);

    void putFluid(net.minecraft.class_3611 fluid, net.minecraft.class_1921 type);

    void putFluids(net.minecraft.class_1921 type, net.minecraft.class_3611... fluids);

    /** the chunk layer a 1.21.5 chunk render type stands for */
    static net.minecraft.class_11515 layer(net.minecraft.class_1921 type) {
        com.mojang.blaze3d.pipeline.RenderPipeline pipeline = top.dext.centurybridge.step.v1_21_6.rt.CbGuiTypes.pipeline(type);
        for (net.minecraft.class_11515 layer : net.minecraft.class_11515.values()) {
            if (layer.method_72020() == pipeline) {
                return layer;
            }
        }
        throw new IllegalArgumentException("CenturyBridge: render type " + type
            + " is not a chunk render layer (1.21.6 maps blocks to ChunkSectionLayer only)");
    }

    final class Impl implements BlockRenderLayerMap {

        @Override
        public void putBlock(net.minecraft.class_2248 block, net.minecraft.class_1921 type) {
            if (block == null) {
                throw new IllegalArgumentException("Request to map null block to RenderLayer");
            }
            if (type == null) {
                throw new IllegalArgumentException("Request to map block " + block.toString() + " to null RenderLayer");
            }
            net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap.putBlock(block, layer(type));
        }

        @Override
        public void putBlocks(net.minecraft.class_1921 type, net.minecraft.class_2248... blocks) {
            for (net.minecraft.class_2248 block : blocks) {
                putBlock(block, type);
            }
        }

        @Override
        public void putFluid(net.minecraft.class_3611 fluid, net.minecraft.class_1921 type) {
            if (fluid == null) {
                throw new IllegalArgumentException("Request to map null fluid to RenderLayer");
            }
            if (type == null) {
                throw new IllegalArgumentException("Request to map fluid " + fluid.toString() + " to null RenderLayer");
            }
            net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap.putFluid(fluid, layer(type));
        }

        @Override
        public void putFluids(net.minecraft.class_1921 type, net.minecraft.class_3611... fluids) {
            for (net.minecraft.class_3611 fluid : fluids) {
                putFluid(fluid, type);
            }
        }
    }
}
