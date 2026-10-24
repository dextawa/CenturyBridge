package top.dext.centurybridge.step.v26_1.rt;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;

/**
 * 1.21.11's tree "dirt" on 26.1's below-trunk provider. 1.21.11's TreeConfiguration held a dirtProvider and a
 * forceDirt flag: below the trunk TrunkPlacer.setDirtAt set the dirt provider's state unless forceDirt was off
 * and the block there was already dirt (1.21.11's dirt tag but grass and mycelium). 26.1 holds one provider
 * asked for the state below the trunk (none: left as it is); its default, PLACE_BELOW_OVERWORLD_TRUNKS, is dirt
 * where the block is not in cannot_replace_below_tree_trunk -- the same blocks 1.21.11 left (its dirt tag, mud,
 * moss and podzol). So 1.21.11's pair is: forced, the dirt provider itself; else that rule with the dirt
 * provider (vanilla's own object for plain dirt); and read back the other way.
 */
public final class CbWallTrees {

    private CbWallTrees() {
    }

    /** 1.21.11's (dirtProvider, forceDirt) as 26.1's below-trunk provider */
    public static BlockStateProvider belowTrunk(BlockStateProvider dirt, boolean force) {
        if (force) {
            return dirt;
        }
        if (isPlainDirt(dirt)) {
            return TreeConfiguration.PLACE_BELOW_OVERWORLD_TRUNKS;
        }
        return RuleBasedStateProvider.ifTrueThenProvide(TreeConfiguration.CAN_PLACE_BELOW_OVERWORLD_TRUNKS, dirt);
    }

    private static boolean isPlainDirt(BlockStateProvider provider) {
        return provider instanceof SimpleStateProvider s && s.getState(null, null, null) == Blocks.DIRT.defaultBlockState();
    }

    /** the rule a non-forced 1.21.11 dirt provider stands in, or null */
    private static RuleBasedStateProvider.Rule unforced(BlockStateProvider below) {
        if (below instanceof RuleBasedStateProvider r && r.fallback == null && r.rules.size() == 1
                && r.rules.get(0).ifTrue() == TreeConfiguration.CAN_PLACE_BELOW_OVERWORLD_TRUNKS) {
            return r.rules.get(0);
        }
        return null;
    }

    /** TreeConfiguration.dirtProvider */
    public static BlockStateProvider dirtProvider(TreeConfiguration config) {
        RuleBasedStateProvider.Rule rule = unforced(config.belowTrunkProvider);
        return rule != null ? rule.then() : config.belowTrunkProvider;
    }

    /** TreeConfiguration.forceDirt */
    public static boolean forceDirt(TreeConfiguration config) {
        return unforced(config.belowTrunkProvider) == null;
    }

    /** 1.21.11's builder default: dirt, not forced -- 26.1's default below-trunk provider */
    public static BlockStateProvider defaultBelowTrunk() {
        return TreeConfiguration.PLACE_BELOW_OVERWORLD_TRUNKS;
    }

    // ---- the builder: 1.21.11's dirt(provider) and forceDirt() kept per builder

    private record Dirt(BlockStateProvider provider, boolean force) {
    }

    private static final Map<TreeConfiguration.TreeConfigurationBuilder, Dirt> BUILDERS = new WeakHashMap<>();

    private static Dirt dirt(TreeConfiguration.TreeConfigurationBuilder builder) {
        synchronized (BUILDERS) {
            return BUILDERS.getOrDefault(builder, new Dirt(BlockStateProvider.simple(Blocks.DIRT), false));
        }
    }

    private static TreeConfiguration.TreeConfigurationBuilder set(TreeConfiguration.TreeConfigurationBuilder builder, Dirt dirt) {
        synchronized (BUILDERS) {
            BUILDERS.put(builder, dirt);
        }
        return builder.belowTrunkProvider(belowTrunk(dirt.provider(), dirt.force()));
    }

    /** TreeConfigurationBuilder.dirt(provider) */
    public static TreeConfiguration.TreeConfigurationBuilder dirt(TreeConfiguration.TreeConfigurationBuilder builder, BlockStateProvider provider) {
        return set(builder, new Dirt(provider, dirt(builder).force()));
    }

    /** TreeConfigurationBuilder.forceDirt() */
    public static TreeConfiguration.TreeConfigurationBuilder forceDirt(TreeConfiguration.TreeConfigurationBuilder builder) {
        return set(builder, new Dirt(dirt(builder).provider(), true));
    }

    /**
     * TrunkPlacer.setDirtAt(level, setter, random, pos, config): 26.1's placeBelowTrunkBlock body (the level a
     * trunk placer is handed is a WorldGenLevel in 26.1); on another level, 1.21.11's body over the dirt pair
     */
    public static void setDirtAt(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> setter, RandomSource random, BlockPos pos,
            TreeConfiguration config) {
        if (level instanceof WorldGenLevel w) {
            BlockState state = config.belowTrunkProvider.getOptionalState(w, random, pos);
            if (state != null) {
                setter.accept(pos, state);
            }
            return;
        }
        if (forceDirt(config) || !level.isStateAtPosition(pos, s -> CbWallStatics.isDirt(s) && !s.is(Blocks.GRASS_BLOCK) && !s.is(Blocks.MYCELIUM))) {
            setter.accept(pos, CbWallWorldGen.getState(dirtProvider(config), random, pos));
        }
    }
}
