package top.dext.centurybridge.rt.v26_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
/**
 * Client-only split of {@link Statics}: homes for dead STATIC/instance/ctor-redirect
 * targets whose signatures (parameter or return types) reference client-only classes
 * (com.mojang.blaze3d, net.minecraft.client.*, com.mojang.realmsclient). Kept out of
 * {@link Statics} so a dedicated server, which has no blaze3d/client classes, can link
 * {@link Statics} without eager-resolving those types. Call sites for these live only in
 * client-side mixins/classes, which a dedicated server never loads.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class ClientStatics {

    /** was net/minecraft/client/sounds/SoundEventListener/onPlaySound(Lnet/minecraft/client/resources/sounds/SoundInstance;Lnet/minecraft/client/sounds/WeighedSoundEvents;)V */
    public void onPlaySound(net.minecraft.client.resources.sounds.SoundInstance a0, net.minecraft.client.sounds.WeighedSoundEvents a1) {
        ((net.minecraft.client.sounds.SoundEventListener) (Object) this).onPlaySound(a0, a1, 1.0f);
    }

    /** was net/minecraft/client/Minecraft/method_1589()Z */
    public static boolean method_1589(net.minecraft.client.Minecraft self) {
        return true;
    }

    /** was net/minecraft/client/Minecraft/method_44649()Z */
    public static boolean method_44649(net.minecraft.client.Minecraft self) {
        return self.extraTelemetryAvailable();
    }

    /** was net/minecraft/client/gui/components/AbstractWidget/method_47937()Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner; */
    public static net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner method_47937(net.minecraft.client.gui.components.AbstractWidget self) {
        try {
            for (java.lang.reflect.Field field : net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredFields()) {
                if (field.getType() == net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner.class) {
                    field.setAccessible(true);
                    return (net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner) field.get(self);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.field_19576Lnet/minecraft/com.mojang.realmsclient.client.RealmsClient$class_4343; */
    public static com.mojang.realmsclient.client.RealmsClient.Environment field_19576() {
        return com.mojang.realmsclient.client.RealmsClient.ENVIRONMENT;
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21021()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21021(com.mojang.realmsclient.client.RealmsClient self) {
        return java.lang.Boolean.FALSE;
    }

    /** was net/minecraft/com.mojang.realmsclient.client.Request.method_35685(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/com.mojang.realmsclient.client.Request; */
    public static com.mojang.realmsclient.client.Request method_35685(com.mojang.realmsclient.client.Request self, java.lang.String a0, java.lang.String a1) {
        self.cookie(a0, a1);
        return self;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.method_25228(I)I */
    public static int method_25228(com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen self, int a0) {
        return 40 + a0 * 13;
    }

    /** was net/minecraft/client/multiplayer/ClientAdvancements/method_2863()Lnet/minecraft/advancements/AdvancementTree; */
    public static net.minecraft.advancements.AdvancementTree method_2863(net.minecraft.client.multiplayer.ClientAdvancements self) {
        return self.getTree();
    }

    /** was net/minecraft/client/multiplayer/PlayerInfo/method_2967()Z */
    public static boolean method_2967(net.minecraft.client.multiplayer.PlayerInfo self) {
        return self.hasVerifiableChat();
    }

    /** was net/minecraft/client/multiplayer/PlayerInfo/method_2969()V */
    public static void method_2969(net.minecraft.client.multiplayer.PlayerInfo self) {
        self.getSkin();
    }

    /** was net/minecraft/client/multiplayer/PlayerInfo/method_2977()Ljava/lang/String; */
    public static java.lang.String method_2977(net.minecraft.client.multiplayer.PlayerInfo self) {
        return self.getProfile().name();
    }

    /** was net/minecraft/client/multiplayer/PlayerInfo/method_35757()Z */
    public static boolean method_35757(net.minecraft.client.multiplayer.PlayerInfo self) {
        return self.hasVerifiableChat();
    }

    /** was net/minecraft/client/gui/components/MultiLineEditBox/method_44406()V */
    public static void method_44406(net.minecraft.client.gui.components.MultiLineEditBox self) {
        return;
    }

    /** was net/minecraft/client/Minecraft/method_27466(Z)V */
    public static void method_27466(net.minecraft.client.Minecraft self, boolean a0) {
        try {
            java.lang.reflect.Field field = net.minecraft.client.Minecraft.class.getDeclaredField("field_24209");
            field.setAccessible(true);
            field.setBoolean(self, a0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** was net/minecraft/client/MouseHandler/turnPlayer()V */
    public static void method_1606(net.minecraft.client.MouseHandler self) {
        self.handleAccumulatedMovement();
    }

    /** was net/minecraft/client/Options$2.field_28779Lnet/minecraft/client/Options; */
    public static net.minecraft.client.Options field_28779() {
        return net.minecraft.client.Minecraft.getInstance().options;
    }

    /** was net/minecraft/com.mojang.blaze3d.font.GlyphBitmap.method_41712()F */
    public static float method_41712(com.mojang.blaze3d.font.GlyphBitmap self) {
        return (float)self.getPixelWidth() / (float)self.getPixelHeight();
    }

    /** was net/minecraft/com.mojang.blaze3d.font.GlyphBitmap.method_41713()F */
    public static float method_41713(com.mojang.blaze3d.font.GlyphBitmap self) {
        return 1.0f;
    }

    /** was net/minecraft/client/multiplayer/MultiPlayerGameMode/method_2904()F */
    public static float method_2904(net.minecraft.client.multiplayer.MultiPlayerGameMode self) {
        return (float) self.getDestroyStage();
        // CB-CONFIDENCE: medium -- getDestroyStage is the likely int-returning replacement for the old float getBlockReachDistance; the int-to-float cast loses the 0.5 fractional reach in survival mode
    }

    /** was net/minecraft/client/gui/screens/worldselection/WorldOpenFlows/method_54618(Ljava/lang/String;Ljava/lang/Runnable;)V */
    public static void method_54618(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows self, java.lang.String a0, java.lang.Runnable a1) {
        self.openWorld(a0, a1);
    }

    /** was net/minecraft/client/renderer/debug/ChunkDebugRenderer$ChunkData.field_20999Lnet/minecraft/client/renderer/debug/ChunkDebugRenderer; */
    public static net.minecraft.client.renderer.debug.ChunkDebugRenderer field_20999() {
        return null;
    }

    /** was net/minecraft/client/renderer/rendertype/RenderType/method_24296()Ljava/util/Optional; */
    public static java.util.Optional method_24296(net.minecraft.client.renderer.rendertype.RenderType self) {
        return self.outline();
    }

    /** was net/minecraft/client/Minecraft/method_1488()F */
    public static float method_1488(net.minecraft.client.Minecraft self) {
        return (float) self.getGpuUtilization();
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_22901(IIII)V */
    public static void method_22901(com.mojang.blaze3d.vertex.VertexConsumer self, int a0, int a1, int a2, int a3) {
        self.setColor(a0, a1, a2, a3);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.addVertex(DDD)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public com.mojang.blaze3d.vertex.VertexConsumer addVertex(double a0, double a1, double a2) {
        return ((com.mojang.blaze3d.vertex.VertexConsumer) (Object) this).addVertex((float) a0, (float) a1, (float) a2);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_22916(I)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22916(com.mojang.blaze3d.vertex.VertexConsumer self, int a0) {
        // 1.20.1 uv2(int) = packed lightmap coordinates (the forged body sent it to setColor)
        return self.setLight(a0);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_22917(II)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22917(com.mojang.blaze3d.vertex.VertexConsumer self, int a0, int a1) {
        return self.setUv1(a0, a1);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_23763(Lnet/minecraft/com.mojang.blaze3d.vertex.PoseStack$class_4665;FFF)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_23763(com.mojang.blaze3d.vertex.VertexConsumer self, com.mojang.blaze3d.vertex.PoseStack.Pose a0, float a1, float a2, float a3) {
        return self.addVertex(a0, a1, a2, a3);
    }

    /** was net/minecraft/client/gui/screens/options/OnlineOptionsScreen/method_47621(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Options;)Lnet/minecraft/client/gui/screens/options/OnlineOptionsScreen; */
    public static net.minecraft.client.gui.screens.options.OnlineOptionsScreen method_47621(net.minecraft.client.gui.screens.options.OnlineOptionsScreen self, net.minecraft.client.Minecraft a0, net.minecraft.client.gui.screens.Screen a1, net.minecraft.client.Options a2) {
        return new net.minecraft.client.gui.screens.options.OnlineOptionsScreen(a1, a2);
        // CB-CONFIDENCE: medium -- the new constructor takes exactly the Screen and GameOptions args the old factory accepted; the MinecraftClient arg has no equivalent in the new API and is dropped, which is the most faithful bridge available.
    }

    /** was com/mojang/blaze3d/platform/NativeImage.method_24036()[B */
    public static byte[] method_24036(com.mojang.blaze3d.platform.NativeImage self) {
        java.nio.file.Path tempFile = null;
        try {
            tempFile = java.nio.file.Files.createTempFile("cb_nativeimage", ".png");
            self.writeToFile(tempFile);
            return java.nio.file.Files.readAllBytes(tempFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null) {
                try { java.nio.file.Files.deleteIfExists(tempFile); } catch (java.io.IOException ignored) {}
            }
        }
    }

    /** was com/mojang/blaze3d/platform/NativeImage.method_35621(IIB)V */
    public static void method_35621(com.mojang.blaze3d.platform.NativeImage self, int a0, int a1, byte a2) {
        self.setPixel(a0, a1, (int) a2);
    }

    /** was com/mojang/blaze3d/platform/NativeImage.method_35624(III)V */
    public static void method_35624(com.mojang.blaze3d.platform.NativeImage self, int a0, int a1, int a2) {
        self.setPixel(a0, a1, a2);
    }

    /** was com/mojang/blaze3d/platform/NativeImage.method_35625(II)B */
    public static byte method_35625(com.mojang.blaze3d.platform.NativeImage self, int a0, int a1) {
        return self.getLuminanceOrAlpha(a0, a1);
    }

    /** was com/mojang/blaze3d/platform/NativeImage.method_35626(II)B */
    public static byte method_35626(com.mojang.blaze3d.platform.NativeImage self, int a0, int a1) {
        return self.getLuminanceOrAlpha(a0, a1);
    }

    /** was com/mojang/blaze3d/platform/NativeImage.setPixelABGR(III)V */
    public static void method_4305(com.mojang.blaze3d.platform.NativeImage self, int a0, int a1, int a2) {
        self.setPixel(a0, a1, a2);
        // CB-CONFIDENCE: high -- setPixel(III)V is the 1.21.2 remap of NativeImage.setPixelColor(int,int,int), same (x,y,color) contract
    }

    /** was com/mojang/blaze3d/platform/NativeImage.getPixelABGR(II)I */
    public static int method_4315(com.mojang.blaze3d.platform.NativeImage self, int a0, int a1) {
        return self.getPixel(a0, a1);
    }

    /** was com/mojang/blaze3d/platform/Window.method_15999(I)V */
    public static void method_15999(com.mojang.blaze3d.platform.Window self, int a0) {
        self.setWidth(a0);
    }

    /** was net/minecraft/client/renderer/texture/TextureManager.method_22813(Lnet/minecraft/resources/Identifier;)V */
    public static void method_22813(net.minecraft.client.renderer.texture.TextureManager self, net.minecraft.resources.Identifier a0) {
        self.release(a0);
    }

    /** was net/minecraft/client/Options.method_1631(Lnet/minecraft/world/entity/player/PlayerModelPart;Z)V */
    public static void method_1631(net.minecraft.client.Options self, net.minecraft.world.entity.player.PlayerModelPart a0, boolean a1) {
        self.setModelPart(a0, a1);
    }

    /** was net/minecraft/client/model/Model.method_2828(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V */
    public static void method_2828(net.minecraft.client.model.Model self, com.mojang.blaze3d.vertex.PoseStack a0, com.mojang.blaze3d.vertex.VertexConsumer a1, int a2, int a3, int a4) {
        self.renderToBuffer(a0, a1, a2, a3, a4);
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeBookComponent.method_2600(Lnet/minecraft/world/inventory/Slot;)V */
    public static void method_2600(net.minecraft.client.gui.screens.recipebook.RecipeBookComponent self, net.minecraft.world.inventory.Slot a0) {
        self.slotClicked(a0);
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeBookPage.method_2630(Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;)V */
    public static void method_2630(net.minecraft.client.gui.screens.recipebook.RecipeBookPage self, net.minecraft.client.gui.screens.recipebook.RecipeBookComponent a0) {
        self.setInvisible();
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeCollection.method_2657()Z */
    public static boolean method_2657(net.minecraft.client.gui.screens.recipebook.RecipeCollection self) {
        return self.getRecipes().isEmpty();
    }

    /** was net/minecraft/client/model/geom/ModelLayerLocation.method_35743()Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_35743(net.minecraft.client.model.geom.ModelLayerLocation self) {
        return self.model();
    }

    /** was net/minecraft/client/model/geom/ModelLayerLocation.method_35744()Ljava/lang/String; */
    public static java.lang.String method_35744(net.minecraft.client.model.geom.ModelLayerLocation self) {
        return self.layer();
    }

    /** was com/mojang/blaze3d/opengl/GlProgram.field_29482Lcom/mojang/blaze3d/opengl/Uniform; */
    public static com.mojang.blaze3d.opengl.Uniform field_29482() {
        return null;
    }

    /** was net/minecraft/client/renderer/GameRenderer.field_29403Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram field_29403() {
        return null;
    }

    /** was net/minecraft/client/renderer/GameRenderer.field_4015Lnet/minecraft/client/Minecraft; */
    public static net.minecraft.client.Minecraft field_4015() {
        return net.minecraft.client.Minecraft.getInstance();
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34538(Lnet/minecraft/server/packs/resources/ResourceProvider;)V */
    public static void method_34538(net.minecraft.client.renderer.GameRenderer self, net.minecraft.server.packs.resources.ResourceProvider a0) {
        self.preloadUiShader(a0);
    }

    /** was net/minecraft/client/model/object/boat/RaftModel.method_45715()Lcom/google/common/collect/ImmutableList; */
    public static com.google.common.collect.ImmutableList method_45715(net.minecraft.client.model.object.boat.RaftModel self) {
        return com.google.common.collect.ImmutableList.of(self.createRaftModel(), self.createChestRaftModel());
    }

    /** was net/minecraft/client/renderer/entity/GhastRenderer.method_3973(Lnet/minecraft/world/entity/monster/Ghast;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_3973(net.minecraft.client.renderer.entity.GhastRenderer self, net.minecraft.world.entity.monster.Ghast a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        self.extractRenderState(a0, self.createRenderState(), a2);
    }

    /** was net/minecraft/client/renderer/entity/GiantMobRenderer.method_3980(Lnet/minecraft/world/entity/monster/Giant;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_3980(net.minecraft.client.renderer.entity.GiantMobRenderer self, net.minecraft.world.entity.monster.Giant a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        self.extractRenderState(a0, self.createRenderState(), a2);
    }

    /** was net/minecraft/client/renderer/entity/SquidRenderer.method_4127(Lnet/minecraft/world/entity/animal/squid/Squid;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_4127(net.minecraft.client.renderer.entity.SquidRenderer self, net.minecraft.world.entity.animal.squid.Squid a0) {
        return self.getTextureLocation(self.createRenderState());
    }

    /** overrideHooks wrap: the extractor vanilla hands us -> the DrawContext a 1.20.x override expects */
    public static top.dext.centurybridge.facade.v26_2.GuiGraphics cbWrapGui(net.minecraft.client.gui.GuiGraphicsExtractor ext) {
        return new top.dext.centurybridge.facade.v26_2.GuiGraphics(ext);
    }

    // ---- 26.x KeyMapping: the String-category constructors died with KeyMapping.Category ----
    private static final java.util.Map<String, net.minecraft.client.KeyMapping.Category> CB_KEY_CATEGORIES = new java.util.HashMap<>();

    private static net.minecraft.client.KeyMapping.Category cbKeyCategory(String legacy) {
        switch (legacy) {
            case "key.categories.movement": return net.minecraft.client.KeyMapping.Category.MOVEMENT;
            case "key.categories.misc": return net.minecraft.client.KeyMapping.Category.MISC;
            case "key.categories.multiplayer": return net.minecraft.client.KeyMapping.Category.MULTIPLAYER;
            case "key.categories.gameplay": return net.minecraft.client.KeyMapping.Category.GAMEPLAY;
            case "key.categories.inventory": return net.minecraft.client.KeyMapping.Category.INVENTORY;
            case "key.categories.creative": return net.minecraft.client.KeyMapping.Category.CREATIVE;
            case "key.categories.spectator": return net.minecraft.client.KeyMapping.Category.SPECTATOR;
            case "key.categories.ui": return net.minecraft.client.KeyMapping.Category.DEBUG;
            default:
                synchronized (CB_KEY_CATEGORIES) {
                    return CB_KEY_CATEGORIES.computeIfAbsent(legacy, k -> {
                        String path = k.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");
                        return net.minecraft.client.KeyMapping.Category.register(
                            net.minecraft.resources.Identifier.fromNamespaceAndPath("centurybridge", path));
                    });
                }
        }
    }

    /** ctorRedirects: 1.20.x KeyBinding(String, InputUtil.Type, int, String category) */
    public static net.minecraft.client.KeyMapping cbNewKeyMapping(String name, com.mojang.blaze3d.platform.InputConstants.Type type, int code, String category) {
        net.minecraft.client.KeyMapping km = new net.minecraft.client.KeyMapping(name, code, cbKeyCategory(category));
        if (type != com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM) {
            km.setKey(type.getOrCreate(code));
        }
        return km;
    }

    /** ctorRedirects: 1.20.x KeyBinding(String, int, String category) */
    public static net.minecraft.client.KeyMapping cbNewKeyMapping(String name, int code, String category) {
        return new net.minecraft.client.KeyMapping(name, code, cbKeyCategory(category));
    }

    /** 26.2 moved Minecraft.setScreen onto the Gui (instance redirect: receiver becomes arg 0) */
    public static void method_1507(net.minecraft.client.Minecraft self, net.minecraft.client.gui.screens.Screen screen) {
        self.gui.setScreen(screen);
    }

    /** ctorRedirects: 1.20.1 new ServerInfo(name, address, local) -- the boolean
     *  became ServerData.Type in 1.20.2 (local ? LAN : OTHER). */
    public static net.minecraft.client.multiplayer.ServerData cbNewServerInfo(String name, String address, boolean local) {
        return new net.minecraft.client.multiplayer.ServerData(name, address,
            local ? net.minecraft.client.multiplayer.ServerData.Type.LAN
                  : net.minecraft.client.multiplayer.ServerData.Type.OTHER);
    }

    /** superCtorAdapts: the 1.20.1 ServerInfo ctor boolean, as ServerData.Type */
    public static net.minecraft.client.multiplayer.ServerData.Type cbServerType(boolean local) {
        return local ? net.minecraft.client.multiplayer.ServerData.Type.LAN
                     : net.minecraft.client.multiplayer.ServerData.Type.OTHER;
    }
    /** PoseStack.mulPose(Quaternionf): 参数在 26.x 变成接口 Quaternionfc (Quaternionf 是其实现) */
    public static void method_22907(com.mojang.blaze3d.vertex.PoseStack self, org.joml.Quaternionf q) {
        self.mulPose(q);
    }

    /** was net/minecraft/class_4588.method_22912(DDD)Lnet/minecraft/class_4588; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22912(
            com.mojang.blaze3d.vertex.VertexConsumer self, double x, double y, double z) {
        return self.addVertex((float) x, (float) y, (float) z);
    }

    /** was net/minecraft/class_4588.method_22918(Lorg/joml/Matrix4f;FFF)Lnet/minecraft/class_4588; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22918(
            com.mojang.blaze3d.vertex.VertexConsumer self, org.joml.Matrix4f m, float x, float y, float z) {
        return self.addVertex(m, x, y, z);
    }

    /** was net/minecraft/class_1144.method_4873(Lnet/minecraft/class_1113;)V -- SoundManager.play now returns a PlayResult; the old call discarded a void return */
    public static void method_4873(net.minecraft.client.sounds.SoundManager self, net.minecraft.client.resources.sounds.SoundInstance s) {
        self.play(s);
    }

    // =====================================================================
    // 1.21 VertexConsumer surface: endVertex / default colour gone (implicit),
    // normal(Matrix3f, ..) transforms then sets. BufferBuilder implements the
    // interface, but the redirect emits the receiver's own type -> overloads.
    // =====================================================================
    /** was net/minecraft/class_4588.method_1344()V -- endVertex(): vertices complete implicitly since 1.21 */
    public static void method_1344(com.mojang.blaze3d.vertex.VertexConsumer self) {
    }
    /** was net/minecraft/class_287.method_1344()V */
    public static void method_1344(com.mojang.blaze3d.vertex.BufferBuilder self) {
    }
    /** was net/minecraft/class_4588.method_35666()V -- unsetDefaultColor(): default colours are gone in 1.21 */
    public static void method_35666(com.mojang.blaze3d.vertex.VertexConsumer self) {
    }
    /** was net/minecraft/class_287.method_35666()V */
    public static void method_35666(com.mojang.blaze3d.vertex.BufferBuilder self) {
    }
    /** was net/minecraft/class_4588.method_23763(Lorg/joml/Matrix3f;FFF)Lnet/minecraft/class_4588; -- normal(matrix, x, y, z): 1.20.1 transformed the normal by the matrix, then stored it */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_23763(com.mojang.blaze3d.vertex.VertexConsumer self, org.joml.Matrix3f m, float x, float y, float z) {
        org.joml.Vector3f n = m.transform(new org.joml.Vector3f(x, y, z));
        return self.setNormal(n.x(), n.y(), n.z());
    }
    /** was net/minecraft/class_287.method_23763(Lorg/joml/Matrix3f;FFF)Lnet/minecraft/class_4588; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_23763(com.mojang.blaze3d.vertex.BufferBuilder self, org.joml.Matrix3f m, float x, float y, float z) {
        return method_23763((com.mojang.blaze3d.vertex.VertexConsumer) self, m, x, y, z);
    }
    /** was net/minecraft/class_4588.method_23919(FFFFFFFFFIIFFF)V -- vertex(x,y,z, r,g,b,a, u,v, overlay, light, nx,ny,nz): colour is one packed int now */
    public static void method_23919(com.mojang.blaze3d.vertex.VertexConsumer self, float x, float y, float z, float r, float g, float b, float a,
            float u, float v, int overlay, int light, float nx, float ny, float nz) {
        self.addVertex(x, y, z, net.minecraft.util.ARGB.colorFromFloat(a, r, g, b), u, v, overlay, light, nx, ny, nz);
    }
    /** was net/minecraft/class_287.method_23919(FFFFFFFFFIIFFF)V */
    public static void method_23919(com.mojang.blaze3d.vertex.BufferBuilder self, float x, float y, float z, float r, float g, float b, float a,
            float u, float v, int overlay, int light, float nx, float ny, float nz) {
        method_23919((com.mojang.blaze3d.vertex.VertexConsumer) self, x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz);
    }
    /** was net/minecraft/class_287.method_22918(Lorg/joml/Matrix4f;FFF)Lnet/minecraft/class_4588; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22918(com.mojang.blaze3d.vertex.BufferBuilder self, org.joml.Matrix4f m, float x, float y, float z) {
        return self.addVertex(m, x, y, z);
    }
    /** was net/minecraft/class_287.method_22912(DDD)Lnet/minecraft/class_4588; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22912(com.mojang.blaze3d.vertex.BufferBuilder self, double x, double y, double z) {
        return self.addVertex((float) x, (float) y, (float) z);
    }
    /** was net/minecraft/class_287.method_22917(II)Lnet/minecraft/class_4588; -- overlayCoords(u, v) */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22917(com.mojang.blaze3d.vertex.BufferBuilder self, int u, int v) {
        return self.setUv1(u, v);
    }
    /** was net/minecraft/class_287.method_22916(I)Lnet/minecraft/class_4588; -- uv2(packedLight) */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22916(com.mojang.blaze3d.vertex.BufferBuilder self, int packedLight) {
        return self.setLight(packedLight);
    }

    // ---- RenderType static factories: RenderTypes since 1.21.x; chunk layers left RenderType
    //      (ChunkSectionLayer), the "moving block" variants are the same pipelines for ad-hoc drawing ----
    /** was net/minecraft/class_1921.method_23577()Lnet/minecraft/class_1921; -- RenderType.solid(): RenderTypes.solidMovingBlock() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23577() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.solidMovingBlock();
    }
    /** was net/minecraft/class_1921.method_23579()Lnet/minecraft/class_1921; -- RenderType.cutoutMipped(): RenderTypes.cutoutMovingBlock() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23579() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.cutoutMovingBlock();
    }
    /** was net/minecraft/class_1921.method_23581()Lnet/minecraft/class_1921; -- RenderType.cutout(): RenderTypes.cutoutMovingBlock() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23581() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.cutoutMovingBlock();
    }
    /** was net/minecraft/class_1921.method_23583()Lnet/minecraft/class_1921; -- RenderType.translucent(): RenderTypes.translucentMovingBlock() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23583() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.translucentMovingBlock();
    }
    /** was net/minecraft/class_1921.method_23585()Lnet/minecraft/class_1921; -- RenderType.translucentNoCrumbling(): RenderTypes.translucentMovingBlock() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23585() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.translucentMovingBlock();
    }
    /** was net/minecraft/class_1921.method_29380()Lnet/minecraft/class_1921; -- RenderType.translucentMovingBlock(): RenderTypes.translucentMovingBlock() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_29380() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.translucentMovingBlock();
    }
    /** was net/minecraft/class_1921.method_29997()Lnet/minecraft/class_1921; -- RenderType.tripwire(): RenderTypes.translucentMovingBlock() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_29997() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.translucentMovingBlock();
    }
    /** was net/minecraft/class_1921.method_23572(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.entitySolid(tex): RenderTypes.entitySolid(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23572(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entitySolid(tex);
    }
    /** was net/minecraft/class_1921.method_23576(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.entityCutout(tex): RenderTypes.entityCutoutCull(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23576(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutoutCull(tex);
    }
    /** was net/minecraft/class_1921.method_23578(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.entityCutoutNoCull(tex): RenderTypes.entityCutout(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23578(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(tex);
    }
    /** was net/minecraft/class_1921.method_23584(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.entitySmoothCutout(tex): RenderTypes.entityCutoutCull(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23584(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityCutoutCull(tex);
    }
    /** was net/minecraft/class_1921.method_23580(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.entityTranslucent(tex): RenderTypes.entityTranslucent(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23580(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(tex);
    }
    /** was net/minecraft/class_1921.method_24294(Lnet/minecraft/class_2960;Z)Lnet/minecraft/class_1921; -- RenderType.entityTranslucent(tex, outline): RenderTypes.entityTranslucent(tex, outline) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_24294(net.minecraft.resources.Identifier tex, boolean outline) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(tex, outline);
    }
    /** was net/minecraft/class_1921.method_23689(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.entityTranslucentCull(tex): RenderTypes.entityTranslucent(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23689(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(tex);
    }
    /** was net/minecraft/class_1921.method_29379(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.itemEntityTranslucentCull(tex): RenderTypes.itemTranslucent(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_29379(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.itemTranslucent(tex);
    }
    /** was net/minecraft/class_1921.method_25448(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.armorCutoutNoCull(tex): RenderTypes.armorCutoutNoCull(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_25448(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.armorCutoutNoCull(tex);
    }
    /** was net/minecraft/class_1921.method_23026(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.eyes(tex): RenderTypes.eyes(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23026(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.eyes(tex);
    }
    /** was net/minecraft/class_1921.method_23028(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921; -- RenderType.text(tex): RenderTypes.text(tex) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23028(net.minecraft.resources.Identifier tex) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.text(tex);
    }
    /** was net/minecraft/class_1921.method_23018(Lnet/minecraft/class_2960;FF)Lnet/minecraft/class_1921; -- RenderType.energySwirl(tex, u, v): RenderTypes.energySwirl(tex, u, v) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23018(net.minecraft.resources.Identifier tex, float u, float v) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.energySwirl(tex, u, v);
    }
    /** was net/minecraft/class_1921.method_23592(Lnet/minecraft/class_2960;Z)Lnet/minecraft/class_1921; -- RenderType.beaconBeam(tex, translucent): RenderTypes.beaconBeam(tex, translucent) */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23592(net.minecraft.resources.Identifier tex, boolean translucent) {
        return net.minecraft.client.renderer.rendertype.RenderTypes.beaconBeam(tex, translucent);
    }
    /** was net/minecraft/class_1921.method_23594()Lnet/minecraft/class_1921; -- RenderType.lines(): RenderTypes.lines() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23594() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.lines();
    }
    /** was net/minecraft/class_1921.method_23587()Lnet/minecraft/class_1921; -- RenderType.leash(): RenderTypes.leash() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23587() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.leash();
    }
    /** was net/minecraft/class_1921.method_23589()Lnet/minecraft/class_1921; -- RenderType.waterMask(): RenderTypes.waterMask() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23589() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.waterMask();
    }
    /** was net/minecraft/class_1921.method_23590()Lnet/minecraft/class_1921; -- RenderType.glint(): RenderTypes.glint() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23590() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.glint();
    }
    /** was net/minecraft/class_1921.method_29706()Lnet/minecraft/class_1921; -- RenderType.glintDirect(): RenderTypes.glint() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_29706() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.glint();
    }
    /** was net/minecraft/class_1921.method_23591()Lnet/minecraft/class_1921; -- RenderType.entityGlint(): RenderTypes.entityGlint() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23591() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.entityGlint();
    }
    /** was net/minecraft/class_1921.method_30676()Lnet/minecraft/class_1921; -- RenderType.glintTranslucent(): RenderTypes.glintTranslucent() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_30676() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.glintTranslucent();
    }
    /** was net/minecraft/class_1921.method_23593()Lnet/minecraft/class_1921; -- RenderType.lightning(): RenderTypes.lightning() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23593() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.lightning();
    }
    /** was net/minecraft/class_1921.method_23574()Lnet/minecraft/class_1921; -- RenderType.endPortal(): RenderTypes.endPortal() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23574() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.endPortal();
    }
    /** was net/minecraft/class_1921.method_34571()Lnet/minecraft/class_1921; -- RenderType.endGateway(): RenderTypes.endGateway() */
    public static net.minecraft.client.renderer.rendertype.RenderType method_34571() {
        return net.minecraft.client.renderer.rendertype.RenderTypes.endGateway();
    }

    // ---- BakedModel (class_1087) -> BlockStateModel: 1.21.5 split models into parts, 26.x made
    //      the particle texture a Material. The old owner is class-renamed to the real
    //      BlockStateModel; these answer the members that did not survive. ----
    private static java.util.List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> cbModelParts(net.minecraft.client.renderer.block.dispatch.BlockStateModel self, net.minecraft.util.RandomSource rand) {
        java.util.List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts = new java.util.ArrayList<>();
        self.collectParts(rand == null ? net.minecraft.util.RandomSource.create(42L) : rand, parts);
        return parts;
    }
    /** was net/minecraft/class_1087.method_4707(Lnet/minecraft/class_2680;Lnet/minecraft/class_2350;Lnet/minecraft/class_5819;)Ljava/util/List; -- getQuads(state, side, random): every part's quads for that side */
    public static java.util.List<net.minecraft.client.resources.model.geometry.BakedQuad> method_4707(net.minecraft.client.renderer.block.dispatch.BlockStateModel self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.Direction side, net.minecraft.util.RandomSource rand) {
        java.util.List<net.minecraft.client.resources.model.geometry.BakedQuad> out = new java.util.ArrayList<>();
        for (net.minecraft.client.renderer.block.dispatch.BlockStateModelPart part : cbModelParts(self, rand)) {
            out.addAll(part.getQuads(side));
        }
        return out;
    }
    /** was net/minecraft/class_1087.method_4708()Z -- useAmbientOcclusion(): any part */
    public static boolean method_4708(net.minecraft.client.renderer.block.dispatch.BlockStateModel self) {
        for (net.minecraft.client.renderer.block.dispatch.BlockStateModelPart part : cbModelParts(self, null)) {
            if (part.useAmbientOcclusion()) {
                return true;
            }
        }
        return false;
    }
    /** was net/minecraft/class_1087.method_4711()Lnet/minecraft/class_1058; -- getParticleIcon(): the particle material's sprite */
    public static net.minecraft.client.renderer.texture.TextureAtlasSprite method_4711(net.minecraft.client.renderer.block.dispatch.BlockStateModel self) {
        return self.particleMaterial().sprite();
    }
    /** was net/minecraft/class_1087.method_4712()Z -- isGui3d(): block-state models always were */
    public static boolean method_4712(net.minecraft.client.renderer.block.dispatch.BlockStateModel self) {
        return true;
    }
    /** was net/minecraft/class_1087.method_24304()Z -- usesBlockLight(): block-state models always did */
    public static boolean method_24304(net.minecraft.client.renderer.block.dispatch.BlockStateModel self) {
        return true;
    }
    /** was net/minecraft/class_1087.method_4713()Z -- isCustomRenderer(): custom renderers are item models now */
    public static boolean method_4713(net.minecraft.client.renderer.block.dispatch.BlockStateModel self) {
        return false;
    }
    /** was net/minecraft/class_1087.method_4709()Lnet/minecraft/class_809; -- getTransforms(): block-state models carry no item transforms (identity) */
    public static net.minecraft.client.resources.model.cuboid.ItemTransforms method_4709(net.minecraft.client.renderer.block.dispatch.BlockStateModel self) {
        return net.minecraft.client.resources.model.cuboid.ItemTransforms.NO_TRANSFORMS;
    }

    /** was net/minecraft/client/gui/components/events/GuiEventListener/mouseScrolled(DDD)Z */
    public boolean mouseScrolled(double a0, double a1, double a2) {
        return ((net.minecraft.client.gui.components.events.GuiEventListener) (Object) this).mouseScrolled(a0, a1, 0.0, a2);
    }

    /** was net/minecraft/client/multiplayer/ClientAdvancements$Listener.onSelectedTabChanged(Lnet/minecraft/advancements/Advancement;)V */
    public void onSelectedTabChanged(net.minecraft.advancements.Advancement a0) {
        ((net.minecraft.client.multiplayer.ClientAdvancements.Listener) (Object) this).onSelectedTabChanged(new net.minecraft.advancements.AdvancementHolder(net.minecraft.resources.Identifier.fromNamespaceAndPath("centurybridge", "dummy"), a0));
    }

    /** was net/minecraft/client/gui/font/providers/GlyphProviderDefinition/field_44801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_44801() {
        return net.minecraft.client.gui.font.providers.GlyphProviderDefinition.MAP_CODEC.codec();
    }

    /** was net/minecraft/client/multiplayer/chat/LoggedChatMessage$Player.field_40809Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40809() {
        return net.minecraft.client.multiplayer.chat.LoggedChatMessage.Player.CODEC.codec();
    }

    /** was net/minecraft/client/multiplayer/chat/LoggedChatMessage$System.field_40810Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40810() {
        return net.minecraft.client.multiplayer.chat.LoggedChatMessage.System.CODEC.codec();
    }

    /** was net/minecraft/client/renderer/texture/atlas/sources/DirectoryLister/field_41399Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41399() {
        return net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister.MAP_CODEC.codec();
    }

    /** was net/minecraft/client/renderer/texture/atlas/sources/SingleFile/field_41402Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41402() {
        return net.minecraft.client.renderer.texture.atlas.sources.SingleFile.MAP_CODEC.codec();
    }

    /** was net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$Tile.field_45655Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45655() {
        return (com.mojang.serialization.Codec) net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Tile.CODEC;
    }

    /** was com/mojang/blaze3d/opengl/GlProgram.field_29483Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_29483() {
        return org.slf4j.LoggerFactory.getLogger(com.mojang.blaze3d.opengl.GlProgram.class);
    }

    /** was net/minecraft/client/renderer/GameRenderer.field_3993Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_3993() {
        return org.slf4j.LoggerFactory.getLogger(net.minecraft.client.renderer.GameRenderer.class);
    }

    /** was net/minecraft/client/renderer/Sheets/armorTrimsSheet()Lnet/minecraft/client/renderer/rendertype/RenderType; */
    public static net.minecraft.client.renderer.rendertype.RenderType method_48480() {
        return net.minecraft.client.renderer.Sheets.armorTrimsSheet(false);
    }

    /** was net/minecraft/com.mojang.realmsclient.dto.RealmsServerPlayerLists.parse(Lcom/google/gson/JsonObject;)Lnet/minecraft/com.mojang.realmsclient.dto.RealmsServerPlayerLists; */
    public static com.mojang.realmsclient.dto.RealmsServerPlayerLists method_25074(com.google.gson.JsonObject a0) {
        return com.mojang.realmsclient.dto.RealmsServerPlayerLists.parse(a0.toString());
    }

    /** was net/minecraft/client/telemetry/TelemetryPropertyMap/createCodec(Ljava/util/List;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_47761(java.util.List a0) {
        return net.minecraft.client.telemetry.TelemetryPropertyMap.createCodec(a0).codec();
    }

    /** was net/minecraft/client/renderer/entity/EndCrystalRenderer.getY(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;F)F */
    public static float method_23155(net.minecraft.world.entity.boss.enderdragon.EndCrystal a0, float a1) {
        return net.minecraft.client.renderer.entity.EndCrystalRenderer.getY(a1);
    }

    /** was net/minecraft/class_638.method_2942(ILnet/minecraft/class_1297;)V -- ClientWorld.addEntity(id, entity):
     *  1.20.1 discarded whatever entity held that id, then added this one */
    public static void method_2942(net.minecraft.client.multiplayer.ClientLevel self, int id, net.minecraft.world.entity.Entity e) {
        self.removeEntity(id, net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        self.addEntity(e);
    }
}
