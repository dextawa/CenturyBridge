package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_1.facade.BlockSubmit;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * 1.21.11's block submissions' records on 26.1's.
 * <ul>
 * <li>SubmitNodeStorage.BlockModelSubmit(pose, renderType, model, r, g, b, light, overlay, outline): 1.21.11's
 *     BlockFeatureRenderer drew it with ModelBlockRenderer.renderModel (javap class_778.method_3367): the model's parts
 *     with seed 42, every quad of every side and the unculled ones, a tinted quad in (r, g, b) clamped to [0, 1], any
 *     other white. 26.1's record holds the parts and a colour per tint index (a tinted quad whose index has none is
 *     white): the parts with seed 42 and (r, g, b) for every tint index the parts' quads use.</li>
 * <li>Its model() / red() / green() / blue(): what the 1.21.11 constructor was given; for a record 26.1 made, a model of
 *     its parts and the colour of tint index 0 (the block colour 26.1's callers put there; white without one).</li>
 * <li>BlockBreakingRenderState(level, pos, progress): 26.1's record of the position, the level's block state there
 *     (1.21.11 read it the same way) and the progress.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallBlockSubmits {

    private CbWallBlockSubmits() {
    }

    /** what a 1.21.11 BlockModelSubmit constructor was given: model, r, g, b */
    private record Legacy(BlockStateModel model, float red, float green, float blue) {
    }

    private static final Map<SubmitNodeStorage.BlockModelSubmit, Legacy> LEGACY = new MapMaker().weakKeys().makeMap();

    /** 1.21.11 new SubmitNodeStorage.BlockModelSubmit(pose, renderType, model, r, g, b, light, overlay, outline) */
    public static SubmitNodeStorage.BlockModelSubmit blockModelSubmit(PoseStack.Pose pose, RenderType renderType, BlockStateModel model, float red,
                                                                     float green, float blue, int light, int overlay, int outline) {
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);
        int maxTint = -1;
        for (BlockStateModelPart part : parts) {
            for (Direction side : SIDES) {
                for (BakedQuad quad : part.getQuads(side)) {
                    maxTint = Math.max(maxTint, quad.materialInfo().tintIndex());
                }
            }
        }
        int[] tints = new int[maxTint + 1];
        java.util.Arrays.fill(tints, ARGB.colorFromFloat(1.0F, Mth.clamp(red, 0.0F, 1.0F), Mth.clamp(green, 0.0F, 1.0F), Mth.clamp(blue, 0.0F, 1.0F)));
        SubmitNodeStorage.BlockModelSubmit submit = new SubmitNodeStorage.BlockModelSubmit(pose, renderType, parts, tints, light, overlay, outline);
        LEGACY.put(submit, new Legacy(model, red, green, blue));
        return submit;
    }

    /** the six sides and the unculled quads (null), as 1.21.11's renderModel walked them */
    private static final Direction[] SIDES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null};

    /** 1.21.11 BlockModelSubmit.model() */
    public static BlockStateModel model(SubmitNodeStorage.BlockModelSubmit submit) {
        Legacy legacy = LEGACY.get(submit);
        return legacy != null ? legacy.model() : new PartsModel(submit.modelParts());
    }

    /** 1.21.11 BlockModelSubmit.r() */
    public static float red(SubmitNodeStorage.BlockModelSubmit submit) {
        Legacy legacy = LEGACY.get(submit);
        return legacy != null ? legacy.red() : ARGB.red(tint0(submit)) / 255.0F;
    }

    /** 1.21.11 BlockModelSubmit.g() */
    public static float green(SubmitNodeStorage.BlockModelSubmit submit) {
        Legacy legacy = LEGACY.get(submit);
        return legacy != null ? legacy.green() : ARGB.green(tint0(submit)) / 255.0F;
    }

    /** 1.21.11 BlockModelSubmit.b() */
    public static float blue(SubmitNodeStorage.BlockModelSubmit submit) {
        Legacy legacy = LEGACY.get(submit);
        return legacy != null ? legacy.blue() : ARGB.blue(tint0(submit)) / 255.0F;
    }

    private static int tint0(SubmitNodeStorage.BlockModelSubmit submit) {
        int[] tints = submit.tintLayers();
        return tints.length > 0 ? tints[0] : -1;
    }

    /** a model of a 26.1 record's parts: collects them whatever the random; particle and flags as the first part / all parts */
    private record PartsModel(List<BlockStateModelPart> parts) implements BlockStateModel {
        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
            output.addAll(this.parts);
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.parts.isEmpty() ? null : this.parts.get(0).particleMaterial();
        }

        @Override
        public int materialFlags() {
            int flags = 0;
            for (BlockStateModelPart part : this.parts) {
                flags |= part.materialFlags();
            }
            return flags;
        }
    }

    /** 1.21.11 new BlockBreakingRenderState(level, pos, progress) (javap class_12073.&lt;init&gt;: blockState = level.getBlockState(pos)) */
    public static BlockBreakingRenderState blockBreaking(ClientLevel level, BlockPos pos, int progress) {
        return new BlockBreakingRenderState(pos, level.getBlockState(pos), progress);
    }

    /** 1.21.11's SubmitNodeCollection.getBlockSubmits(), declared by a mod's collection subclass (adapterHooks) */
    public interface LegacyBlockSubmits {
        List<BlockSubmit> getBlockSubmits();
    }

    /**
     * 1.21.11's SubmitNodeCollection.blockSubmits: the blocks submitBlock queued in the collection since it was last
     * cleared (each frame, after rendering). 26.1's collection has no such list: one is kept per collection here and
     * starts over on the next frame (Minecraft.getFrameTimeNs, taken at the start of every frame). Adding a BlockSubmit
     * queues it as 1.21.11's BlockFeatureRenderer drew it, the block's model (CbFapiSubmitBlocks.submitBlockModelOf);
     * removing one takes it off the list only (26.1 already holds its model's submission).
     */
    private static final class BlockSubmitList extends AbstractList<BlockSubmit> {
        private final SubmitNodeCollection collection;
        private final List<BlockSubmit> submits = new ArrayList<>();
        private long frame;

        BlockSubmitList(SubmitNodeCollection collection) {
            this.collection = collection;
        }

        BlockSubmitList current() {
            long now = Minecraft.getInstance().getFrameTimeNs();
            if (now != this.frame) {
                this.frame = now;
                this.submits.clear();
            }
            return this;
        }

        @Override
        public BlockSubmit get(int index) {
            return this.submits.get(index);
        }

        @Override
        public int size() {
            return this.submits.size();
        }

        @Override
        public void add(int index, BlockSubmit submit) {
            this.submits.add(index, submit);
            this.modCount++;
            PoseStack pose = new PoseStack();
            pose.last().set(submit.comp_4484());
            CbFapiSubmitBlocks.submitBlockModelOf(CbFapiSubmitBlocks.modelTarget(this.collection), pose, submit.comp_4485(), submit.comp_4486(), submit.comp_4487(), submit.comp_4660());
        }

        @Override
        public BlockSubmit set(int index, BlockSubmit submit) {
            return this.submits.set(index, submit);
        }

        @Override
        public BlockSubmit remove(int index) {
            this.modCount++;
            return this.submits.remove(index);
        }
    }

    private static final Map<SubmitNodeCollection, BlockSubmitList> BLOCK_SUBMITS = new MapMaker().weakKeys().makeMap();

    private static BlockSubmitList list(SubmitNodeCollection collection) {
        return BLOCK_SUBMITS.computeIfAbsent(collection, BlockSubmitList::new).current();
    }

    /** submitBlock queued a BlockSubmit in the collection (SubmitNodeStorage: in order(0)'s), the pose copied (javap class_11788.method_73481) */
    public static void queued(OrderedSubmitNodeCollector collector, PoseStack pose, BlockState state, int light, int overlay, int outline) {
        SubmitNodeCollection collection = collector instanceof SubmitNodeCollection c ? c
            : collector instanceof SubmitNodeStorage storage ? storage.order(0) : null;
        if (collection != null) {
            list(collection).submits.add(new BlockSubmit(pose.last().copy(), state, light, overlay, outline));
        }
    }

    /** a mod's call of 1.21.11's getBlockSubmits(): a 1.21.11 override's answer, else the collection's list */
    public static List<BlockSubmit> blockSubmits(SubmitNodeCollection collection) {
        if (collection instanceof LegacyBlockSubmits legacy) {
            return legacy.getBlockSubmits();
        }
        return superBlockSubmits(collection);
    }

    /** 1.21.11's getBlockSubmits() body: the list */
    public static List<BlockSubmit> superBlockSubmits(SubmitNodeCollection collection) {
        return list(collection);
    }
}
