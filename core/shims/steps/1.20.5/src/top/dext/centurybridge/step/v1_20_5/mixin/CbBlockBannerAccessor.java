package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/** BannerBlockEntity's private patterns and base color, for 1.20.4's bodies (CbBlockBanner) */
@Mixin(net.minecraft.class_2573.class)
public interface CbBlockBannerAccessor extends CbBlockBanner.Banner {

    @Override
    @Accessor("field_49756")
    net.minecraft.class_9307 cb$patterns();

    @Override
    @Accessor("field_11774")
    net.minecraft.class_1767 cb$baseColor();

    @Override
    @Accessor("field_11774")
    void cb$setBaseColor(net.minecraft.class_1767 color);
}
