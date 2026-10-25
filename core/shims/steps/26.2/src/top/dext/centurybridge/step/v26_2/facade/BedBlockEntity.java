package top.dext.centurybridge.step.v26_2.facade;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_2.rt.CbS26Beds;

/**
 * 26.1.2's BedBlockEntity (gone on 26.2, whose beds have no block entity and draw as block models): reached by
 * classRenames. The same class body as 26.1.2's (javap): a block entity of 26.1.2's BlockEntityType.BED holding the
 * bed's colour, the colour taken from the BedBlock when none is given, and an update packet of its (empty) data.
 * Nothing is saved: 26.1.2's held only the colour, read from the block.
 */
public class BedBlockEntity extends BlockEntity {
    private final DyeColor color;

    public BedBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((net.minecraft.world.level.block.BedBlock) state.getBlock()).getColor());
    }

    public BedBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(CbS26Beds.BED(), pos, state);
        this.color = color;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public DyeColor getColor() {
        return this.color;
    }
}
