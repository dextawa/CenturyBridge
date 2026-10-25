package cb.s26;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 26.1.2 beds (s26b, queue item 3): 26.2 dropped the bed block entity (beds draw as block models); a 26.1.2 mod's
 * beds keep it (facade/BedBlock, facade/BedBlockEntity, rt/CbS26Beds).
 */
final class BedChecks {
    private BedChecks() {}

    /** a 26.1.2 mod's own bed class */
    static class ModBed extends BedBlock {
        ModBed() {
            super(DyeColor.BLUE, S26TestMod.blockProps("mod_bed"));
        }
    }

    static void run() {
        S26TestMod.check("bed.BlockEntityType.BED", S26TestMod.describe(BlockEntityType.BED), "value\tminecraft:block_entity_type\tminecraft:bed");
        BlockState red = Blocks.RED_BED.defaultBlockState();
        S26TestMod.check("bed.BED.isValid(red bed)", BlockEntityType.BED.isValid(red), red);
        S26TestMod.check("bed.BED.isValid(stone)", !BlockEntityType.BED.isValid(Blocks.STONE.defaultBlockState()), "");
        // 26.1.2 BedBlock.newBlockEntity on a vanilla bed: new BedBlockEntity(pos, state, color)
        BlockEntity be = ((BedBlock) Blocks.RED_BED).newBlockEntity(BlockPos.ZERO, red);
        S26TestMod.check("bed.newBlockEntity(vanilla red)", be instanceof BedBlockEntity b && b.getColor() == DyeColor.RED
            && be.getType() == BlockEntityType.BED, be);
        BedBlockEntity direct = new BedBlockEntity(BlockPos.ZERO, Blocks.BLACK_BED.defaultBlockState());
        S26TestMod.check("bed.new BedBlockEntity(pos, state).getColor", direct.getColor() == DyeColor.BLACK, direct.getColor());
        // a 26.1.2 mod's beds are EntityBlocks, as every 26.1.2 BedBlock was
        BedBlock plain = net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, S26TestMod.blockKey("plain_bed"),
            new BedBlock(DyeColor.GREEN, S26TestMod.blockProps("plain_bed")));
        S26TestMod.check("bed.new BedBlock is EntityBlock", plain instanceof EntityBlock && plain.getColor() == DyeColor.GREEN, plain.getClass().getName());
        ModBed mod = net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.BLOCK, S26TestMod.blockKey("mod_bed"), new ModBed());
        S26TestMod.check("bed.mod subclass is EntityBlock", mod instanceof EntityBlock && mod.getColor() == DyeColor.BLUE, mod.getClass().getSuperclass().getName());
        // its block entity is the BED type's, which validates the state as on 26.1.2 (a mod adds its bed to BED's blocks)
        try {
            mod.newBlockEntity(BlockPos.ZERO, mod.defaultBlockState());
            S26TestMod.check("bed.mod newBlockEntity validates", false, "no exception for a block BED does not list");
        } catch (IllegalStateException e) {
            S26TestMod.check("bed.mod newBlockEntity validates", true, "");
        }
    }
}
