package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * ChunkSectionLayer.CUTOUT_MIPPED (field_60924), removed at 1.21.11 where 1.21.10's CUTOUT_MIPPED and CUTOUT merge
 * into one CUTOUT layer (javap -c -p of both code jars): every place 1.21.10's ItemBlockRenderTypes chose CUTOUT_MIPPED
 * (leaves under the fancy-leaves option in getChunkRenderType, the block map's CUTOUT_MIPPED entries) chooses CUTOUT at
 * 1.21.11, and 1.21.11's CUTOUT takes CUTOUT_MIPPED's ordinal (1) and buffer size (4194304), not 1.21.10 CUTOUT's
 * (786432). Cutout-mipped geometry is drawn in CUTOUT, so that is the layer the old constant names. The two old layers
 * are one value now: code telling them apart sees them equal.
 */
@Environment(EnvType.CLIENT)
public final class CbChunkSectionLayers {

    private CbChunkSectionLayers() {
    }

    public static net.minecraft.class_11515 field_60924() {
        return net.minecraft.class_11515.field_60925;
    }
}
