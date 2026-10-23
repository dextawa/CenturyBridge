package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3 rendering members whose shape 1.21.4 changed, body for body on 1.21.4's.
 * <ul>
 * <li>Immediate drawing (a Tesselator, a LightTexture, a pose of one's own) became drawing into the frame's buffer source: a
 *     1.21.3 call draws into the game's buffer source and ends the batch, which is what 1.21.4's own callers do right after,
 *     so it is drawn when 1.21.3 drew it; a 1.21.3 override is handed what 1.21.3's caller handed (the Tesselator instance, the
 *     game's LightTexture, an identity PoseStack -- 1.21.3's sky pass made a fresh one).</li>
 * <li>Values 1.21.4 takes in addition are the game's (the item model resolver, the special block renderer) or, where 1.21.4
 *     made a 1.21.3 constant a parameter, that constant as 1.21.4 itself passes it (cherry leaves, an identity layer scale,
 *     emissive layers hidden on invisible entities as 1.21.3 hid them).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTail {
    private CbRenderTail() {
    }

    private static net.minecraft.class_310 mc() {
        return net.minecraft.class_310.method_1551();
    }

    private static net.minecraft.class_4597.class_4598 buffers() {
        return mc().method_22940().method_23000();
    }

    /** the Tesselator a 1.21.3 override drew with, where 1.21.4 hands a buffer source */
    public static net.minecraft.class_289 tesselator(net.minecraft.class_4597.class_4598 buffers) {
        return net.minecraft.class_289.method_1348();
    }

    /** the game's LightTexture, where 1.21.4 hands a buffer source in its place */
    public static net.minecraft.class_765 lightTexture(net.minecraft.class_4597 buffers) {
        return mc().field_1773.method_22974();
    }

    // ---- SkyRenderer ----

    /** was renderSkyDisc(pose, tesselator, ..) style calls: into the game's buffer source, drawn at once */
    public static void method_62306(net.minecraft.class_9975 sky, net.minecraft.class_4587 pose, net.minecraft.class_289 tesselator, float a, int b) {
        net.minecraft.class_4597.class_4598 buffers = buffers();
        sky.method_62306(pose, buffers, a, b);
        buffers.method_22993();
    }

    public static void method_62307(net.minecraft.class_9975 sky, net.minecraft.class_4587 pose, net.minecraft.class_289 tesselator, float a, int b,
        float c, float d, net.minecraft.class_9958 e) {
        net.minecraft.class_4597.class_4598 buffers = buffers();
        sky.method_62307(pose, buffers, a, b, c, d, e);
        buffers.method_22993();
    }

    /** was renderEndSky(pose): 1.21.4 draws it under the model-view stack, which takes the pose for the call */
    public static void method_62312(net.minecraft.class_9975 sky, net.minecraft.class_4587 pose) {
        org.joml.Matrix4fStack view = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        view.pushMatrix();
        view.mul(pose.method_23760().method_23761());
        try {
            sky.method_62312();
        } finally {
            view.popMatrix();
        }
    }

    /** a 1.21.3 SkyRenderer overriding renderEndSky(pose) */
    public interface CbLegacyEndSky {
        void method_62312(net.minecraft.class_4587 pose);
    }

    /** 1.21.4's renderEndSky() on a 1.21.3 override: the identity pose 1.21.3's sky pass gave it */
    public static void endSky(CbLegacyEndSky sky) {
        sky.method_62312(new net.minecraft.class_4587());
    }

    // ---- WeatherEffectRenderer / ParticleEngine / ScreenEffectRenderer ----

    /** was render(level, lightTexture, ticks, partialTick, cameraPosition) */
    public static void method_62316(net.minecraft.class_9976 weather, net.minecraft.class_1937 level, net.minecraft.class_765 lightTexture, int ticks,
        float partialTick, net.minecraft.class_243 camera) {
        net.minecraft.class_4597.class_4598 buffers = buffers();
        weather.method_62316(level, buffers, ticks, partialTick, camera);
        buffers.method_22993();
    }

    /** was ParticleEngine.render(lightTexture, camera, partialTick) */
    public static void method_3049(net.minecraft.class_702 engine, net.minecraft.class_765 lightTexture, net.minecraft.class_4184 camera, float partialTick) {
        net.minecraft.class_4597.class_4598 buffers = buffers();
        engine.method_3049(camera, partialTick, buffers);
        buffers.method_22993();
    }

    /** was ScreenEffectRenderer.renderScreenEffect(minecraft, pose) */
    public static void method_23067(net.minecraft.class_310 minecraft, net.minecraft.class_4587 pose) {
        net.minecraft.class_4597.class_4598 buffers = buffers();
        net.minecraft.class_4603.method_23067(minecraft, pose, buffers);
        buffers.method_22993();
    }

    /** a 1.21.3 LevelRenderer subclass's renderLevel (with the LightTexture 1.21.4 no longer passes) */
    public interface CbLegacyRenderLevel {
        void method_22710(net.minecraft.class_9922 allocator, net.minecraft.class_9779 delta, boolean outline, net.minecraft.class_4184 camera,
            net.minecraft.class_757 gameRenderer, net.minecraft.class_765 lightTexture, org.joml.Matrix4f frustum, org.joml.Matrix4f projection);
    }

    /** 1.21.4's renderLevel on a 1.21.3 override: the game's LightTexture, as 1.21.3's game renderer passed its own */
    public static void renderLevel(CbLegacyRenderLevel renderer, net.minecraft.class_9922 allocator, net.minecraft.class_9779 delta, boolean outline,
        net.minecraft.class_4184 camera, net.minecraft.class_757 gameRenderer, org.joml.Matrix4f frustum, org.joml.Matrix4f projection) {
        renderer.method_22710(allocator, delta, outline, camera, gameRenderer, gameRenderer.method_22974(), frustum, projection);
    }

    // ---- RenderSystem / NativeImage ----

    /** was RenderSystem.overlayBlendFunc() */
    public static void overlayBlendFunc() {
        com.mojang.blaze3d.systems.RenderSystem.blendFuncSeparate(com.mojang.blaze3d.platform.GlStateManager.class_4535.SRC_ALPHA,
            com.mojang.blaze3d.platform.GlStateManager.class_4534.ONE, com.mojang.blaze3d.platform.GlStateManager.class_4535.ONE,
            com.mojang.blaze3d.platform.GlStateManager.class_4534.ZERO);
    }

    /** was NativeImage.upload(level, x, y, ox, oy, w, h, blur, clamp, mipmap, autoClose): 1.21.3 set the bound texture's filter and wrap */
    public static void method_22619(net.minecraft.class_1011 image, int level, int x, int y, int ox, int oy, int w, int h, boolean blur, boolean clamp,
        boolean mipmap, boolean autoClose) {
        Runnable upload = () -> {
            setFilter(blur, mipmap);
            image.method_22619(level, x, y, ox, oy, w, h, autoClose);
            if (clamp) {
                com.mojang.blaze3d.platform.GlStateManager._texParameter(3553, 10242, 33071);
                com.mojang.blaze3d.platform.GlStateManager._texParameter(3553, 10243, 33071);
            }
        };
        if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThreadOrInit()) {
            com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(upload::run);
        } else {
            upload.run();
        }
    }

    /** was NativeImage.upload(level, x, y, ox, oy, w, h, mipmap, autoClose): no blur, no clamp */
    public static void method_4312(net.minecraft.class_1011 image, int level, int x, int y, int ox, int oy, int w, int h, boolean mipmap, boolean autoClose) {
        method_22619(image, level, x, y, ox, oy, w, h, false, false, mipmap, autoClose);
    }

    /** 1.21.3 NativeImage.setFilter(blur, mipmap) */
    private static void setFilter(boolean blur, boolean mipmap) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThreadOrInit();
        if (blur) {
            com.mojang.blaze3d.platform.GlStateManager._texParameter(3553, 10241, mipmap ? 9987 : 9729);
            com.mojang.blaze3d.platform.GlStateManager._texParameter(3553, 10240, 9729);
        } else {
            com.mojang.blaze3d.platform.GlStateManager._texParameter(3553, 10241, mipmap ? 9986 : 9728);
            com.mojang.blaze3d.platform.GlStateManager._texParameter(3553, 10240, 9728);
        }
    }

    // ---- input, music ----

    /** a 1.21.3 ClientInput subclass's tick(isSneaking, sneakingSpeedBonus) */
    public interface CbLegacyInputTick {
        void method_3129(boolean sneaking, float sneakSpeed);
    }

    /** KeyboardInput's options and impulse function (CbTailKeyboardInputAccess) */
    public interface CbKeyboardInput {
        net.minecraft.class_315 cb$options();
    }

    /** was ClientInput.tick(isSneaking, sneakingSpeedBonus): a 1.21.3 input's own, else 1.21.4's tick and KeyboardInput's 1.21.3 slowdown */
    public static void method_3129(net.minecraft.class_744 input, boolean sneaking, float sneakSpeed) {
        if (input instanceof CbLegacyInputTick legacy) {
            legacy.method_3129(sneaking, sneakSpeed);
            return;
        }
        input.method_3129();
        if (input instanceof net.minecraft.class_743 && sneaking) {
            input.field_3907 *= sneakSpeed;
            input.field_3905 *= sneakSpeed;
        }
    }

    /**
     * 1.21.4's tick() on a 1.21.3 input: its own tick, without slowdown -- 1.21.4's player applies the sneaking slowdown after
     * the input's tick, where 1.21.3's KeyboardInput applied it inside
     */
    public static void tick(CbLegacyInputTick input) {
        input.method_3129(false, 1.0f);
    }

    /** a subclass's super.tick(isSneaking, sneakingSpeedBonus): ClientInput's 1.21.3 body (none), KeyboardInput's (keys, impulses, slowdown) */
    public static void superTick(net.minecraft.class_744 input, boolean sneaking, float sneakSpeed) {
        if (input instanceof net.minecraft.class_743 keyboard) {
            net.minecraft.class_315 options = ((CbKeyboardInput) keyboard).cb$options();
            input.field_54155 = new net.minecraft.class_10185(options.field_1894.method_1434(), options.field_1881.method_1434(),
                options.field_1913.method_1434(), options.field_1849.method_1434(), options.field_1903.method_1434(), options.field_1832.method_1434(),
                options.field_1867.method_1434());
            input.field_3905 = impulse(input.field_54155.comp_3159(), input.field_54155.comp_3160());
            input.field_3907 = impulse(input.field_54155.comp_3161(), input.field_54155.comp_3162());
            if (sneaking) {
                input.field_3907 *= sneakSpeed;
                input.field_3905 *= sneakSpeed;
            }
        }
    }

    /** 1.21.3 KeyboardInput.calculateImpulse(positive, negative) */
    private static float impulse(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    // ---- constructors ----

    /** the game's item model resolver */
    public static net.minecraft.class_10442 cbResolver() {
        return CbItemRender.cbGameResolver();
    }

    /** cherry leaves, as 1.21.4's CherryProvider passes them (1.21.3's FallingLeavesParticle was the cherry particle) */
    public static float cbLeafGravity() {
        return 0.25f;
    }

    public static float cbLeafWind() {
        return 2.0f;
    }

    public static boolean cbLeafSwirl() {
        return false;
    }

    public static boolean cbLeafFlowAway() {
        return true;
    }

    public static float cbLeafSize() {
        return 1.0f;
    }

    public static float cbLeafYSpeed() {
        return 0.0f;
    }

    /** BreakingItemParticle: the state 1.21.4 takes, empty -- the sprite is set after as 1.21.3 chose it */
    public static net.minecraft.class_10444 cbEmptyState(net.minecraft.class_1799 stack) {
        return new net.minecraft.class_10444();
    }

    /** a particle's sprite (TextureSheetParticle.setSprite) */
    public interface CbSpriteParticle {
        void cb$setSprite(net.minecraft.class_1058 sprite);
    }

    /** after a 1.21.3 BreakingItemParticle's super(..): 1.21.3's sprite, the particle icon of the stack's model */
    public static void cbBreakingSprite(net.minecraft.class_647 particle, net.minecraft.class_638 level, net.minecraft.class_1799 stack) {
        ((CbSpriteParticle) particle).cb$setSprite(CbItemRender.getModel(mc().method_1480(), stack, level, null, 0).method_4711());
    }

    /** BlockRenderDispatcher's special block renderer: the game's (1.21.3 took the BlockEntityWithoutLevelRenderer, which draws the same) */
    public interface CbBlockRenderDispatcher {
        Supplier<net.minecraft.class_10418> cb$specialRenderer();
    }

    public static Supplier<net.minecraft.class_10418> cbSpecialRenderer(top.dext.centurybridge.step.v1_21_4.facade.BlockEntityWithoutLevelRenderer bewlr) {
        return () -> ((CbBlockRenderDispatcher) mc().method_1541()).cb$specialRenderer().get();
    }

    /** a renderer constant 1.21.4 took as a parameter: an identity layer scale */
    public static float cbUnitScale() {
        return 1.0f;
    }

    /** LivingEntityEmissiveLayer: 1.21.3 drew nothing on an invisible entity */
    public static boolean cbNotAlwaysVisible() {
        return false;
    }

    /** AbstractHorseRenderer's 1.21.3 scale (CbTailHorseScaleMixin) */
    public interface CbHorseScale {
        void cb$setScale(float scale);
    }

    public static void cbHorseScale(net.minecraft.class_875<?, ?, ?> renderer, float scale) {
        ((CbHorseScale) renderer).cb$setScale(scale);
    }

    // ---- entity renderers ----

    /** was SlimeRenderer.render(state, ..) (method_4117): LivingEntityRenderer's render (1.21.3's set the shadow, 1.21.4's renderer asks it) */
    public static void method_4117(net.minecraft.class_945 renderer, net.minecraft.class_10067 state, net.minecraft.class_4587 pose,
        net.minecraft.class_4597 buffers, int light) {
        renderer.method_4054(state, pose, buffers, light);
    }

    /** was MagmaCubeRenderer.render(state, ..) (method_48302) */
    public static void method_48302(net.minecraft.class_917 renderer, net.minecraft.class_10067 state, net.minecraft.class_4587 pose,
        net.minecraft.class_4597 buffers, int light) {
        renderer.method_4054(state, pose, buffers, light);
    }

    /** was LivingEntityRenderer.itemRenderer (field_53193): the context's, which is the game's */
    public static net.minecraft.class_918 field_53193(net.minecraft.class_922<?, ?, ?> renderer) {
        return mc().method_1480();
    }

    /** DonkeyRenderer: 1.21.4's vanilla donkey / mule layers carry the scale 1.21.3 applied in the renderer */
    public static void cbDonkeyScale(net.minecraft.class_9991<?> renderer, float scale, net.minecraft.class_5601 layer) {
        if (layer.equals(net.minecraft.class_5602.field_27550) || layer.equals(net.minecraft.class_5602.field_52970)
                || layer.equals(net.minecraft.class_5602.field_27616) || layer.equals(net.minecraft.class_5602.field_52985)) {
            return;
        }
        cbHorseScale(renderer, scale);
    }

    // ---- CubeMap, ModelLayers ----

    /** was CubeMap.preload(textureManager, executor): every face preloaded (TextureManager.preload, CbTextureManagerMixin) */
    public static CompletableFuture<Void> method_18143(net.minecraft.class_751 cubeMap, net.minecraft.class_1060 textureManager, Executor executor) {
        java.util.List<net.minecraft.class_2960> faces = ((CbCubeMap) cubeMap).cb$faces();
        CompletableFuture<?>[] futures = new CompletableFuture<?>[faces.size()];
        for (int i = 0; i < futures.length; i++) {
            futures[i] = ((CbTexturePreload) textureManager).method_18168(faces.get(i), executor);
        }
        return CompletableFuture.allOf(futures);
    }

    /** CubeMap's face images (CbTailCubeMapAccess) */
    public interface CbCubeMap {
        java.util.List<net.minecraft.class_2960> cb$faces();
    }

    /** TextureManager's 1.21.3 preload (CbTextureManagerMixin) */
    public interface CbTexturePreload {
        CompletableFuture<Void> method_18168(net.minecraft.class_2960 id, Executor executor);
    }

    /** was ModelLayers.createHangingSignModelName(woodType) (method_45719): 1.21.3's one layer per wood */
    public static net.minecraft.class_5601 method_45719(net.minecraft.class_4719 wood) {
        return new net.minecraft.class_5601(net.minecraft.class_2960.method_60656("hanging_sign/" + wood.comp_1299()), "main");
    }
}
