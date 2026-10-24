package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractCauldronBlock's dispatcher (rt/CbWallCauldrons.interactions) */
@Mixin(AbstractCauldronBlock.class)
public interface CbWallCauldronBlockAccess {

    @Accessor("interactions")
    CauldronInteraction.Dispatcher cb$interactions();
}
