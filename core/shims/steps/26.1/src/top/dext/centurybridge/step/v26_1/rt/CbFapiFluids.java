package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.data.AtlasIds;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import top.dext.centurybridge.step.v26_1.compat.fapi.client.render.fluid.v1.FluidRenderHandler;
import top.dext.centurybridge.step.v26_1.compat.fapi.client.render.fluid.v1.FluidRenderHandlerRegistry;
import top.dext.centurybridge.step.v26_1.compat.fapi.client.render.fluid.v1.FluidRendering;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 1.21.11's Fabric fluid rendering API (FluidRenderHandlerRegistryImpl, FluidRenderingImpl, FluidRenderHandlerInfo,
 * LiquidBlockRendererMixin, BlockRenderLayerMap's fluid half and ItemBlockRenderTypes' fluid layers) on 26.1, whose
 * fluids render from a FluidModel per fluid (layer, still / flowing / overlay material, tint source):
 * <ul>
 * <li>a fluid with a 1.21.11 handler registered, or one 1.21.11 mapped to a layer, answers 26.1's
 * FluidStateModelSet.get with the model 1.21.11's tesselation drew: the handler's sprites and colour, the fluid's
 * 1.21.11 layer (mixin/CbFapiFluidStateModelSetMixin); so 26.1's own readers (the tesselation, Fabric API's
 * FluidVariantRendering) see what 1.21.11's did;</li>
 * <li>26.1's FluidRenderer.tesselate of such a fluid calls the handler's renderFluid with the consumer of the
 * fluid's layer (mixin/CbFapiFluidRendererMixin, 1.21.11's onHeadRender); the default renderFluid runs 26.1's
 * tesselation with the handler's sprites and colour for that position (1.21.11's FluidRenderHandlerInfo);</li>
 * <li>after each model reload every handler's reloadTextures gets the block atlas and the water and lava handlers
 * take the reloaded vanilla sprites (mixin/CbFapiModelManagerFluidMixin, 1.21.11's onResourceReloadReturn).</li>
 * </ul>
 * Overlay transparency is 26.1 Fabric API's own registry (the same map and default as 1.21.11's), so both APIs and
 * 26.1's overlay test read one answer.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiFluids {
    /** 1.21.11 FluidRenderHandlerRegistryImpl.handlers: water and lava, then every registration */
    private static final Map<Fluid, FluidRenderHandler> HANDLERS = new IdentityHashMap<>();
    /** 1.21.11 FluidRenderHandlerRegistryImpl.modHandlers: registrations only */
    private static final Map<Fluid, FluidRenderHandler> MOD_HANDLERS = new IdentityHashMap<>();
    /** 1.21.11 ItemBlockRenderTypes.TYPE_BY_FLUID entries put by BlockRenderLayerMap.putFluid(s) */
    private static final Map<Fluid, ChunkSectionLayer> LAYERS = new IdentityHashMap<>();
    /** 1.21.11 FluidRenderingImpl.CURRENT_DEFAULT_RENDERER */
    private static final ThreadLocal<FluidRendering.DefaultRenderer> CURRENT_DEFAULT_RENDERER = new ThreadLocal<>();
    /** 1.21.11 FluidRenderingImpl.CURRENT_INFO */
    private static final ThreadLocal<Info> CURRENT_INFO = ThreadLocal.withInitial(Info::new);
    /** set while the reload reads 26.1's own models (the vanilla sprites of water and lava) */
    private static final ThreadLocal<Boolean> RAW = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final WaterHandler WATER = new WaterHandler();
    private static final LavaHandler LAVA = new LavaHandler();
    private static final Registry REGISTRY = new Registry();
    /** 1.21.11 FluidRenderingImpl.vanillaRenderer, set at each model reload */
    private static volatile FluidRenderer vanillaRenderer;

    static {
        HANDLERS.put(Fluids.WATER, WATER);
        HANDLERS.put(Fluids.FLOWING_WATER, WATER);
        HANDLERS.put(Fluids.LAVA, LAVA);
        HANDLERS.put(Fluids.FLOWING_LAVA, LAVA);
    }

    private CbFapiFluids() {
    }

    /** FluidRenderHandlerRegistry.INSTANCE */
    public static FluidRenderHandlerRegistry registry() {
        return REGISTRY;
    }

    private static final class Registry implements FluidRenderHandlerRegistry {
        @Override
        public FluidRenderHandler get(Fluid fluid) {
            FluidRenderHandler handler = HANDLERS.get(fluid);
            return handler != null ? handler : nativeView(fluid);
        }

        @Override
        public FluidRenderHandler getOverride(Fluid fluid) {
            FluidRenderHandler handler = MOD_HANDLERS.get(fluid);
            return handler != null ? handler : nativeView(fluid);
        }

        @Override
        public void register(Fluid fluid, FluidRenderHandler renderer) {
            HANDLERS.put(fluid, renderer);
            MOD_HANDLERS.put(fluid, renderer);
        }

        @Override
        public void setBlockTransparency(Block block, boolean transparent) {
            FluidRenderingRegistry.setBlockTransparency(block, transparent);
        }

        @Override
        public boolean isBlockTransparent(Block block) {
            return FluidRenderingRegistry.isBlockTransparent(block);
        }
    }

    /**
     * A fluid a 26.1 mod registered with 26.1's FluidRenderingRegistry (1.21.11's registry is where such a mod
     * registered): seen through its FluidModel, 1.21.11's handler shape over the same sprites and tint. Null
     * for a fluid nobody registered, as 1.21.11 answered.
     */
    private static FluidRenderHandler nativeView(Fluid fluid) {
        return FluidRenderingRegistry.getOverride(fluid) != null ? new ModelView(fluid) : null;
    }

    private record ModelView(Fluid fluid) implements FluidRenderHandler {
        @Override
        public TextureAtlasSprite[] getFluidSprites(BlockAndLightGetter view, BlockPos pos, FluidState state) {
            return modelSprites(rawModel(fluid.defaultFluidState()));
        }

        @Override
        public int getFluidColor(BlockAndLightGetter view, BlockPos pos, FluidState state) {
            BlockTintSource tint = rawModel(state).tintSource();
            if (tint == null) {
                return -1;
            }
            BlockState block = state.createLegacyBlock();
            return view != null && pos != null ? tint.colorInWorld(block, CbWallTintClient.toTint(view), pos) : tint.color(block);
        }
    }

    // ---- 1.21.11 FluidRenderingImpl ----

    /** 1.21.11 FluidRendering.render */
    public static void render(FluidRenderHandler handler, BlockAndLightGetter world, BlockPos pos, VertexConsumer vertexConsumer,
                              BlockState blockState, FluidState fluidState, FluidRendering.DefaultRenderer defaultRenderer) {
        CURRENT_DEFAULT_RENDERER.set(defaultRenderer);
        try {
            handler.renderFluid(pos, world, vertexConsumer, blockState, fluidState);
        } finally {
            CURRENT_DEFAULT_RENDERER.remove();
        }
    }

    /** 1.21.11 FluidRenderHandler.renderFluid's default body */
    public static void renderDefault(FluidRenderHandler handler, BlockAndLightGetter world, BlockPos pos, VertexConsumer vertexConsumer,
                                     BlockState blockState, FluidState fluidState) {
        FluidRendering.DefaultRenderer renderer = CURRENT_DEFAULT_RENDERER.get();
        if (renderer != null) {
            renderer.render(handler, world, pos, vertexConsumer, blockState, fluidState);
        } else {
            renderVanillaDefault(handler, world, pos, vertexConsumer, blockState, fluidState);
        }
    }

    /**
     * 1.21.11 FluidRendering.DefaultRenderer.render's default body: the vanilla tesselation with the handler's
     * sprites and colour for this position, written to the consumer given.
     */
    public static void renderVanillaDefault(FluidRenderHandler handler, BlockAndLightGetter world, BlockPos pos,
                                            VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        Info info = CURRENT_INFO.get();
        Info saved = info.copy();
        info.setup(handler, world, pos, fluidState);
        try {
            renderer().tesselate(CbWallTintClient.toTint(world), pos, layer -> vertexConsumer, blockState, fluidState);
        } finally {
            info.restore(saved);
        }
    }

    private static FluidRenderer renderer() {
        FluidRenderer renderer = vanillaRenderer;
        return renderer != null ? renderer : CbWallBlockRender.fluidRenderer();
    }

    /** 1.21.11 FluidRenderHandlerInfo: the handler whose default rendering runs on this thread, and its sprites */
    private static final class Info {
        FluidRenderHandler handler;
        BlockAndLightGetter view;
        BlockPos pos;
        FluidState state;
        TextureAtlasSprite[] sprites;

        void setup(FluidRenderHandler handler, BlockAndLightGetter view, BlockPos pos, FluidState state) {
            this.handler = handler;
            this.view = view;
            this.pos = pos;
            this.state = state;
            this.sprites = handler.getFluidSprites(view, pos, state);
        }

        Info copy() {
            Info c = new Info();
            c.restore(this);
            return c;
        }

        void restore(Info o) {
            handler = o.handler;
            view = o.view;
            pos = o.pos;
            state = o.state;
            sprites = o.sprites;
        }
    }

    // ---- 26.1 hooks ----

    /**
     * 1.21.11 LiquidBlockRendererMixin.onHeadRender on 26.1's FluidRenderer.tesselate: a fluid a 1.21.11 handler
     * was registered for renders through that handler, into the consumer of the fluid's layer. True when it did
     * (the tesselation is then skipped). Not while a handler's default rendering is running: that is the
     * tesselation it asked for.
     */
    public static boolean tesselate(BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output output, BlockState blockState,
                                    FluidState fluidState) {
        if (CURRENT_INFO.get().handler != null) {
            return false;
        }
        FluidRenderHandler handler = MOD_HANDLERS.get(fluidState.getType());
        if (handler == null) {
            return false;
        }
        handler.renderFluid(pos, level, output.getBuilder(getRenderLayer(fluidState.getType())), blockState, fluidState);
        return true;
    }

    /**
     * 26.1's FluidStateModelSet.get answer for 1.21.11: during a handler's default rendering, the model of that
     * handler's sprites and colour at that position; otherwise a fluid a 1.21.11 handler was registered for
     * answers that handler's model (sprites and colour asked without a world, as 1.21.11's FluidVariant
     * rendering asked), a fluid 1.21.11 mapped to a layer answers its model on that layer.
     */
    public static FluidModel model(FluidState state, FluidModel model) {
        if (RAW.get()) {
            return model;
        }
        Fluid fluid = state.getType();
        Info info = CURRENT_INFO.get();
        if (info.handler != null) {
            return handlerModel(fluid, info.handler, info.sprites, info.state, info.view);
        }
        FluidRenderHandler handler = MOD_HANDLERS.get(fluid);
        if (handler != null) {
            return handlerModel(fluid, handler, handler.getFluidSprites(null, null, state), state, null);
        }
        ChunkSectionLayer layer = LAYERS.get(fluid);
        if (layer != null && layer != model.layer()) {
            return new FluidModel(layer, model.stillMaterial(), model.flowingMaterial(), model.overlayMaterial(), model.tintSource());
        }
        return model;
    }

    /** what 1.21.11's tesselation drew for a handler: its still, flowing and (third, when given) overlay sprite and its colour */
    private static FluidModel handlerModel(Fluid fluid, FluidRenderHandler handler, TextureAtlasSprite[] sprites, FluidState state,
                                           BlockAndLightGetter view) {
        Material.Baked overlay = sprites.length > 2 ? new Material.Baked(sprites[2], false) : null;
        return new FluidModel(getRenderLayer(fluid), new Material.Baked(sprites[0], false), new Material.Baked(sprites[1], false),
                overlay, new HandlerTint(handler, state, view));
    }

    /**
     * A handler's colour where 26.1 asks the model's tint source. 1.21.11's tesselation took only the colour's
     * red, green and blue (alpha 1); 26.1 writes the tint as ARGB, so the answer is made opaque. The world asked
     * with is the one the handler's rendering was given, when it was given one.
     */
    private record HandlerTint(FluidRenderHandler handler, FluidState state, BlockAndLightGetter view) implements BlockTintSource {
        @Override
        public int color(BlockState blockState) {
            return handler.getFluidColor(null, null, state) | 0xFF000000;
        }

        @Override
        public int colorInWorld(BlockState blockState, BlockAndTintGetter level, BlockPos pos) {
            return handler.getFluidColor(view != null ? view : level, pos, state) | 0xFF000000;
        }
    }

    /** 26.1's own model for a fluid state (what a 1.21.11 handler of a 26.1-registered fluid reads) */
    private static FluidModel rawModel(FluidState state) {
        RAW.set(Boolean.TRUE);
        try {
            return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state);
        } finally {
            RAW.set(Boolean.FALSE);
        }
    }

    private static TextureAtlasSprite[] modelSprites(FluidModel model) {
        if (model.overlayMaterial() == null) {
            return new TextureAtlasSprite[]{model.stillMaterial().sprite(), model.flowingMaterial().sprite()};
        }
        return new TextureAtlasSprite[]{model.stillMaterial().sprite(), model.flowingMaterial().sprite(), model.overlayMaterial().sprite()};
    }

    /**
     * 1.21.11 LiquidBlockRendererMixin.onResourceReloadReturn (LiquidBlockRenderer.setupSprites, run at each model
     * reload) on 26.1's ModelManager.apply: the renderer over the new models, water and lava take their reloaded
     * sprites, then every handler reloads its textures from the block atlas.
     */
    public static void onModelsApplied(FluidStateModelSet models) {
        vanillaRenderer = new FluidRenderer(models);
        RAW.set(Boolean.TRUE);
        try {
            WATER.sprites = modelSprites(models.get(Fluids.WATER.defaultFluidState()));
            LAVA.sprites = modelSprites(models.get(Fluids.LAVA.defaultFluidState()));
        } finally {
            RAW.set(Boolean.FALSE);
        }
        TextureAtlas blocks = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        for (FluidRenderHandler handler : HANDLERS.values()) {
            handler.reloadTextures(blocks);
        }
    }

    /** 1.21.11 FluidRenderHandlerRegistryImpl.WaterRenderHandler */
    private static final class WaterHandler implements FluidRenderHandler {
        /** 1.21.11's DEFAULT_WATER_COLOR */
        private static final int DEFAULT_WATER_COLOR = 4159204;
        TextureAtlasSprite[] sprites = new TextureAtlasSprite[3];

        @Override
        public TextureAtlasSprite[] getFluidSprites(BlockAndLightGetter view, BlockPos pos, FluidState state) {
            return sprites;
        }

        @Override
        public int getFluidColor(BlockAndLightGetter view, BlockPos pos, FluidState state) {
            return view != null && pos != null ? CbWallTintClient.getAverageWaterColor(view, pos) : DEFAULT_WATER_COLOR;
        }
    }

    /** 1.21.11 FluidRenderHandlerRegistryImpl.LavaRenderHandler */
    private static final class LavaHandler implements FluidRenderHandler {
        TextureAtlasSprite[] sprites;

        @Override
        public TextureAtlasSprite[] getFluidSprites(BlockAndLightGetter view, BlockPos pos, FluidState state) {
            return sprites;
        }
    }

    // ---- layers: 1.21.11 BlockRenderLayerMap.putFluid(s), ItemBlockRenderTypes.getRenderLayer(FluidState) ----

    /** 1.21.11 BlockRenderLayerMap.putFluid */
    public static void putFluid(Fluid fluid, ChunkSectionLayer layer) {
        Objects.requireNonNull(fluid, "fluid");
        Objects.requireNonNull(layer, "layer");
        LAYERS.put(fluid, layer);
    }

    /** 1.21.11 BlockRenderLayerMap.putFluids */
    public static void putFluids(ChunkSectionLayer layer, Fluid... fluids) {
        for (Fluid fluid : fluids) {
            putFluid(fluid, layer);
        }
    }

    /** the layer 1.21.11's ItemBlockRenderTypes gave a fluid: mapped, water translucent, otherwise solid */
    private static ChunkSectionLayer getRenderLayer(Fluid fluid) {
        ChunkSectionLayer layer = LAYERS.get(fluid);
        if (layer != null) {
            return layer;
        }
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER ? ChunkSectionLayer.TRANSLUCENT : ChunkSectionLayer.SOLID;
    }

    // ---- transfer: 1.21.11 FluidVariantRenderHandler / FluidVariantRendering members 26.1 dropped or reshaped ----

    private static final MethodType SPRITES = MethodType.methodType(TextureAtlasSprite[].class, FluidVariantRenderHandler.class, FluidVariant.class);
    private static final ClassValue<MethodHandle> SPRITES_OVERRIDE = new ClassValue<>() {
        @Override
        protected MethodHandle computeValue(Class<?> type) {
            return CbFapiOverrides.find(type, "getSprites", SPRITES);
        }
    };

    /** a 1.21.11 call of FluidVariantRenderHandler.getSprites: the receiver's own override, else 1.21.11's default */
    public static TextureAtlasSprite[] variantSprites(FluidVariantRenderHandler handler, FluidVariant variant) {
        MethodHandle override = SPRITES_OVERRIDE.get(handler.getClass());
        if (override == null) {
            return variantSpritesBase(handler, variant);
        }
        try {
            return (TextureAtlasSprite[]) override.invokeExact(handler, variant);
        } catch (Throwable t) {
            throw CbFapiOverrides.rethrow(t);
        }
    }

    /** 1.21.11 FluidVariantRenderHandler.getSprites' default body (a super call reaches it) */
    public static TextureAtlasSprite[] variantSpritesBase(FluidVariantRenderHandler handler, FluidVariant variant) {
        FluidRenderHandler fluidHandler = REGISTRY.get(variant.getFluid());
        return fluidHandler != null ? fluidHandler.getFluidSprites(null, null, variant.getFluid().defaultFluidState()) : null;
    }

    /** 1.21.11 FluidVariantRenderHandler.getColor's default body (a super call reaches it) */
    public static int variantColorBase(FluidVariantRenderHandler handler, FluidVariant variant, BlockAndLightGetter view, BlockPos pos) {
        FluidRenderHandler fluidHandler = REGISTRY.get(variant.getFluid());
        return fluidHandler != null ? fluidHandler.getFluidColor(view, pos, variant.getFluid().defaultFluidState()) | 0xFF000000 : -1;
    }

    /** a 26.1 client getter where a 1.21.11 override of FluidVariantRenderHandler.getColor takes 1.21.11's getter */
    public static BlockAndLightGetter lightGetter(BlockAndTintGetter level) {
        return level;
    }

    /** 1.21.11 FluidVariantRendering.getSprites */
    public static TextureAtlasSprite[] getSprites(FluidVariant variant) {
        return variantSprites(FluidVariantRendering.getHandlerOrDefault(variant.getFluid()), variant);
    }

    /** 1.21.11 FluidVariantRendering.getSprite */
    public static TextureAtlasSprite getSprite(FluidVariant variant) {
        TextureAtlasSprite[] sprites = getSprites(variant);
        return sprites != null ? Objects.requireNonNull(sprites[0]) : null;
    }

    /** 1.21.11 FluidVariantRendering.getColor(variant, view, pos): the handler's getColor on 26.1's client getter */
    public static int getColor(FluidVariant variant, BlockAndLightGetter view, BlockPos pos) {
        return FluidVariantRendering.getHandlerOrDefault(variant.getFluid()).getColor(variant, CbWallTintClient.toTint(view), pos);
    }
}
