package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1767;
import net.minecraft.class_2573;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** BannerBlockEntity.baseColor, final since 1.21.4; 1.21.3's fromItem(stack, color) set it (CbRestBlockEntities). */
@Mixin(class_2573.class)
public interface CbRestBannerAccess {
    @Mutable
    @Accessor("field_11774")
    void cb$setBaseColor(class_1767 color);
}
