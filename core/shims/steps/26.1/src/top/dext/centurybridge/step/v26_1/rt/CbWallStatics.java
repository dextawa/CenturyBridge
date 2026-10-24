package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** 1.21.11 static helpers 26.1 dropped or reshaped, 1.21.11's bodies on 26.1's API */
public final class CbWallStatics {

    private CbWallStatics() {
    }

    /** ComponentUtils.updateForEntity(source, component, entity, depth): resolved with a context of the source and entity */
    public static MutableComponent updateForEntity(CommandSourceStack source, Component component, Entity entity, int depth) throws CommandSyntaxException {
        return ComponentUtils.resolve(context(source, entity), component, depth);
    }

    /** ComponentUtils.updateForEntity(source, optional component, entity, depth) */
    public static Optional<MutableComponent> updateForEntity(CommandSourceStack source, Optional<Component> component, Entity entity, int depth)
        throws CommandSyntaxException {
        return ComponentUtils.resolve(context(source, entity), component, depth);
    }

    private static ResolutionContext context(CommandSourceStack source, Entity entity) {
        return ResolutionContext.builder().withSource(source).withEntityOverride(entity).build();
    }

    /** Feature.isStone(state) */
    public static boolean isStone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD);
    }

    /** Feature.isDirt(state) */
    public static boolean isDirt(BlockState state) {
        return state.is(BlockTags.DIRT);
    }

    /** Feature.isGrassOrDirt(level, pos) */
    public static boolean isGrassOrDirt(LevelSimulatedReader level, BlockPos pos) {
        return level.isStateAtPosition(pos, CbWallStatics::isDirt);
    }

    /** BlockEntityType.getKey(type) */
    public static Identifier getKey(BlockEntityType<?> type) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
    }

    /** TrimMaterials.getFromIngredient(registries, stack): the material the item provides (26.1's component is the resolved holder) */
    public static Optional<Holder<TrimMaterial>> getFromIngredient(HolderLookup.Provider registries, ItemStack stack) {
        return Optional.ofNullable(stack.get(DataComponents.PROVIDES_TRIM_MATERIAL));
    }

    /** ShulkerBoxBlock.getBlockByColor(color): the plain box for no color */
    public static Block getBlockByColor(DyeColor color) {
        if (color == null) {
            return Blocks.SHULKER_BOX;
        }
        return switch (color) {
            case WHITE -> Blocks.WHITE_SHULKER_BOX;
            case ORANGE -> Blocks.ORANGE_SHULKER_BOX;
            case MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
            case YELLOW -> Blocks.YELLOW_SHULKER_BOX;
            case LIME -> Blocks.LIME_SHULKER_BOX;
            case PINK -> Blocks.PINK_SHULKER_BOX;
            case GRAY -> Blocks.GRAY_SHULKER_BOX;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
            case CYAN -> Blocks.CYAN_SHULKER_BOX;
            case PURPLE -> Blocks.PURPLE_SHULKER_BOX;
            case BLUE -> Blocks.BLUE_SHULKER_BOX;
            case BROWN -> Blocks.BROWN_SHULKER_BOX;
            case GREEN -> Blocks.GREEN_SHULKER_BOX;
            case RED -> Blocks.RED_SHULKER_BOX;
            case BLACK -> Blocks.BLACK_SHULKER_BOX;
        };
    }

    /** ShulkerBoxBlock.getColoredItemStack(color) */
    public static ItemStack getColoredItemStack(DyeColor color) {
        return new ItemStack(getBlockByColor(color));
    }

    /** BannerBlock.byColor(color): the standing banner of the color */
    public static Block bannerByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_BANNER;
            case ORANGE -> Blocks.ORANGE_BANNER;
            case MAGENTA -> Blocks.MAGENTA_BANNER;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BANNER;
            case YELLOW -> Blocks.YELLOW_BANNER;
            case LIME -> Blocks.LIME_BANNER;
            case PINK -> Blocks.PINK_BANNER;
            case GRAY -> Blocks.GRAY_BANNER;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_BANNER;
            case CYAN -> Blocks.CYAN_BANNER;
            case PURPLE -> Blocks.PURPLE_BANNER;
            case BLUE -> Blocks.BLUE_BANNER;
            case BROWN -> Blocks.BROWN_BANNER;
            case GREEN -> Blocks.GREEN_BANNER;
            case RED -> Blocks.RED_BANNER;
            case BLACK -> Blocks.BLACK_BANNER;
        };
    }
}
