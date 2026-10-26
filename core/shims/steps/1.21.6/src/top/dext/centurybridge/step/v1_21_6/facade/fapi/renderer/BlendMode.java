package top.dext.centurybridge.step.v1_21_6.facade.fapi.renderer;

/**
 * Fabric API 1.21.5's renderer-v1 material.BlendMode, which 1.21.6's Fabric API (renderer-api 7.0) removed with the
 * whole material system: a quad now carries its ChunkSectionLayer itself (QuadEmitter.renderLayer, null = the block's
 * default layer; Indigo encodes null as its own index, javap of EncodingFormat.renderLayer). Constants, field and
 * fromRenderLayer as 1.21.5's (javap): DEFAULT has no render type, the others 1.21.5's chunk render types
 * (RenderType.solid / cutoutMipped / cutout / translucent; translucent() is the layer's rebuild, CbChunkTypes).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public enum BlendMode {
    DEFAULT(null),
    SOLID(net.minecraft.class_1921.method_23577()),
    CUTOUT_MIPPED(net.minecraft.class_1921.method_23579()),
    CUTOUT(net.minecraft.class_1921.method_23581()),
    TRANSLUCENT(top.dext.centurybridge.step.v1_21_6.rt.CbChunkTypes.method_23583());

    public final net.minecraft.class_1921 blockRenderLayer;

    BlendMode(net.minecraft.class_1921 blockRenderLayer) {
        this.blockRenderLayer = blockRenderLayer;
    }

    public static BlendMode fromRenderLayer(net.minecraft.class_1921 renderLayer) {
        if (renderLayer == net.minecraft.class_1921.method_23577()) {
            return SOLID;
        } else if (renderLayer == net.minecraft.class_1921.method_23579()) {
            return CUTOUT_MIPPED;
        } else if (renderLayer == net.minecraft.class_1921.method_23581()) {
            return CUTOUT;
        } else if (renderLayer == top.dext.centurybridge.step.v1_21_6.rt.CbChunkTypes.method_23583()) {
            return TRANSLUCENT;
        }
        return DEFAULT;
    }

    /** the 1.21.6 chunk layer this mode stands for; null (the block's default) for DEFAULT */
    public net.minecraft.class_11515 cb$layer() {
        return blockRenderLayer == null ? null : top.dext.centurybridge.step.v1_21_6.facade.fapi.BlockRenderLayerMap.layer(blockRenderLayer);
    }

    /** the mode a 1.21.6 quad's layer reads as: null is DEFAULT; TRIPWIRE, which 1.21.5 had no mode for, too */
    public static BlendMode cb$of(net.minecraft.class_11515 layer) {
        if (layer != null) {
            for (BlendMode mode : values()) {
                if (mode != DEFAULT && mode.cb$layer() == layer) {
                    return mode;
                }
            }
        }
        return DEFAULT;
    }
}
