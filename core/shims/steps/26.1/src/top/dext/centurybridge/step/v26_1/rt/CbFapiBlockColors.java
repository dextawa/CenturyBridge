package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import top.dext.centurybridge.step.v26_1.facade.BlockColor;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;

/**
 * 1.21.11's BlockColor (one function of the tint index, asked with or without a world) and 26.1's per-block list of
 * BlockTintSource (one source per tint index) converted into each other.
 * <ul>
 * <li>A BlockColor registered for a block (BlockColors.register, Fabric API's ColorProviderRegistry.BLOCK) becomes a
 *     live {@link TintList}: source i answers getColor(state, level, pos, i), with no world for {@code color(state)}
 *     as 1.21.11 asked without one. 26.1 reads as many tints as the list is long (block models, terrain), and
 *     1.21.11 asked the BlockColor for exactly the tint indices the block's quads carry, so the list is as long as the
 *     highest tint index of the block's current models (all states, every variant) plus one -- at least one, since
 *     1.21.11 asked index 0 for particles. An index past that (a Fabric model emitting further tinted quads) is
 *     still answered through BlockColors.getTintSource (mixin/CbFapiBlockColorsMixin).</li>
 * <li>A 26.1 list asked for as a BlockColor (ColorProviderRegistry.BLOCK.get) answers source i, in the world when one
 *     is given, -1 past the list; the BlockColor a list was made from is returned as itself.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbFapiBlockColors {
    private CbFapiBlockColors() {
    }

    /** variants sampled per state when measuring the tint indices a block's models carry */
    private static final int SEEDS = 64;
    private static final Direction[] FACES = {null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    // ---- Fabric API ColorProviderRegistry.BLOCK ----

    /** 1.21.11 ColorProviderRegistry.BLOCK.register: through 26.1's BlockColorRegistry (which defers until the game's BlockColors exist) */
    public static void registerProvider(BlockColor provider, Block... blocks) {
        for (Block block : blocks) {
            BlockColorRegistry.register(tintSources(provider, block), block);
        }
    }

    /** 1.21.11 ColorProviderRegistry.BLOCK.get: null until the game's BlockColors exist, as 1.21.11's was */
    public static BlockColor getProvider(Block block) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockColors colors = minecraft == null ? null : minecraft.getBlockColors();
        if (colors == null) {
            return null;
        }
        return toBlockColor(colors.getTintSources(block.defaultBlockState()));
    }

    // ---- vanilla BlockColors ----

    /** 1.21.11 BlockColors.register(BlockColor, Block...) */
    public static void register(BlockColors colors, BlockColor color, Block... blocks) {
        for (Block block : blocks) {
            colors.register(tintSources(color, block), block);
        }
    }

    /** 1.21.11 BlockColors.getColor(BlockState, BlockAndTintGetter, BlockPos, int): -1 when the block has no colour at that index */
    public static int getColor(BlockColors colors, BlockState state, BlockAndLightGetter level, BlockPos pos, int tintIndex) {
        BlockTintSource source = colors.getTintSource(state, tintIndex);
        return source == null ? -1 : color(source, state, level, pos);
    }

    /**
     * 1.21.11 BlockColors.getColor(BlockState, Level, BlockPos), the particle colour: a coloured block's colour at index
     * 0 without a world, otherwise the state's map colour (-1 for none)
     */
    public static int getColor(BlockColors colors, BlockState state, Level level, BlockPos pos) {
        if (!colors.getTintSources(state).isEmpty()) {
            BlockTintSource source = colors.getTintSource(state, 0);
            return source == null ? -1 : source.color(state);
        }
        MapColor mapColor = state.getMapColor(level, pos);
        return mapColor != null ? mapColor.col : -1;
    }

    // ---- conversion ----

    /** the 26.1 tint sources of a BlockColor registered for {@code block} */
    public static List<BlockTintSource> tintSources(BlockColor color, Block block) {
        Objects.requireNonNull(color, "color");
        Objects.requireNonNull(block, "block");
        return new TintList(color, block);
    }

    /** a 26.1 tint source list seen as a 1.21.11 BlockColor (null for a block without colours) */
    public static BlockColor toBlockColor(List<BlockTintSource> sources) {
        if (sources instanceof TintList list) {
            return list.color;
        }
        if (sources.isEmpty()) {
            return null;
        }
        return (state, level, pos, tintIndex) -> {
            if (tintIndex < 0 || tintIndex >= sources.size()) {
                return -1;
            }
            BlockTintSource source = sources.get(tintIndex);
            return source == null ? -1 : color(source, state, level, pos);
        };
    }

    /** a source's colour: in the world when 1.21.11 gave one, else without */
    private static int color(BlockTintSource source, BlockState state, BlockAndLightGetter level, BlockPos pos) {
        if (level != null && pos != null) {
            return source.colorInWorld(state, CbWallTintClient.toTint(level), pos);
        }
        return source.color(state);
    }

    /** BlockColors.getTintSource's answer past a converted BlockColor's measured length (mixin/CbFapiBlockColorsMixin) */
    public static BlockTintSource beyond(List<BlockTintSource> sources, int tintIndex) {
        return sources instanceof TintList list && tintIndex >= 0 ? new Source(list.color, tintIndex) : null;
    }

    /** source {@code index} of a 1.21.11 BlockColor */
    private record Source(BlockColor color, int index) implements BlockTintSource {
        @Override
        public int color(BlockState state) {
            return this.color.getColor(state, null, null, this.index);
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            return this.color.getColor(state, level, pos, this.index);
        }
    }

    /** a 1.21.11 BlockColor as the tint sources of one block, as long as the block's models need */
    private static final class TintList extends AbstractList<BlockTintSource> implements RandomAccess {
        final BlockColor color;
        private final Block block;
        private volatile Object measuredFor;
        private volatile int size = 1;

        TintList(BlockColor color, Block block) {
            this.color = color;
            this.block = block;
        }

        @Override
        public BlockTintSource get(int index) {
            Objects.checkIndex(index, size());
            return new Source(this.color, index);
        }

        @Override
        public int size() {
            Minecraft minecraft = Minecraft.getInstance();
            ModelManager models = minecraft == null ? null : minecraft.getModelManager();
            BlockStateModelSet set = models == null ? null : models.getBlockStateModelSet();
            if (set == null) {
                return 1;
            }
            if (this.measuredFor != set) {
                synchronized (this) {
                    if (this.measuredFor != set) {
                        this.size = measure(set, this.block);
                        this.measuredFor = set;
                    }
                }
            }
            return this.size;
        }
    }

    /** the highest tint index of the block's models plus one, at least one */
    private static int measure(BlockStateModelSet set, Block block) {
        int max = 0;
        List<BlockStateModelPart> parts = new ArrayList<>();
        Set<BlockStateModel> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            BlockStateModel model = set.get(state);
            if (!seen.add(model)) {
                continue;
            }
            int seeds = model instanceof SingleVariant ? 1 : SEEDS;
            for (int seed = 0; seed < seeds; seed++) {
                parts.clear();
                model.collectParts(RandomSource.create(seed), parts);
                for (BlockStateModelPart part : parts) {
                    for (Direction face : FACES) {
                        for (BakedQuad quad : part.getQuads(face)) {
                            max = Math.max(max, quad.materialInfo().tintIndex() + 1);
                        }
                    }
                }
            }
        }
        return Math.max(max, 1);
    }
}
