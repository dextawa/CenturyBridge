package top.dext.centurybridge.step.v26_2.facade;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_2.rt.CbS26Beds;

/**
 * 26.1.2's BedBlock as the beds a mod makes see it: 26.2's BedBlock (every member it kept) that is still an
 * EntityBlock making 26.1.2's bed block entity (26.1.2 BedBlock.newBlockEntity, javap: new BedBlockEntity(pos, state,
 * this.color)). Reached by superRenames (a mod's subclass of BedBlock) and ctorRedirects (a mod's own new BedBlock):
 * 26.2's vanilla beds stay as 26.2 made them (block models, no block entity) -- the bed a 26.1.2 mod adds keeps the
 * block entity its renderer (26.1.2 BedRenderer, facade/BedRenderer) draws it from.
 * <p>
 * 26.1.2's BedBlock bounced what fell on it (updateEntityMovementAfterFallOn -> bounceUp); 26.2 bounces through the
 * block's bounce restitution, which its own beds get from their properties (Blocks: bounceRestitution(0.75f)). A mod's
 * bed whose properties set none gets the same (CbS26Beds.bedProperties).
 */
public class BedBlock extends net.minecraft.world.level.block.BedBlock implements EntityBlock {

    public BedBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(color, CbS26Beds.bedProperties(properties));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BedBlockEntity(pos, state, this.getColor());
    }
}
