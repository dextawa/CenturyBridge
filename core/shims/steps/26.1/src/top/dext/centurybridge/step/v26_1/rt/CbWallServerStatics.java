package top.dext.centurybridge.step.v26_1.rt;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;

/**
 * Server-side 1.21.11 statics and members 26.1 dropped or reshaped, 1.21.11's bodies on 26.1's API:
 * <ul>
 * <li>Brightness.pack / block / sky (class_8104): 1.21.11's packed light coordinates, block light in bits 4..19 and
 *     sky light from bit 20 -- 26.1's LightCoordsUtil packs the same way but unpacks 4 bits where 1.21.11 took 16,
 *     so 1.21.11's bodies;</li>
 * <li>FluidState.holder() / getTags(): the fluid's built-in holder and its tags;</li>
 * <li>PiglinAi.isZombified(type): a zombified piglin or a zoglin;</li>
 * <li>ItemUtils.onContainerDestroyed(entity, contents): 26.1 takes a stream of the contents;</li>
 * <li>Raids.getType(dimension) / TYPE_END: 1.21.11 kept the End's raids in a file of their own beside the
 *     overworld's in one shared data storage; 26.1's levels each keep their own storage and every level its raids
 *     under Raids.TYPE (ServerLevel reads them so), which is therefore the type for every dimension;</li>
 * <li>WardenAi.updateActivity(warden): 26.1 takes the warden's brain;</li>
 * <li>Player.isWithinAttackRange(box, buffer) / setIgnoreFallDamageFromCurrentImpulse(ignore): 26.1's with the
 *     active item / the current impact position;</li>
 * <li>AbstractContainerMenu.getQuickCraftPlaceCount(slots, type, carried): 1.21.11's body (26.1 dropped it);</li>
 * <li>BigDripleafBlock.canPlaceAt(level, pos, state): inside the build height and replaceable (air, water, small
 *     dripleaf) -- 1.21.11's body (26.1's canPlaceAt(level, pos) tests other things);</li>
 * <li>BubbleColumnBlock.updateColumn(level, pos, state): 26.1's with the bubble column block it names.</li>
 * </ul>
 */
public final class CbWallServerStatics {

    /** 1.21.11 Raids.TYPE_END (class_3767.field_56444): see the class comment */
    public static final SavedDataType<Raids> TYPE_END = Raids.TYPE;

    private CbWallServerStatics() {
    }

    /** 1.21.11 Brightness.pack(block, sky) */
    public static int pack(int block, int sky) {
        return block << 4 | sky << 20;
    }

    /** 1.21.11 Brightness.block(packed) */
    public static int block(int packed) {
        return packed >> 4 & 0xFFFF;
    }

    /** 1.21.11 Brightness.sky(packed) */
    public static int sky(int packed) {
        return packed >> 20 & 0xFFFF;
    }

    /** 1.21.11 FluidState.holder() */
    public static Holder<Fluid> holder(FluidState state) {
        return state.getType().builtInRegistryHolder();
    }

    /** 1.21.11 FluidState.getTags() */
    public static Stream<TagKey<Fluid>> getTags(FluidState state) {
        return state.getType().builtInRegistryHolder().tags();
    }

    /** 1.21.11 PiglinAi.isZombified(type) */
    public static boolean isZombified(EntityType<?> type) {
        return type == EntityType.ZOMBIFIED_PIGLIN || type == EntityType.ZOGLIN;
    }

    /** 1.21.11 ItemUtils.onContainerDestroyed(entity, contents) */
    public static void onContainerDestroyed(ItemEntity entity, Iterable<ItemStack> contents) {
        ItemUtils.onContainerDestroyed(entity, StreamSupport.stream(contents.spliterator(), false));
    }

    /** 1.21.11 Raids.getType(dimension): see the class comment */
    public static SavedDataType<Raids> getType(Holder<DimensionType> dimension) {
        return Raids.TYPE;
    }

    /** 1.21.11 WardenAi.updateActivity(warden) */
    public static void updateActivity(Warden warden) {
        WardenAi.updateActivity(warden.getBrain());
    }

    /**
     * 1.21.11 Player.isWithinAttackRange(box, buffer): 26.1's with the active item (whose range 26.1 reads through
     * getAttackRangeWith, which the layer routes through entityAttackRange for the active item)
     */
    public static boolean isWithinAttackRange(Player player, AABB box, double buffer) {
        return player.isWithinAttackRange(player.getActiveItem(), box, buffer);
    }

    /**
     * 1.21.11 Player.setIgnoreFallDamageFromCurrentImpulse(ignore): 1.21.11's left the impulse impact position as it
     * was; 26.1's sets the one passed -- the current one
     */
    public static void setIgnoreFallDamageFromCurrentImpulse(Player player, boolean ignore) {
        player.setIgnoreFallDamageFromCurrentImpulse(ignore, player.currentImpulseImpactPos);
    }

    /** 1.21.11 AbstractContainerMenu.getQuickCraftPlaceCount(slots, type, carried) */
    public static int getQuickCraftPlaceCount(Set<Slot> slots, int type, ItemStack carried) {
        return switch (type) {
            case 0 -> Mth.floor((float) carried.getCount() / (float) slots.size());
            case 1 -> 1;
            case 2 -> carried.getMaxStackSize();
            default -> carried.getCount();
        };
    }

    /** 1.21.11 BigDripleafBlock.canPlaceAt(level, pos, state) */
    public static boolean canPlaceAt(LevelHeightAccessor level, BlockPos pos, BlockState state) {
        return !level.isOutsideBuildHeight(pos) && (state.isAir() || state.is(Blocks.WATER) || state.is(Blocks.SMALL_DRIPLEAF));
    }

    /** 1.21.11 BubbleColumnBlock.updateColumn(level, pos, state) */
    public static void updateColumn(LevelAccessor level, BlockPos pos, BlockState state) {
        BubbleColumnBlock.updateColumn(Blocks.BUBBLE_COLUMN, level, pos, state);
    }
}
