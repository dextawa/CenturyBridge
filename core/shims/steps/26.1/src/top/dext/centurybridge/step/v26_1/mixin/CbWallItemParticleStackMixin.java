package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v26_1.rt.CbWallParticleItems;

/**
 * The ItemStack 1.21.11's ItemParticleOption held (class_2392.field_11192), kept beside 26.1's template so 1.21.11's
 * getItem() (rt/CbWallParticleItems) answers the stack it was built from, the same one on every call.
 */
@Mixin(ItemParticleOption.class)
public abstract class CbWallItemParticleStackMixin implements CbWallParticleItems.Stack {

    @Shadow
    @Final
    private ItemStackTemplate itemStack;

    @Unique
    private ItemStack cb$stack;

    @Override
    public ItemStack cb$stack() {
        return this.cb$stack;
    }

    @Override
    public void cb$setStack(ItemStack stack) {
        this.cb$stack = stack;
    }

    @Override
    public ItemStackTemplate cb$template() {
        return this.itemStack;
    }
}
