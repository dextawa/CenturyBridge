package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1535;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbEntityPaintings;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/** PaintingVariant's 1.20.6 pixel-sized getters, restored on the native record for converted subclasses. */
@Mixin(value = class_1535.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbPaintingVariantMixin implements CbEntityPaintings.LegacyDimensions {
    @Override
    public int method_6945() {
        return CbEntityPaintings.getWidthBody((class_1535) (Object) this);
    }

    @Override
    public int method_6943() {
        return CbEntityPaintings.getHeightBody((class_1535) (Object) this);
    }
}
