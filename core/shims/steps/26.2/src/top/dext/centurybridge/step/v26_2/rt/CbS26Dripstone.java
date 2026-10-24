package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.minecraft.world.level.levelgen.feature.SpeleothemUtils;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpeleothemClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpeleothemConfiguration;

/**
 * 26.1.2's dripstone worldgen on 26.2's speleothems. 26.2 generalised DripstoneClusterConfiguration,
 * PointedDripstoneConfiguration and DripstoneUtils into speleothem records/utilities that carry the base block, the
 * pointed block and the replaceable set; 26.1.2 hard-wired those to dripstone_block, pointed_dripstone and
 * #dripstone_replaceable_blocks. Every adapter passes exactly what 26.2's own CaveFeatures passes for its dripstone
 * features: Blocks.DRIPSTONE_BLOCK.defaultBlockState(), Blocks.POINTED_DRIPSTONE.defaultBlockState(), and the block
 * registry's tag BlockTags.DRIPSTONE_REPLACEABLE, read at use (the tag 26.1.2's state.is(tag) consulted).
 */
public final class CbS26Dripstone {
    private CbS26Dripstone() {}

    /** #dripstone_replaceable_blocks read at use, as 26.1.2's state.is(tag) did (rt/CbS26LazyTagSet) */
    private static final HolderSet<Block> REPLACEABLE = new CbS26LazyTagSet<>(BuiltInRegistries.BLOCK, BlockTags.DRIPSTONE_REPLACEABLE);

    static HolderSet<Block> replaceable() {
        return REPLACEABLE;
    }

    // ---- DripstoneClusterConfiguration (renamed to SpeleothemClusterConfiguration) ----

    /** 26.1.2 new DripstoneClusterConfiguration(...) */
    public static SpeleothemClusterConfiguration cbNewCluster(int floorToCeilingSearchRange, IntProvider height, IntProvider radius,
            int maxStalagmiteStalactiteHeightDiff, int heightDeviation, IntProvider dripstoneBlockLayerThickness, FloatProvider density,
            FloatProvider wetness, float chanceOfDripstoneColumnAtMaxDistanceFromCenter, int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn,
            int maxDistanceFromCenterAffectingHeightBias) {
        return new SpeleothemClusterConfiguration(Blocks.DRIPSTONE_BLOCK.defaultBlockState(), Blocks.POINTED_DRIPSTONE.defaultBlockState(),
            replaceable(), floorToCeilingSearchRange, height, radius, maxStalagmiteStalactiteHeightDiff, heightDeviation,
            dripstoneBlockLayerThickness, density, wetness, chanceOfDripstoneColumnAtMaxDistanceFromCenter,
            maxDistanceFromEdgeAffectingChanceOfDripstoneColumn, maxDistanceFromCenterAffectingHeightBias);
    }

