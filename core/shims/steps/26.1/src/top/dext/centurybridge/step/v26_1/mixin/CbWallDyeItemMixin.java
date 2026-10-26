package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallDyes;

/** 1.21.11's DyeItem.getDyeColor(), given back (cover): the item's color, its default DYE component (rt/CbWallDyes) */
@Mixin(value = DyeItem.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallDyeItemMixin {

    public DyeColor getDyeColor() {
        return CbWallDyes.dyeColor((DyeItem) (Object) this);
    }
}
