package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/** LoomMenu's private selectable patterns, for a mod loom's super.getSelectablePatterns() (CbBlockBanner) */
@Mixin(net.minecraft.class_1726.class)
public interface CbBlockLoomAccessor extends CbBlockBanner.Loom {

    @Override
    @Accessor("field_39121")
    java.util.List<net.minecraft.class_6880<net.minecraft.class_2582>> cb$selectablePatterns();
}
