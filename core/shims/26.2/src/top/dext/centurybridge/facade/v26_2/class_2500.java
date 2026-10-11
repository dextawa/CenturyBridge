package top.dext.centurybridge.facade.v26_2;

import net.minecraft.world.level.block.SpreadingSnowyBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.mojang.serialization.MapCodec;

/** Facade for 1.20.1 net/minecraft/class_2500 (SpreadingSnowyDirtBlock).
 *  Not deleted on 26.2 -- renamed to SpreadingSnowyBlock and its ctor gained a
 *  baseBlock ResourceKey (the block it spreads from). The old 1.20.1 subclass
 *  (e.g. species' AlphaceneGrassBlock) still calls the 1-arg super(Properties),
 *  so this facade restores that constructor by defaulting the base block to
 *  DIRT (the vanilla grass base) and keeps the subclass a genuine
 *  SpreadingSnowyBlock -- the real spreading/randomTick behaviour runs. The
 *  abstract codec() is satisfied with the generic Block codec (data-driven
 *  serialization is registered separately by the mod). */
public abstract class class_2500 extends SpreadingSnowyBlock {
    public class_2500(BlockBehaviour.Properties props) {
        super(props, Blocks.DIRT.builtInRegistryHolder().key());
    }

    @Override
    protected MapCodec<? extends SpreadingSnowyBlock> codec() {
        // The real codec is only consulted for vanilla data-driven block
        // serialization; modded blocks supply their own registration. Return a
        // non-null codec so the abstract method links and PREPARE validation
        // never AbstractMethodErrors.
        return net.minecraft.world.level.block.GrassBlock.CODEC;
    }
}
