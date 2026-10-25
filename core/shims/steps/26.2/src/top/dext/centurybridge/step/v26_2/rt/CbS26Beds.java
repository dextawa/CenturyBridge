package top.dext.centurybridge.step.v26_2.rt;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_2.facade.BedBlockEntity;

/**
 * Beds, common side. 26.2 draws beds as block models (blockstates/&lt;colour&gt;_bed.json -> models/block/
 * &lt;colour&gt;_bed_head|foot, parent template_bed_head|foot) and dropped their block entity: BedBlock is no
 * EntityBlock, BlockEntityType.BED and BedBlockEntity are gone. A 26.1.2 mod's bed was drawn from that block entity
 * (BlockEntityRenderers: BED -> BedRenderer), so it keeps one: 26.1.2's BED type, registered as 26.1.2 registered it
 * ("minecraft:bed", the sixteen vanilla beds valid -- a mod adds its own bed as it did on 26.1.2), made by the beds
 * a 26.1.2 mod creates (facade/BedBlock).
 */
public final class CbS26Beds {
    private CbS26Beds() {}

    private static BlockEntityType<BedBlockEntity> bed;

    /**
     * 26.1.2 BlockEntityType.BED: registered from the end of 26.2's BlockEntityTypes initializer (mixin/
     * CbS26BedTypeMixin), where 26.1.2's BlockEntityType initializer registered it -- the built-in registry is open,
     * Blocks is initialized.
     */
    public static void register() {
        if (bed == null) {
            Set<Block> valid = new HashSet<>(Blocks.BED.asList());
            bed = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, "bed", new BlockEntityType<>(BedBlockEntity::new, valid));
        }
    }

    /** 26.1.2 BlockEntityType.BED */
    public static BlockEntityType<BedBlockEntity> BED() {
        return bed;
    }

    /**
     * 26.1.2 AtlasIds.BEDS ("minecraft:beds"): 26.2 has no bed atlas; the layer's resources put 26.1.2's entity/bed
     * sprites in the block atlas (rt/CbS26BedsClient), so the atlas holding them is AtlasIds.BLOCKS
     */
    public static net.minecraft.resources.Identifier AtlasIds_BEDS() {
        return net.minecraft.data.AtlasIds.BLOCKS;
    }

    /**
     * 26.1.2 BedBlock.newBlockEntity(pos, state): the bed's own (a mod's bed, facade/BedBlock or a subclass, answers
     * through its override), else 26.1.2's body: new BedBlockEntity(pos, state, color).
     */
    public static BlockEntity newBlockEntity(BedBlock self, BlockPos pos, BlockState state) {
        if (self instanceof EntityBlock e) {
            return e.newBlockEntity(pos, state);
        }
        return new BedBlockEntity(pos, state, self.getColor());
    }

    /** a 26.1.2 mod's new BedBlock(color, properties): the bed that keeps 26.1.2's block entity (facade/BedBlock) */
    public static BedBlock cbNewBedBlock(DyeColor color, BlockBehaviour.Properties properties) {
        return new top.dext.centurybridge.step.v26_2.facade.BedBlock(color, properties);
    }

    /**
     * the bounce 26.1.2's BedBlock gave whatever fell on it, as 26.2 gives its own beds one: a bounce restitution of
     * 0.75 (26.2 Blocks, every bed's properties), where the mod's properties set none
     */
    public static BlockBehaviour.Properties bedProperties(BlockBehaviour.Properties properties) {
        if (properties.bounceRestitution == 0.0F) {
            properties.bounceRestitution(0.75F);
        }
        return properties;
    }
}
