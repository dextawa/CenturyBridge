package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbWallDisplayIcons;

/**
 * The ItemStack icon 1.21.11's DisplayInfo held (class_185.field_1241), kept beside 26.1's template so 1.21.11's
 * getIcon() (rt/CbWallDisplayIcons) answers the stack it was built from, the same one on every call.
 */
@Mixin(DisplayInfo.class)
public abstract class CbWallDisplayInfoIconMixin implements CbWallDisplayIcons.Icon {

    @Shadow
    @Final
    private ItemStackTemplate icon;

    @Unique
    private ItemStack cb$icon;

    @Override
    public ItemStack cb$icon() {
        return this.cb$icon;
    }

    @Override
    public void cb$setIcon(ItemStack icon) {
        this.cb$icon = icon;
    }

    @Override
    public ItemStackTemplate cb$template() {
        return this.icon;
    }
}
