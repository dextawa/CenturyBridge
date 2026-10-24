package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadAtlas;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 1.21.11's per-block chunk layer (ItemBlockRenderTypes' block table, filled by Fabric API's
 * BlockRenderLayerMap.putBlock(s)) on 26.1, which has no per-block layer: every quad carries its own layer and
 * item render type in its MaterialInfo, derived from its sprite's transparency.
 * <p>
 * 1.21.11 rendered every quad of a mapped block in the mapped layer: in the section compiler, and through
 * getRenderType / getMovingBlockRenderType wherever the block was drawn as an entity or a block item (item quads on
 * the block atlas took the translucent block-item sheet when the layer was TRANSLUCENT, the cutout one otherwise).
 * Here a mapped block's model is answered by 26.1's BlockStateModelSet (mixin/CbFapiBlockStateModelSetMixin) as a
 * wrapper whose quads carry the mapped layer and the matching item render type -- collected parts and emitted
 * (Fabric renderer) quads alike -- so every 26.1 path that reads a quad's layer renders it as 1.21.11 did; a block
 * item of a mapped block gets its item-model quads the same way (mixin/CbFapiCuboidItemModelMixin). Leaves keep
 * 1.21.11's precedence: they ignore the table and follow the cutout-leaves flag, which 26.1 also applies itself.
 * <p>
 * For a block that is not mapped, 1.21.11 answered its vanilla table; on 26.1 the layer is the one the block's
 * model actually renders in: TRANSLUCENT if any of its quads is translucent, else CUTOUT if any is cut out, else SOLID.
 * <p>
 * Limit: a Fabric-renderer quad that named its own layer kept it in 1.21.11 even in a mapped block; a 26.1 quad
 * always names one, so in a mapped block every quad takes the mapped layer.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiBlockLayers {
    private CbFapiBlockLayers() {
    }

    /** 1.21.11 ItemBlockRenderTypes.TYPE_BY_BLOCK entries put by BlockRenderLayerMap.putBlock(s) */
    private static final Map<Block, ChunkSectionLayer> LAYERS = new ConcurrentHashMap<>();

    /** 1.21.11 ItemBlockRenderTypes.cutoutLeaves: set by the level renderer from the option on every allChanged */
    private static volatile boolean cutoutLeaves;

    // ---- BlockRenderLayerMap ----

    /** 1.21.11 BlockRenderLayerMap.putBlock */
    public static void putBlock(Block block, ChunkSectionLayer layer) {
        Objects.requireNonNull(block, "block must not be null");
        Objects.requireNonNull(layer, "render layer must not be null");
        LAYERS.put(block, layer);
    }

    /** 1.21.11 BlockRenderLayerMap.putBlocks */
    public static void putBlocks(ChunkSectionLayer layer, Block... blocks) {
        for (Block block : blocks) {
            putBlock(block, layer);
        }
    }

    // ---- ItemBlockRenderTypes ----

    /** 1.21.11 ItemBlockRenderTypes.getChunkRenderType */
    public static ChunkSectionLayer getChunkRenderType(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return cutoutLeaves ? ChunkSectionLayer.CUTOUT : ChunkSectionLayer.SOLID;
        }
        ChunkSectionLayer layer = LAYERS.get(block);
        return layer != null ? layer : modelLayer(state);
    }

    /** 1.21.11 ItemBlockRenderTypes.getMovingBlockRenderType: the moving-block render type of the block's layer */
    public static RenderType getMovingBlockRenderType(BlockState state) {
        ChunkSectionLayer layer = getChunkRenderType(state);
        if (layer == ChunkSectionLayer.TRANSLUCENT) {
            return RenderTypes.translucentMovingBlock();
        }
        if (layer == ChunkSectionLayer.CUTOUT) {
            return RenderTypes.cutoutMovingBlock();
        }
        return RenderTypes.solidMovingBlock();
    }

    /** 1.21.11 ItemBlockRenderTypes.getRenderType: the block sheet of the block's layer */
    public static RenderType getRenderType(BlockState state) {
        return getChunkRenderType(state) == ChunkSectionLayer.TRANSLUCENT ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockSheet();
    }

    /**
     * 1.21.11 ItemBlockRenderTypes.getRenderLayer(FluidState): the layer of the fluid's 26.1 model, which for a fluid
     * BlockRenderLayerMap.putFluid(s) mapped is the mapped one (rt/CbFapiFluids.model)
     */
    public static ChunkSectionLayer getRenderLayer(FluidState state) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state).layer();
    }

    /** 1.21.11 ItemBlockRenderTypes.setCutoutLeaves */
    public static void setCutoutLeaves(boolean value) {
        cutoutLeaves = value;
    }

    /** the cutout-leaves flag the section compiler reads (mixin/CbFapiSectionCompilerMixin) */
    public static boolean cutoutLeaves() {
        return cutoutLeaves;
    }

    /** the layer the state's 26.1 model renders in */
    private static ChunkSectionLayer modelLayer(BlockState state) {
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
        if (model.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT)) {
            return ChunkSectionLayer.TRANSLUCENT;
        }
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);
        ChunkSectionLayer found = ChunkSectionLayer.SOLID;
        for (BlockStateModelPart part : parts) {
            for (Direction direction : FACES) {
                for (BakedQuad quad : part.getQuads(direction)) {
                    ChunkSectionLayer layer = quad.materialInfo().layer();
                    if (layer == ChunkSectionLayer.TRANSLUCENT) {
                        return layer;
                    }
                    if (layer == ChunkSectionLayer.CUTOUT) {
                        found = layer;
                    }
                }
            }
        }
        return found;
    }

    private static final Direction[] FACES = {null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    // ---- applying the table ----

    /** the block's mapped layer, or null when 1.21.11 would not have applied the table to it */
    public static ChunkSectionLayer mapped(Block block) {
        if (LAYERS.isEmpty() || block instanceof LeavesBlock) {
            return null;
        }
        return LAYERS.get(block);
    }

    /**
     * BlockStateModelSet.get's answer for a state: the model itself, or for a mapped block its relayered wrapper
     * (kept per model set in {@code cache}, one per model and layer)
     */
    public static BlockStateModel model(Map<BlockStateModel, BlockStateModel[]> cache, BlockState state, BlockStateModel model) {
        ChunkSectionLayer layer = mapped(state.getBlock());
        if (layer == null || model instanceof Relayered) {
            return model;
        }
        synchronized (cache) {
            BlockStateModel[] byLayer = cache.computeIfAbsent(model, m -> new BlockStateModel[ChunkSectionLayer.values().length]);
            BlockStateModel wrapper = byLayer[layer.ordinal()];
            if (wrapper == null) {
                wrapper = new Relayered(model, layer);
                byLayer[layer.ordinal()] = wrapper;
            }
            return wrapper;
        }
    }

    /** a new per-model-set cache for {@link #model} */
    public static Map<BlockStateModel, BlockStateModel[]> newCache() {
        return new IdentityHashMap<>();
    }

    /** the item render type 1.21.11 gave a quad of a block in {@code layer}: block-atlas quads by layer, others unchanged */
    private static RenderType itemRenderType(boolean blockAtlas, ChunkSectionLayer layer, RenderType current) {
        if (!blockAtlas) {
            return current;
        }
        return layer.translucent() ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockItemSheet();
    }

    /** the quad in {@code layer}, with the item render type that goes with it */
    public static BakedQuad relayer(BakedQuad quad, ChunkSectionLayer layer) {
        BakedQuad.MaterialInfo info = quad.materialInfo();
        boolean blockAtlas = TextureAtlas.LOCATION_BLOCKS.equals(info.sprite().atlasLocation());
        RenderType type = itemRenderType(blockAtlas, layer, info.itemRenderType());
        if (info.layer() == layer && info.itemRenderType() == type) {
            return quad;
        }
        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(), quad.direction(),
                new BakedQuad.MaterialInfo(info.sprite(), layer, type, info.tintIndex(), info.shade(), info.lightEmission()));
    }

    private static List<BakedQuad> relayer(List<BakedQuad> quads, ChunkSectionLayer layer) {
        if (quads.isEmpty()) {
            return quads;
        }
        List<BakedQuad> out = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            out.add(relayer(quad, layer));
        }
        return Collections.unmodifiableList(out);
    }

    private static int flags(int flags, ChunkSectionLayer layer) {
        return layer.translucent() ? flags | BakedQuad.FLAG_TRANSLUCENT : flags & ~BakedQuad.FLAG_TRANSLUCENT;
    }

    /** a mapped block's model: every quad, collected or emitted, in the mapped layer */
    private static final class Relayered extends WrapperBlockStateModel implements FabricBlockStateModel {
        private final ChunkSectionLayer layer;
        private final Map<BlockStateModelPart, BlockStateModelPart> parts = Collections.synchronizedMap(new IdentityHashMap<>());

        Relayered(BlockStateModel wrapped, ChunkSectionLayer layer) {
            super(wrapped);
            this.layer = layer;
        }

        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> out) {
            List<BlockStateModelPart> collected = new ArrayList<>();
            this.wrapped.collectParts(random, collected);
            for (BlockStateModelPart part : collected) {
                out.add(this.parts.computeIfAbsent(part, p -> new RelayeredPart(p, this.layer)));
            }
        }

        @Override
        public int materialFlags() {
            return flags(this.wrapped.materialFlags(), this.layer);
        }

        @Override
        public boolean hasMaterialFlag(int flag) {
            return (materialFlags() & flag) != 0;
        }

        @Override
        public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
            return flags(((FabricBlockStateModel) (Object) this.wrapped).materialFlags(level, pos, state, random), this.layer);
        }

        @Override
        public boolean hasMaterialFlag(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, int flag) {
            return (materialFlags(level, pos, state, random) & flag) != 0;
        }

        @Override
        public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
                              Predicate<Direction> cullTest) {
            emitter.pushTransform(this::transform);
            try {
                ((FabricBlockStateModel) (Object) this.wrapped).emitQuads(emitter, level, pos, state, random, cullTest);
            } finally {
                emitter.popTransform();
            }
        }

        private boolean transform(MutableQuadView quad) {
            quad.chunkLayer(this.layer);
            quad.itemRenderType(itemRenderType(quad.atlas() == QuadAtlas.BLOCK, this.layer, quad.itemRenderType()));
            return true;
        }
    }

    /** a part of a mapped block's model, its quads in the mapped layer (computed once per face) */
    private static final class RelayeredPart implements BlockStateModelPart {
        private final BlockStateModelPart part;
        private final ChunkSectionLayer layer;
        @SuppressWarnings("unchecked")
        private final List<BakedQuad>[] quads = new List[FACES.length];

        RelayeredPart(BlockStateModelPart part, ChunkSectionLayer layer) {
            this.part = part;
            this.layer = layer;
        }

        @Override
        public List<BakedQuad> getQuads(Direction direction) {
            int index = direction == null ? 0 : direction.ordinal() + 1;
            List<BakedQuad> list = this.quads[index];
            if (list == null) {
                list = relayer(this.part.getQuads(direction), this.layer);
                this.quads[index] = list;
            }
            return list;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.part.useAmbientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.part.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return flags(this.part.materialFlags(), this.layer);
        }
    }

    // ---- block items ----

    private static final Map<QuadCollection, Object[]> ITEM_QUADS = Collections.synchronizedMap(new WeakHashMap<>());

    /** a block-item model's quads (CuboidItemModelWrapper.update): those of a mapped block's item in the mapped layer */
    public static List<BakedQuad> itemQuads(QuadCollection quads, ItemStack stack) {
        List<BakedQuad> all = quads.getAll();
        if (LAYERS.isEmpty() || !(stack.getItem() instanceof BlockItem item)) {
            return all;
        }
        ChunkSectionLayer layer = mapped(item.getBlock());
        if (layer == null) {
            return all;
        }
        Object[] cached = ITEM_QUADS.get(quads);
        if (cached != null && cached[0] == layer) {
            @SuppressWarnings("unchecked")
            List<BakedQuad> list = (List<BakedQuad>) cached[1];
            return list;
        }
        List<BakedQuad> list = relayer(all, layer);
        ITEM_QUADS.put(quads, new Object[]{layer, list});
        return list;
    }
}
