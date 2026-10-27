package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.special.BedSpecialRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import top.dext.centurybridge.step.v26_1.facade.SpecialBlockModelRenderer;

/**
 * 1.21.11's block special models (SpecialModelRenderers.createBlockRenderers, ModelManager.specialBlockModelRenderer,
 * facade/SpecialBlockModelRenderer) on 26.1's special models. 26.1 keeps a block's special model in the block's model
 * (BuiltInBlockModels: a special model and the transformation placing it per block state); 1.21.11 kept one per block,
 * its renderer posing itself, drawn at the block's origin.
 */
@Environment(EnvType.CLIENT)
public final class CbWallSpecialBlocks {

    private CbWallSpecialBlocks() {
    }

    /** 1.21.11 SpecialModelRenderers.STATIC_BLOCK_MAPPING (field_55454, rt/CbWallSpecialBlockMap) */
    private static final Map<Block, SpecialModelRenderer.Unbaked<?>> STATIC_BLOCK_MAPPING;
    /** 1.21.11 SpecialModelRenderers' Christmas chest (field_55455): new ChestSpecialRenderer.Unbaked(GIFT_CHEST_TEXTURE) */
    private static final SpecialModelRenderer.Unbaked<?> XMAS_CHEST = CbWallItemModels.chest(CbWallSpriteIds.giftChest(), 0.0F);

    static {
        ImmutableMap.Builder<Block, SpecialModelRenderer.Unbaked<?>> builder = ImmutableMap.builder();
        CbWallSpecialBlockMap.put(builder::put);
        STATIC_BLOCK_MAPPING = builder.build();
    }

    /**
     * 1.21.11 SpecialModelRenderers.createBlockRenderers(context) (javap class_10517.method_65714): the static table,
     * the Christmas chest for CHEST and TRAPPED_CHEST when ChestRenderer.xmasTextures(), each baked (a null renderer
     * left out) -- baked as 1.21.11's renderers were, posing themselves ({@link #legacyBake}). 1.21.11's Fabric API put
     * SpecialBlockRendererRegistry's blocks into the static table itself (SpecialModelRenderersMixin.onReturnClinit,
     * SpecialBlockRendererRegistryImpl.setup / register): they are in it here too (rt/CbFapiSpecialBlocks).
     */
    public static Map<Block, SpecialModelRenderer<?>> createBlockRenderers(SpecialModelRenderer.BakingContext context) {
        Map<Block, SpecialModelRenderer.Unbaked<?>> unbaked = new HashMap<>(STATIC_BLOCK_MAPPING);
        unbaked.putAll(CbFapiSpecialBlocks.registered());
        if (ChestRenderer.xmasTextures()) {
            unbaked.put(Blocks.CHEST, XMAS_CHEST);
            unbaked.put(Blocks.TRAPPED_CHEST, XMAS_CHEST);
        }
        ImmutableMap.Builder<Block, SpecialModelRenderer<?>> builder = ImmutableMap.builder();
        unbaked.forEach((block, model) -> {
            SpecialModelRenderer<?> renderer = legacyBake(model, context);
            if (renderer != null) {
                builder.put(block, renderer);
            }
        });
        return builder.build();
    }

    /**
     * A special model baked into the renderer 1.21.11 baked from it: a shulker box posing itself by its orientation, a
     * whole bed (CbWallItemModels.bakeShulkerBox, CbWallBed.bake), any other 26.1 renderer drawn in the pose 1.21.11's
     * renderer of that model applied (CbWallItemModels.legacyPose; none for chests, decorated pots and models 1.21.11
     * did not have).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static SpecialModelRenderer<?> legacyBake(SpecialModelRenderer.Unbaked<?> unbaked, SpecialModelRenderer.BakingContext context) {
        if (unbaked instanceof ShulkerBoxSpecialRenderer.Unbaked shulker) {
            return CbWallItemModels.bakeShulkerBox(shulker, context);
        }
        if (unbaked instanceof BedSpecialRenderer.Unbaked bed && CbWallBed.whole(bed)) {
            return CbWallBed.bake(bed, context);
        }
        SpecialModelRenderer renderer = unbaked.bake(context);
        Optional<com.mojang.math.Transformation> pose = CbWallItemModels.legacyPose(unbaked);
        return renderer != null && pose.isPresent() ? new CbWallPosedSpecialRenderer<>(renderer, pose.get()) : renderer;
    }

    /** 1.21.11's ModelManager.specialBlockModelRenderer(), declared by a mod's model manager (adapterHooks) */
    public interface LegacySpecialBlocks {
        SpecialBlockModelRenderer specialBlockModelRenderer();
    }

    /** the baking context 1.21.11's model manager gave SpecialBlockModelRenderer.vanilla: its models, the atlas sprites, the skin cache */
    private record Context(EntityModelSet entityModelSet, SpriteGetter sprites, PlayerSkinRenderCache playerSkinRenderCache)
        implements SpecialModelRenderer.BakingContext {
    }

    /** per model manager: the entity models of its last reload and the renderer baked from them */
    private static final Map<ModelManager, Object[]> BAKED = new WeakHashMap<>();

    /** a mod's call of 1.21.11's ModelManager.specialBlockModelRenderer(): a 1.21.11 override's answer, else 1.21.11's field */
    public static SpecialBlockModelRenderer specialBlockModelRenderer(ModelManager manager) {
        if (manager instanceof LegacySpecialBlocks legacy) {
            return legacy.specialBlockModelRenderer();
        }
        return superSpecialBlockModelRenderer(manager);
    }

    /**
     * 1.21.11's ModelManager.specialBlockModelRenderer field: SpecialBlockModelRenderer.EMPTY until the first reload,
     * then SpecialBlockModelRenderer.vanilla(new BakingContext.Simple(entityModels, atlas sprites, skin cache)) of each
     * reload (javap class_1092.method_73040). 26.1's manager gets a new entity model set on each reload; the renderer is
     * baked once per set.
     */
    public static SpecialBlockModelRenderer superSpecialBlockModelRenderer(ModelManager manager) {
        EntityModelSet models = manager.entityModels().get();
        if (models == EntityModelSet.EMPTY) {
            return SpecialBlockModelRenderer.field_55269;
        }
        synchronized (BAKED) {
            Object[] baked = BAKED.get(manager);
            if (baked == null || baked[0] != models) {
                Minecraft minecraft = Minecraft.getInstance();
                baked = new Object[] {models,
                    SpecialBlockModelRenderer.method_65536(new Context(models, minecraft.getAtlasManager(), minecraft.playerSkinRenderCache()))};
                BAKED.put(manager, baked);
            }
            return (SpecialBlockModelRenderer) baked[1];
        }
    }
}