    /** 26.1.2's DripstoneClusterConfiguration.CODEC: its field names and ranges (javap), the dripstone blocks implied */
    private static final Codec<SpeleothemClusterConfiguration> CLUSTER = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter(SpeleothemClusterConfiguration::floorToCeilingSearchRange),
            IntProviders.codec(1, 128).fieldOf("height").forGetter(SpeleothemClusterConfiguration::height),
            IntProviders.codec(1, 128).fieldOf("radius").forGetter(SpeleothemClusterConfiguration::radius),
            Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter(SpeleothemClusterConfiguration::maxStalagmiteStalactiteHeightDiff),
            Codec.intRange(1, 64).fieldOf("height_deviation").forGetter(SpeleothemClusterConfiguration::heightDeviation),
            IntProviders.codec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter(SpeleothemClusterConfiguration::speleothemBlockLayerThickness),
            FloatProviders.codec(0.0F, 2.0F).fieldOf("density").forGetter(SpeleothemClusterConfiguration::density),
            FloatProviders.codec(0.0F, 2.0F).fieldOf("wetness").forGetter(SpeleothemClusterConfiguration::wetness),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_dripstone_column_at_max_distance_from_center")
                .forGetter(SpeleothemClusterConfiguration::chanceOfSpeleothemAtMaxDistanceFromCenter),
            Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column")
                .forGetter(SpeleothemClusterConfiguration::maxDistanceFromEdgeAffectingChanceOfSpeleothem),
            Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias")
                .forGetter(SpeleothemClusterConfiguration::maxDistanceFromCenterAffectingHeightBias))
        .apply(i, CbS26Dripstone::cbNewCluster));

    public static Codec<SpeleothemClusterConfiguration> CLUSTER_CODEC() {
        return CLUSTER;
    }

    public static int floorToCeilingSearchRange(SpeleothemClusterConfiguration c) { return c.floorToCeilingSearchRange(); }
    public static IntProvider height(SpeleothemClusterConfiguration c) { return c.height(); }
    public static IntProvider radius(SpeleothemClusterConfiguration c) { return c.radius(); }
    public static int maxStalagmiteStalactiteHeightDiff(SpeleothemClusterConfiguration c) { return c.maxStalagmiteStalactiteHeightDiff(); }
    public static int heightDeviation(SpeleothemClusterConfiguration c) { return c.heightDeviation(); }
    public static IntProvider dripstoneBlockLayerThickness(SpeleothemClusterConfiguration c) { return c.speleothemBlockLayerThickness(); }
    public static FloatProvider density(SpeleothemClusterConfiguration c) { return c.density(); }
    public static FloatProvider wetness(SpeleothemClusterConfiguration c) { return c.wetness(); }
    public static float chanceOfDripstoneColumnAtMaxDistanceFromCenter(SpeleothemClusterConfiguration c) {
        return c.chanceOfSpeleothemAtMaxDistanceFromCenter();
    }
    public static int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn(SpeleothemClusterConfiguration c) {
        return c.maxDistanceFromEdgeAffectingChanceOfSpeleothem();
    }
    public static int maxDistanceFromCenterAffectingHeightBias(SpeleothemClusterConfiguration c) { return c.maxDistanceFromCenterAffectingHeightBias(); }

    // ---- PointedDripstoneConfiguration (renamed to SpeleothemConfiguration) ----

    /** 26.1.2 new PointedDripstoneConfiguration(taller, directionalSpread, spreadRadius2, spreadRadius3) */
    public static SpeleothemConfiguration cbNewPointed(float chanceOfTallerDripstone, float chanceOfDirectionalSpread,
            float chanceOfSpreadRadius2, float chanceOfSpreadRadius3) {
        return new SpeleothemConfiguration(Blocks.DRIPSTONE_BLOCK.defaultBlockState(), Blocks.POINTED_DRIPSTONE.defaultBlockState(),
            replaceable(), chanceOfTallerDripstone, chanceOfDirectionalSpread, chanceOfSpreadRadius2, chanceOfSpreadRadius3);
    }

    /** 26.1.2's PointedDripstoneConfiguration.CODEC: its field names, ranges and defaults (javap) */
    private static final Codec<SpeleothemConfiguration> POINTED = RecordCodecBuilder.create(i -> i.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_taller_dripstone").orElse(0.2F).forGetter(SpeleothemConfiguration::chanceOfTallerGeneration),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_directional_spread").orElse(0.7F).forGetter(SpeleothemConfiguration::chanceOfDirectionalSpread),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius2").orElse(0.5F).forGetter(SpeleothemConfiguration::chanceOfSpreadRadius2),
            Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius3").orElse(0.5F).forGetter(SpeleothemConfiguration::chanceOfSpreadRadius3))
        .apply(i, CbS26Dripstone::cbNewPointed));

    public static Codec<SpeleothemConfiguration> POINTED_CODEC() {
        return POINTED;
    }

    public static float chanceOfTallerDripstone(SpeleothemConfiguration c) { return c.chanceOfTallerGeneration(); }
    public static float chanceOfDirectionalSpread(SpeleothemConfiguration c) { return c.chanceOfDirectionalSpread(); }
    public static float chanceOfSpreadRadius2(SpeleothemConfiguration c) { return c.chanceOfSpreadRadius2(); }
    public static float chanceOfSpreadRadius3(SpeleothemConfiguration c) { return c.chanceOfSpreadRadius3(); }

    // ---- DripstoneUtils (renamed to SpeleothemUtils): the members 26.2 parameterised by the blocks ----

    /** 26.1.2 DripstoneUtils.isDripstoneBase(state): state.is(DRIPSTONE_BLOCK) || state.is(#dripstone_replaceable_blocks) */
    public static boolean isDripstoneBase(BlockState state) {
        return state.is(Blocks.DRIPSTONE_BLOCK) || state.is(BlockTags.DRIPSTONE_REPLACEABLE);
    }

    /** 26.1.2 DripstoneUtils.isDripstoneBaseOrLava(state): isDripstoneBase(state) || state.is(LAVA) */
    public static boolean isDripstoneBaseOrLava(BlockState state) {
        return isDripstoneBase(state) || state.is(Blocks.LAVA);
    }

    /** 26.1.2 DripstoneUtils.buildBaseToTipColumn(direction, length, mergedTip, consumer): pointed dripstone states */
    public static void buildBaseToTipColumn(Direction direction, int length, boolean mergedTip, Consumer<BlockState> consumer) {
        SpeleothemUtils.buildBaseToTipColumn(direction, length, mergedTip, consumer, Blocks.POINTED_DRIPSTONE);
    }

    /** 26.1.2 DripstoneUtils.growPointedDripstone(level, pos, direction, length, mergedTip) */
    public static void growPointedDripstone(LevelAccessor level, BlockPos pos, Direction direction, int length, boolean mergedTip) {
        SpeleothemUtils.growSpeleothem(level, pos, direction, length, mergedTip, Blocks.DRIPSTONE_BLOCK, Blocks.POINTED_DRIPSTONE, replaceable());
    }

    /** 26.1.2 DripstoneUtils.placeDripstoneBlockIfPossible(level, pos), its body (state.is(tag) reads the tag as bound now) */
    public static boolean placeDripstoneBlockIfPossible(LevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos).is(BlockTags.DRIPSTONE_REPLACEABLE)) {
            level.setBlock(pos, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), 2);
            return true;
        }
        return false;
    }

    // ---- LargeDripstoneConfiguration: 26.2 put the replaceable set first ----

    /** the replaceable set a 26.1.2 new LargeDripstoneConfiguration(...) / super(...) gets: 26.2's CaveFeatures passes #dripstone_replaceable_blocks */
    public static HolderSet<Block> dripstoneReplaceable() {
        return replaceable();
    }

    /** 26.1.2's LargeDripstoneConfiguration.CODEC: its field names, ranges and defaults (javap), no replaceable_blocks */
    private static final Codec<LargeDripstoneConfiguration> LARGE = RecordCodecBuilder.create(i -> i.group(
            Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").orElse(30).forGetter(c -> c.floorToCeilingSearchRange),
            IntProviders.codec(1, 60).fieldOf("column_radius").forGetter(c -> c.columnRadius),
            FloatProviders.codec(0.0F, 20.0F).fieldOf("height_scale").forGetter(c -> c.heightScale),
            Codec.floatRange(0.1F, 1.0F).fieldOf("max_column_radius_to_cave_height_ratio").forGetter(c -> c.maxColumnRadiusToCaveHeightRatio),
            FloatProviders.codec(0.1F, 10.0F).fieldOf("stalactite_bluntness").forGetter(c -> c.stalactiteBluntness),
            FloatProviders.codec(0.1F, 10.0F).fieldOf("stalagmite_bluntness").forGetter(c -> c.stalagmiteBluntness),
            FloatProviders.codec(0.0F, 2.0F).fieldOf("wind_speed").forGetter(c -> c.windSpeed),
            Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter(c -> c.minRadiusForWind),
            Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter(c -> c.minBluntnessForWind))
        .apply(i, (range, radius, scale, ratio, stalactite, stalagmite, wind, minRadius, minBluntness) -> new LargeDripstoneConfiguration(
            replaceable(), range, radius, scale, ratio, stalactite, stalagmite, wind, minRadius, minBluntness)));

    public static Codec<LargeDripstoneConfiguration> LARGE_CODEC() {
        return LARGE;
    }

    // ---- PointedDripstoneBlock ----

    /** 26.1.2 new PointedDripstoneBlock(properties): 26.2's Blocks passes DRIPSTONE_BLOCK.defaultBlockState() as the base */
    public static PointedDripstoneBlock cbNewPointedDripstoneBlock(BlockBehaviour.Properties properties) {
        return new PointedDripstoneBlock(Blocks.DRIPSTONE_BLOCK.defaultBlockState(), properties);
    }

    /** the base state a 26.1.2 PointedDripstoneBlock subclass's super(properties) gets (as 26.2's Blocks passes) */
    public static BlockState dripstoneBase() {
        return Blocks.DRIPSTONE_BLOCK.defaultBlockState();
    }

    /**
     * 26.1.2 static PointedDripstoneBlock.growStalactiteOrStalagmiteIfPossible(state, level, pos, random): 26.2 made it
     * an instance method of SpeleothemBlock whose rules (canGrow, growth length) the block supplies; 26.1.2's static
     * was pointed dripstone's, so it is asked of Blocks.POINTED_DRIPSTONE.
     */
    public static void growStalactiteOrStalagmiteIfPossible(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ((SpeleothemBlock) Blocks.POINTED_DRIPSTONE).growStalactiteOrStalagmiteIfPossible(state, level, pos, random);
    }

    /**
     * 26.1.2 PointedDripstoneBlock.canDrip(state), its body: a pointed dripstone pointing down, at its tip, not
     * waterlogged. (26.2's isFreeHangingStalactite, the member in its place, tests the #speleothems tag instead of the
     * block.)
     */
    public static boolean canDrip(BlockState state) {
        return state.is(Blocks.POINTED_DRIPSTONE) && state.getValue(SpeleothemBlock.TIP_DIRECTION) == Direction.DOWN
            && state.getValue(SpeleothemBlock.THICKNESS) == SpeleothemThickness.TIP && !state.getValue(SpeleothemBlock.WATERLOGGED);
    }
}
