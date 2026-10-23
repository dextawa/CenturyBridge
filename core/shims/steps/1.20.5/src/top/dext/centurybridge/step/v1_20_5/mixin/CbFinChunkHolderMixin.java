package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * ChunkHolder: 1.20.4's getFullChunkNow() -- the full chunk if its future has completed with one, else null --
 * over 1.20.5's ChunkResult futures.
 */
@Mixin(net.minecraft.class_3193.class)
public abstract class CbFinChunkHolderMixin {

    /** was getFullChunkNow() */
    public net.minecraft.class_2818 method_41205() {
        return ((net.minecraft.class_3193) (Object) this).method_20725().getNow(net.minecraft.class_3193.field_16427).method_57130(null);
    }
}
