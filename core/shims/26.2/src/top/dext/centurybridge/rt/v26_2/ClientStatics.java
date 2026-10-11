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

    /** was net/minecraft/client/Minecraft/emergencySave()V */
    public static void method_1519(net.minecraft.client.Minecraft self) {
        throw new UnsupportedOperationException("CenturyBridge: emergencySave (handleInputEvents) was removed and its functionality was merged into client tick");
    }

    /** was net/minecraft/client/Minecraft/method_1537(Z)V */
    public static void method_1537(net.minecraft.client.Minecraft self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: setConnectedToRealms is no longer supported as this state is now automatically derived from the network handler.");
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

    /** was net/minecraft/com.mojang.blaze3d.audio.Listener.method_19671(Lnet/minecraft/world/phys/Vec3;)V */
    public static void method_19671(com.mojang.blaze3d.audio.Listener self, net.minecraft.world.phys.Vec3 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_19671 is no longer supported on FogData");
    }

    /** was net/minecraft/com.mojang.blaze3d.audio.Listener.method_19672(Lorg/joml/Vector3f;Lorg/joml/Vector3f;)V */
    public static void method_19672(com.mojang.blaze3d.audio.Listener self, org.joml.Vector3f a0, org.joml.Vector3f a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_19672 was deleted and cannot be reconstructed from the new Camera API");
    }

    /** was net/minecraft/com.mojang.blaze3d.audio.Listener.method_27268()Lnet/minecraft/world/phys/Vec3; */
    public static net.minecraft.world.phys.Vec3 method_27268(com.mojang.blaze3d.audio.Listener self) {
        throw new UnsupportedOperationException("CenturyBridge: method_27268 was removed from com.mojang.blaze3d.audio.Listener with no replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.field_19576Lnet/minecraft/com.mojang.realmsclient.client.RealmsClient$class_4343; */
    public static com.mojang.realmsclient.client.RealmsClient.Environment field_19576() {
        return com.mojang.realmsclient.client.RealmsClient.ENVIRONMENT;
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21001()V */
    public static void method_21001(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.client.RealmsClient.method_21001() was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21008()V */
    public static void method_21008(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: method_21008 has no replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21012()V */
    public static void method_21012(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.client.RealmsClient.method_21012 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21021()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21021(com.mojang.realmsclient.client.RealmsClient self) {
        return java.lang.Boolean.FALSE;
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21024()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21024(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: RealmsClient.trialAvailable (method_21024) was removed in 1.20.2");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.Request.method_35685(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/com.mojang.realmsclient.client.Request; */
    public static com.mojang.realmsclient.client.Request method_35685(com.mojang.realmsclient.client.Request self, java.lang.String a0, java.lang.String a1) {
        self.cookie(a0, a1);
        return self;
    }

    /** was net/minecraft/com.mojang.realmsclient.util.task.LongRunningTask.field_19638Lnet/minecraft/com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen; */
    public static com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen field_19638() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.util.task.LongRunningTask.field_19638 (com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.util.task.LongRunningTask.method_21066(Lnet/minecraft/com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;)V */
    public static void method_21066(com.mojang.realmsclient.util.task.LongRunningTask self, com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21066 (addLabel) is no longer supported as RealmsLabel has been removed in 1.20.2");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.selectTemplate()V */
    public static void method_21440(com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen self) {
        throw new UnsupportedOperationException("CenturyBridge: selectTemplate was removed from ConnectScreen in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.method_25228(I)I */
    public static int method_25228(com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen self, int a0) {
        return 40 + a0 * 13;
    }

    /** was net/minecraft/client/gui/screens/achievement/StatsScreen/method_19399()Lnet/minecraft/client/gui/components/ObjectSelectionList; */
    public static net.minecraft.client.gui.components.ObjectSelectionList method_19399(net.minecraft.client.gui.screens.achievement.StatsScreen self) {
        throw new UnsupportedOperationException("CenturyBridge: State getter was removed and state is no longer accessible");
    }


    /** was net/minecraft/client/gui/screens/advancements/AdvancementWidgetType/method_2320()I */
    public static int method_2320(net.minecraft.client.gui.screens.advancements.AdvancementWidgetType self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.advancements.AdvancementWidgetType.method_2320() (Difficulty.getId()) was removed in 1.20.2; difficulties are now registry-backed and identified by ResourceLocation via boxSprite(), with no stable int id equivalent");
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



    /** was net/minecraft/client/gui/screens/worldselection/WorldOpenFlows/method_41890(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Z)Lnet/minecraft/server/WorldStem; */
    public static net.minecraft.server.WorldStem method_41890(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows self, net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: synchronous world loading from session is no longer supported");
    }

    /** was net/minecraft/client/gui/screens/worldselection/WorldOpenFlows/method_41894(Lnet/minecraft/client/gui/screens/Screen;Ljava/lang/String;)V */
    public static void method_41894(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows self, net.minecraft.client.gui.screens.Screen a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.worldselection.WorldOpenFlows.method_41894(Screen,String) created a world from the CreateWorldScreen instance's internal state (data packs, level storage, generator options); 1.20.3 replaced it with createFreshLevel(String,net.minecraft.world.level.LevelSettings,net.minecraft.world.level.levelgen.WorldOptions,Function,net.minecraft.client.gui.screens.Screen) which requires those as explicit arguments, and the shim has no access to the receiver's fields to supply them");
    }




    /** was net/minecraft/client/player/AbstractClientPlayer/method_3120(Lnet/minecraft/resources/Identifier;Ljava/lang/String;)V */
    public static void method_3120(net.minecraft.client.player.AbstractClientPlayer self, net.minecraft.resources.Identifier a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: registerSkinTexture is not supported in 1.20.2 as skin loading is managed by PlayerSkinProvider");
    }

    /** was net/minecraft/client/player/AbstractClientPlayer/method_3121()Ljava/lang/String; */
    public static java.lang.String method_3121(net.minecraft.client.player.AbstractClientPlayer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_3121 (getSkinType) was removed; use getSkin (getSkinTextures) and extract the model from net.minecraft.world.entity.player.PlayerSkin instead");
    }



    /** was net/minecraft/client/player/AbstractClientPlayer/method_3126()Z */
    public static boolean method_3126(net.minecraft.client.player.AbstractClientPlayer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.player.AbstractClientPlayer.method_3126()Z was deleted in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/client/gui/components/MultiLineEditBox/method_44406()V */
    public static void method_44406(net.minecraft.client.gui.components.MultiLineEditBox self) {
        return;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.RealmsDataFetcher.field_39684Lnet/minecraft/com.mojang.realmsclient.gui.task.DataFetcher$class_7586; */
    public static com.mojang.realmsclient.gui.task.DataFetcher.Task field_39684() {
        throw new UnsupportedOperationException("CenturyBridge: field_39684 was removed in 1.20.2 with no direct replacement; the registered com.mojang.realmsclient.gui.task.DataFetcher$class_7586 instance no longer exists");
    }

    /** was net/minecraft/client/renderer/GameRenderer/method_34500()Lnet/minecraft/com.mojang.blaze3d.opengl.GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34500(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: getPositionColorLightmapShader was removed");
    }

    /** was net/minecraft/client/renderer/texture/SpriteLoader/method_45829(Lnet/minecraft/resources/Identifier;Lnet/minecraft/server/packs/resources/Resource;)Lnet/minecraft/client/renderer/texture/SpriteContents; */
    public static net.minecraft.client.renderer.texture.SpriteContents method_45829(net.minecraft.client.renderer.texture.SpriteLoader self, net.minecraft.resources.Identifier a0, net.minecraft.server.packs.resources.Resource a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_45829 (SpriteLoader.load(Identifier, Resource)) was removed in 1.20.2; the new API is async (CompletableFuture-based) and takes a ResourceManager or List instead of a single Resource, with no synchronous single-resource equivalent");
    }

    /** was net/minecraft/client/gui/components/tabs/Tab/method_48613()V */
    public static void method_48613(net.minecraft.client.gui.components.tabs.Tab self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.components.tabs.Tab.method_48613 was removed in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/client/gui/components/tabs/TabManager/method_48617()V */
    public static void method_48617(net.minecraft.client.gui.components.tabs.TabManager self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.components.tabs.TabManager.method_48617 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/client/gui/layouts/HeaderAndFooterLayout/method_49001()Lnet/minecraft/client/gui/layouts/LayoutSettings; */
    public static net.minecraft.client.gui.layouts.LayoutSettings method_49001(net.minecraft.client.gui.layouts.HeaderAndFooterLayout self) {
        throw new UnsupportedOperationException("CenturyBridge: HeaderAndFooterLayoutWidget no longer exposes its default positioner");
    }

    /** was net/minecraft/client/gui/layouts/HeaderAndFooterLayout/method_49002()Lnet/minecraft/client/gui/layouts/LayoutSettings; */
    public static net.minecraft.client.gui.layouts.LayoutSettings method_49002(net.minecraft.client.gui.layouts.HeaderAndFooterLayout self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.layouts.HeaderAndFooterLayout.method_49002() was deleted in 1.20.2 with no direct replacement returning net.minecraft.client.gui.layouts.LayoutSettings");
    }

    /** was net/minecraft/client/gui/layouts/HeaderAndFooterLayout/method_49003()Lnet/minecraft/client/gui/layouts/LayoutSettings; */
    public static net.minecraft.client.gui.layouts.LayoutSettings method_49003(net.minecraft.client.gui.layouts.HeaderAndFooterLayout self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49003() was removed in 1.20.2 with no direct replacement; the new net.minecraft.client.gui.layouts.HeaderAndFooterLayout API does not produce net.minecraft.client.gui.layouts.LayoutSettings instances");
    }

    /** was net/minecraft/client/gui/components/TabButton/method_49607()I */
    public static int method_49607(net.minecraft.client.gui.components.TabButton self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49607 returned a creative tab index (int), but 1.20.2 replaced tab indices with ItemGroup references (net.minecraft.client.gui.components.tabs.Tab); use tab() instead");
    }

    /** was net/minecraft/client/renderer/entity/BatRenderer/method_3882(Lnet/minecraft/world/entity/ambient/Bat;Lnet/minecraft/com.mojang.blaze3d.vertex.PoseStack;FFF)V */
    public static void method_3882(net.minecraft.client.renderer.entity.BatRenderer self, net.minecraft.world.entity.ambient.Bat a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2, float a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_3882(net.minecraft.world.entity.ambient.Bat, com.mojang.blaze3d.vertex.PoseStack, float, float, float) was deleted in 1.20.3 with no direct replacement; remaining getTextureLocation(net.minecraft.world.entity.ambient.Bat) returns net.minecraft.resources.Identifier and drops the MatrixStack/position parameters, so the old render-style contract cannot be expressed");
    }

    /** was net/minecraft/client/renderer/entity/BatRenderer/method_3884(Lnet/minecraft/world/entity/ambient/Bat;Lnet/minecraft/com.mojang.blaze3d.vertex.PoseStack;F)V */
    public static void method_3884(net.minecraft.client.renderer.entity.BatRenderer self, net.minecraft.world.entity.ambient.Bat a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_3884 is unsupported because modern rendering requires a VertexConsumerProvider and light map coordinates which are missing from this signature.");
    }

    /** was net/minecraft/client/Minecraft/method_1540()Z */
    public static boolean method_1540(net.minecraft.client.Minecraft self) {
        throw new UnsupportedOperationException("CenturyBridge: MinecraftClient.isDemo has no equivalent in 1.20.5");
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

    /** was net/minecraft/client/Options$3.field_28781Lnet/minecraft/client/Options; */
    public static net.minecraft.client.Options field_28781() {
        throw new UnsupportedOperationException("CenturyBridge: field_28781 was an instance field of a deleted anonymous class and cannot be accessed without a receiver");
    }

    /** was net/minecraft/com.mojang.blaze3d.font.GlyphBitmap.method_41712()F */
    public static float method_41712(com.mojang.blaze3d.font.GlyphBitmap self) {
        return (float)self.getPixelWidth() / (float)self.getPixelHeight();
    }

    /** was net/minecraft/com.mojang.blaze3d.font.GlyphBitmap.method_41713()F */
    public static float method_41713(com.mojang.blaze3d.font.GlyphBitmap self) {
        return 1.0f;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.method_21155(I)V */
    public static void method_21155(com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21155(int) switched tabs by ordinal index; 1.20.5 CreateWorldScreen uses com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen$class_4382 tab objects via backupList/layout with no int-indexed equivalent");
    }

    /** was net/minecraft/client/renderer/Sheets/method_24066(Ljava/util/function/Consumer;)V */
    public static void method_24066(net.minecraft.client.renderer.Sheets self, java.util.function.Consumer a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.Sheets.method_24066(Consumer) was removed in 1.20.5; the model loading pipeline no longer collects model identifiers via a consumer callback");
        // CB-CONFIDENCE: high -- method explicitly deleted with no direct replacement; the 1.20.5 model loading pipeline was reworked and no new API accepts a Consumer of identifiers
    }

    /** was net/minecraft/client/gui/screens/multiplayer/JoinMultiplayerScreen/method_2528(Ljava/util/List;)V */
    public static void method_2528(net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen self, java.util.List a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen.method_2528(List) was removed in 1.20.5 with no direct replacement; no method in the new API accepts a List");
        // CB-CONFIDENCE: high -- the method is explicitly deleted with no direct replacement, and no remaining method in net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen accepts a List parameter
    }

    /** was net/minecraft/client/gui/screens/debug/GameModeSwitcherScreen$GameModeSlot.field_24585Lnet/minecraft/client/gui/screens/debug/GameModeSwitcherScreen; */
    public static net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen field_24585() {
        throw new UnsupportedOperationException("CenturyBridge: field_24585 was the synthetic outer net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen reference (this$0) of inner class net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen$class_5291; it is an instance field bound to a specific instance and cannot be reconstructed as a static constant without a receiver");
    }

    /** was net/minecraft/client/model/geom/ModelLayers/field_47444Lnet/minecraft/client/model/geom/ModelLayerLocation; */
    public static net.minecraft.client.model.geom.ModelLayerLocation field_47444() {
        throw new UnsupportedOperationException("CenturyBridge: field_47444 was removed in 1.20.5 with no direct replacement; cannot determine which PositionType it corresponded to");
    }

    /** was net/minecraft/client/model/geom/ModelLayers/field_47445Lnet/minecraft/client/model/geom/ModelLayerLocation; */
    public static net.minecraft.client.model.geom.ModelLayerLocation field_47445() {
        throw new UnsupportedOperationException("CenturyBridge: field_47445 (a ParticleType constant in ParticleTypes) was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/client/multiplayer/MultiPlayerGameMode/method_2904()F */
    public static float method_2904(net.minecraft.client.multiplayer.MultiPlayerGameMode self) {
        return (float) self.getDestroyStage();
        // CB-CONFIDENCE: medium -- getDestroyStage is the likely int-returning replacement for the old float getBlockReachDistance; the int-to-float cast loses the 0.5 fractional reach in survival mode
    }

    /** was net/minecraft/client/gui/narration/ScreenNarrationCollector$1.field_33799Lnet/minecraft/client/gui/narration/ScreenNarrationCollector; */
    public static net.minecraft.client.gui.narration.ScreenNarrationCollector field_33799() {
        throw new UnsupportedOperationException("CenturyBridge: field_33799 was the synthetic this$0 outer-instance reference of net.minecraft.client.gui.narration.ScreenNarrationCollector$1, deleted in 1.20.5 because the anonymous class no longer references its enclosing net.minecraft.client.gui.narration.ScreenNarrationCollector; a per-instance outer reference cannot be rebuilt as a static constant");
    }

    /** was net/minecraft/client/multiplayer/ClientLevel/method_2944(Lnet/minecraft/world/scores/Scoreboard;)V */
    public static void method_2944(net.minecraft.client.multiplayer.ClientLevel self, net.minecraft.world.scores.Scoreboard a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.multiplayer.ClientLevel.method_2944(net.minecraft.world.scores.Scoreboard) (setParticleManager) was removed; 1.20.5 net.minecraft.client.multiplayer.ClientLevel only exposes M() to get the particle manager, with no setter");
        // CB-CONFIDENCE: high -- old method_2944 is the particle-manager setter; new ClientWorld exposes only getter M() and no net.minecraft.world.scores.Scoreboard-accepting setter remains
    }

    /** was net/minecraft/client/gui/screens/multiplayer/WarningScreen/method_41160(I)V */
    public static void method_41160(net.minecraft.client.gui.screens.multiplayer.WarningScreen self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.multiplayer.WarningScreen.method_41160(I)V was removed in 1.20.5 with no direct replacement; the new API surface has no int-parameter method on this class");
    }

    /** was net/minecraft/client/gui/screens/worldselection/WorldOpenFlows/method_54618(Ljava/lang/String;Ljava/lang/Runnable;)V */
    public static void method_54618(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows self, java.lang.String a0, java.lang.Runnable a1) {
        self.openWorld(a0, a1);
    }

    /** was net/minecraft/client/particle/SuspendedParticle$SporeBlossomAirProvider$1.field_29074Lnet/minecraft/client/particle/SuspendedParticle$SporeBlossomAirProvider; */
    public static net.minecraft.client.particle.SuspendedParticle.SporeBlossomAirProvider field_29074() {
        throw new UnsupportedOperationException("CenturyBridge: field_29074 was a static net.minecraft.client.particle.SuspendedParticle$class_5877 constant in the anonymous inner class net.minecraft.client.particle.SuspendedParticle$class_5877$1, deleted in 1.20.5 with no replacement; the original constant value cannot be reconstructed from the new API");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface provides no way to reconstruct the original constant value
    }

    /** was net/minecraft/client/renderer/GameRenderer/method_3168(Lnet/minecraft/resources/Identifier;)V */
    public static void method_3168(net.minecraft.client.renderer.GameRenderer self, net.minecraft.resources.Identifier a0) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_3168(Identifier) loadPostProcessor was removed in 1.20.5; post-processing pipeline reworked, no equivalent loader exists on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/gui/components/Tooltip/method_54383(I)V */
    public static void method_54383(net.minecraft.client.gui.components.Tooltip self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_54383(int) was removed in 1.20.5 with no direct replacement; the new net.minecraft.client.gui.components.Tooltip API has no int-parameterized instance method to delegate to");
    }

    /** was net/minecraft/client/gui/components/Tooltip/method_54384(ZZLnet/minecraft/client/gui/navigation/ScreenRectangle;)V */
    public static void method_54384(net.minecraft.client.gui.components.Tooltip self, boolean a0, boolean a1, net.minecraft.client.gui.navigation.ScreenRectangle a2) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.components.Tooltip.method_54384(ZZLnet/minecraft/client/gui/navigation/ScreenRectangle;)V was deleted in 1.20.5 with no direct replacement; remaining net.minecraft.client.gui.components.Tooltip methods (b, toCharSequence-47408) accept incompatible parameter types and cannot express the (boolean, boolean, net.minecraft.client.gui.navigation.ScreenRectangle) contract");
    }

    /** was net/minecraft/client/gui/components/Tooltip/method_54385(ZZLnet/minecraft/client/gui/navigation/ScreenRectangle;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner; */
    public static net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner method_54385(net.minecraft.client.gui.components.Tooltip self, boolean a0, boolean a1, net.minecraft.client.gui.navigation.ScreenRectangle a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_54385 was removed in 1.20.5 with no direct replacement; net.minecraft.client.gui.components.Tooltip's remaining API has no method returning net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner or accepting net.minecraft.client.gui.navigation.ScreenRectangle");
    }

    /** was net/minecraft/client/renderer/debug/ChunkDebugRenderer$ChunkData.field_20999Lnet/minecraft/client/renderer/debug/ChunkDebugRenderer; */
    public static net.minecraft.client.renderer.debug.ChunkDebugRenderer field_20999() {
        return null;
    }


    /** was net/minecraft/client/model/monster/breeze/BreezeModel/method_55234()Lnet/minecraft/client/model/geom/builders/LayerDefinition; */
    public static net.minecraft.client.model.geom.builders.LayerDefinition method_55234(net.minecraft.client.model.monster.breeze.BreezeModel self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.monster.breeze.BreezeModel.method_55234() was deleted in 1.20.5 with no direct replacement; the only remaining method returning net.minecraft.client.model.geom.builders.LayerDefinition is createBodyLayer(int,int) which requires two int parameters not available from the old no-arg signature");
    }

    /** was net/minecraft/client/model/monster/breeze/BreezeModel/method_55235()Lnet/minecraft/client/model/geom/builders/LayerDefinition; */
    public static net.minecraft.client.model.geom.builders.LayerDefinition method_55235(net.minecraft.client.model.monster.breeze.BreezeModel self) {
        throw new UnsupportedOperationException("CenturyBridge: method_55235() returned net.minecraft.client.model.geom.builders.LayerDefinition with no arguments; the only new-API equivalent createBodyLayer(II) requires two int parameters whose values cannot be inferred from the old call site");
    }

    /** was net/minecraft/client/renderer/entity/VindicatorRenderer$1.field_4805Lnet/minecraft/client/renderer/entity/VindicatorRenderer; */
    public static net.minecraft.client.renderer.entity.VindicatorRenderer field_4805() {
        throw new UnsupportedOperationException("CenturyBridge: field_4805 was the synthetic outer instance reference (this$0) of net.minecraft.client.renderer.entity.VindicatorRenderer$1; it is an instance field that cannot be reconstructed from a static context without an instance of the inner class");
    }



    /** was net/minecraft/client/renderer/rendertype/RenderType/method_23012(Lnet/minecraft/com.mojang.blaze3d.vertex.BufferBuilder;Lnet/minecraft/com.mojang.blaze3d.vertex.VertexSorting;)V */
    public static void method_23012(net.minecraft.client.renderer.rendertype.RenderType self, com.mojang.blaze3d.vertex.BufferBuilder a0, com.mojang.blaze3d.vertex.VertexSorting a1) {
        throw new UnsupportedOperationException("CenturyBridge: RenderLayer.draw(BufferBuilder, VertexConsumerProvider.Immediate) was removed in 1.21's rendering pipeline overhaul; net.minecraft.client.renderer.rendertype.RenderType no longer owns buffer-drawing functionality");
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

    /** was net/minecraft/com.mojang.realmsclient.dto.RealmsServer.method_25059(Lnet/minecraft/com.mojang.realmsclient.dto.RealmsServerPlayerLists;)V */
    public static void method_25059(com.mojang.realmsclient.dto.RealmsServer self, com.mojang.realmsclient.dto.RealmsServerPlayerLists a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25059(com.mojang.realmsclient.dto.RealmsServerPlayerLists) was removed in 1.21 with no direct replacement; no field or method on com.mojang.realmsclient.dto.RealmsServer accepts com.mojang.realmsclient.dto.RealmsServerPlayerLists");
    }

    /** was net/minecraft/client/gui/screens/options/OnlineOptionsScreen/method_47621(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Options;)Lnet/minecraft/client/gui/screens/options/OnlineOptionsScreen; */
    public static net.minecraft.client.gui.screens.options.OnlineOptionsScreen method_47621(net.minecraft.client.gui.screens.options.OnlineOptionsScreen self, net.minecraft.client.Minecraft a0, net.minecraft.client.gui.screens.Screen a1, net.minecraft.client.Options a2) {
        return new net.minecraft.client.gui.screens.options.OnlineOptionsScreen(a1, a2);
        // CB-CONFIDENCE: medium -- the new constructor takes exactly the Screen and GameOptions args the old factory accepted; the MinecraftClient arg has no equivalent in the new API and is dropped, which is the most faithful bridge available.
    }
























































































































































































































































































































































































































































































































































































































































































































    /** was net/minecraft/client/renderer/rendertype/RenderType.method_23585()Lnet/minecraft/client/renderer/rendertype/RenderType; */
    public static net.minecraft.client.renderer.rendertype.RenderType method_23585(net.minecraft.client.renderer.rendertype.RenderType self) {
        throw new UnsupportedOperationException("CenturyBridge: method_23585 was deleted with no direct replacement in 26.2");
        // CB-CONFIDENCE: high -- the member is explicitly marked deleted with no direct replacement; no equivalent exists in the new API surface
    }

    /** was net/minecraft/client/player/AbstractClientPlayer.method_3117()Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3117(net.minecraft.client.player.AbstractClientPlayer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_3117 was deleted in 26.2 with no direct replacement; new ClientPlayerEntity API has no method returning Identifier (net.minecraft.resources.Identifier)");
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

    /** was com/mojang/blaze3d/platform/Window.method_16000()I */
    public static int method_16000(com.mojang.blaze3d.platform.Window self) {
        throw new UnsupportedOperationException("CenturyBridge: Window.getFramerateLimit() (method_16000) was removed in 1.21.2; the framerate limit is no longer readable from Window (setter setGuiScale remains but the value is not exposed)");
    }

    /** was net/minecraft/client/renderer/texture/TextureManager.method_22813(Lnet/minecraft/resources/Identifier;)V */
    public static void method_22813(net.minecraft.client.renderer.texture.TextureManager self, net.minecraft.resources.Identifier a0) {
        self.release(a0);
    }

    /** was net/minecraft/class_1095.field_5425Lnet/minecraft/client/renderer/texture/TextureAtlasSprite; */
    public static net.minecraft.client.renderer.texture.TextureAtlasSprite field_5425() {
        throw new UnsupportedOperationException("CenturyBridge: field_5425 (static Identifier on class_1095) was removed in 1.21.2 with no direct equivalent; the constant value cannot be reconstructed from the new API");
    }

    /** was net/minecraft/client/resources/model/UnbakedModel.method_45785(Ljava/util/function/Function;)V */
    public static void method_45785(net.minecraft.client.resources.model.UnbakedModel self, java.util.function.Function a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_45785 was removed with no direct replacement; resolveDependencies requires net.minecraft.client.resources.model.UnbakedModel$class_10103 (constructor unknown) and method_4753 requires additional net.minecraft.client.resources.model.ModelBaker/class_3665 parameters not available from the old contract");
    }

    /** was net/minecraft/client/tutorial/Tutorial.method_31364(Lnet/minecraft/client/gui/components/toasts/TutorialToast;)V */
    public static void method_31364(net.minecraft.client.tutorial.Tutorial self, net.minecraft.client.gui.components.toasts.TutorialToast a0) {
        throw new UnsupportedOperationException("CenturyBridge: InGameHud.setSubtitle(Text) was removed in 1.21.2 with no direct replacement on net.minecraft.client.tutorial.Tutorial; title/subtitle management moved elsewhere");
    }

    /** was net/minecraft/client/renderer/rendertype/RenderType.method_29707()Lnet/minecraft/client/renderer/rendertype/RenderType; */
    public static net.minecraft.client.renderer.rendertype.RenderType method_29707(net.minecraft.client.renderer.rendertype.RenderType self) {
        throw new UnsupportedOperationException("CenturyBridge: method_29707 (outline) was removed in 1.21.2; outline rendering was reworked with no direct equivalent on net.minecraft.client.renderer.rendertype.RenderType");
    }

    /** was net/minecraft/client/ClientRecipeBook.method_1401(Ljava/lang/Iterable;Lnet/minecraft/core/RegistryAccess;)V */
    public static void method_1401(net.minecraft.client.ClientRecipeBook self, java.lang.Iterable a0, net.minecraft.core.RegistryAccess a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.ClientRecipeBook.method_1401(Iterable, net.minecraft.core.RegistryAccess) was removed in 1.21.2; new API methods take net.minecraft.world.item.crafting.display.RecipeDisplayEntry/class_10298 instead of Iterable+Registry, no equivalent exists");
    }

    /** was net/minecraft/client/Minecraft.method_1524(I)V */
    public static void method_1524(net.minecraft.client.Minecraft self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_1524(int) (setFramerateTarget) was removed in 1.21.2 with no direct replacement on MinecraftClient");
    }

    /** was net/minecraft/client/Minecraft.method_16011()Lnet/minecraft/util/profiling/ProfilerFiller; */
    public static net.minecraft.util.profiling.ProfilerFiller method_16011(net.minecraft.client.Minecraft self) {
        throw new UnsupportedOperationException("CenturyBridge: method_16011 (getWindow) was removed in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/client/Minecraft.method_60646()Lnet/minecraft/client/DeltaTracker; */
    public static net.minecraft.client.DeltaTracker method_60646(net.minecraft.client.Minecraft self) {
        throw new UnsupportedOperationException("CenturyBridge: MinecraftClient.method_60646() returning net.minecraft.client.DeltaTracker (ServerLinks) was removed in 1.21.2 with no direct replacement on net.minecraft.client.Minecraft; server links must now be obtained through a different path not exposed on MinecraftClient");
    }

    /** was net/minecraft/client/Options.method_1631(Lnet/minecraft/world/entity/player/PlayerModelPart;Z)V */
    public static void method_1631(net.minecraft.client.Options self, net.minecraft.world.entity.player.PlayerModelPart a0, boolean a1) {
        self.setModelPart(a0, a1);
    }

    /** was net/minecraft/client/Options.method_1641(Lnet/minecraft/client/KeyMapping;Lcom/mojang/blaze3d/platform/InputConstants$Key;)V */
    public static void method_1641(net.minecraft.client.Options self, net.minecraft.client.KeyMapping a0, com.mojang.blaze3d.platform.InputConstants.Key a1) {
        throw new UnsupportedOperationException("CenturyBridge: FontManager.method_1641(TextRenderer, FontStorage) was removed in 1.21.2 with no direct replacement; the font storage/registration architecture was reworked and no method on net.minecraft.client.Options accepts the old (net.minecraft.client.KeyMapping, com.mojang.blaze3d.platform.InputConstants.Key) pair");
        // CB-CONFIDENCE: high -- the method is explicitly deleted with no direct replacement, and the new net.minecraft.client.Options API surface contains no method accepting both a TextRenderer and a FontStorage-style argument, so a tombstone is the correct honest failure
    }



    /** was net/minecraft/client/renderer/entity/IllagerRenderer.method_16460(Lnet/minecraft/world/entity/monster/illager/AbstractIllager;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_16460(net.minecraft.client.renderer.entity.IllagerRenderer self, net.minecraft.world.entity.monster.illager.AbstractIllager a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.IllagerRenderer.method_16460 was removed in 1.21.2; extractRenderState requires net.minecraft.client.renderer.entity.state.IllagerRenderState which cannot be derived from com.mojang.blaze3d.vertex.PoseStack (MatrixStack) without additional context");
    }

    /** was net/minecraft/client/model/Model.method_2828(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V */
    public static void method_2828(net.minecraft.client.model.Model self, com.mojang.blaze3d.vertex.PoseStack a0, com.mojang.blaze3d.vertex.VertexConsumer a1, int a2, int a3, int a4) {
        self.renderToBuffer(a0, a1, a2, a3, a4);
    }

    /** was net/minecraft/client/model/monster/zombie/AbstractZombieModel.method_17790(Lnet/minecraft/world/entity/monster/Monster;)Z */
    public static boolean method_17790(net.minecraft.client.model.monster.zombie.AbstractZombieModel self, net.minecraft.world.entity.monster.Monster a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.monster.zombie.AbstractZombieModel.method_17790(net.minecraft.world.entity.monster.Monster)Z was deleted in 1.21.2; surviving methods take render-context types (net.minecraft.client.renderer.entity.state.EntityRenderState/class_10034/class_10086) and return void, so the ItemStack->boolean contract has no equivalent");
    }

    /** was net/minecraft/client/model/monster/zombie/GiantZombieModel.method_17792(Lnet/minecraft/world/entity/monster/Giant;)Z */
    public static boolean method_17792(net.minecraft.client.model.monster.zombie.GiantZombieModel self, net.minecraft.world.entity.monster.Giant a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.monster.zombie.GiantZombieModel.method_17792(net.minecraft.world.entity.monster.Giant) was removed in 1.21.2 with no direct replacement; the new net.minecraft.client.model.monster.zombie.GiantZombieModel only exposes a constructor and no boolean query taking net.minecraft.world.entity.monster.Giant");
    }

    /** was net/minecraft/client/particle/ParticleRenderType.field_17830Lnet/minecraft/client/particle/ParticleRenderType; */
    public static net.minecraft.client.particle.ParticleRenderType field_17830() {
        throw new UnsupportedOperationException("CenturyBridge: field_17830 (EQUAL_DEPTH_TEST) was removed in 1.21.2; the RenderPhase depth-test system was refactored and this constant has no equivalent");
    }

    /** was com/mojang/realmsclient/gui/screens/configuration/RealmsBackupScreen$BackupObjectSelectionList.field_19760Lcom/mojang/realmsclient/gui/screens/configuration/RealmsBackupScreen; */
    public static com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen field_19760() {
        throw new UnsupportedOperationException("CenturyBridge: field_19760 is the synthetic outer-class reference (this$0) on com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen$class_4382 and cannot be returned from a parameterless static helper");
    }

    /** was net/minecraft/client/model/animal/bee/BeeModel.method_22111(Lnet/minecraft/world/entity/animal/bee/Bee;FFF)V */
    public static void method_22111(net.minecraft.client.model.animal.bee.BeeModel self, net.minecraft.world.entity.animal.bee.Bee a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_22111(net.minecraft.world.entity.animal.bee.Bee, F, F, F) was removed in 1.21.2; remaining methods on net.minecraft.client.model.animal.bee.BeeModel take net.minecraft.client.renderer.entity.state.EntityRenderState/class_10002 with no known conversion from net.minecraft.world.entity.animal.bee.Bee + 3 floats");
    }


    /** was net/minecraft/client/gui/screens/options/controls/KeyBindsList.method_57747(Lnet/minecraft/client/gui/screens/options/controls/KeyBindsList;)Lnet/minecraft/client/Minecraft; */
    public static net.minecraft.client.Minecraft method_57747(net.minecraft.client.gui.screens.options.controls.KeyBindsList self, net.minecraft.client.gui.screens.options.controls.KeyBindsList a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.options.controls.KeyBindsList.method_57747 was deleted in 1.21.2 with no direct replacement among remaining Keyboard methods");
    }

    /** was net/minecraft/client/renderer/Sheets.method_24076()Lnet/minecraft/client/renderer/rendertype/RenderType; */
    public static net.minecraft.client.renderer.rendertype.RenderType method_24076(net.minecraft.client.renderer.Sheets self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.Sheets.method_24076 was deleted in 1.21.2 with no direct replacement; BufferBuilderStorage vertex consumer provider API was restructured");
        // CB-CONFIDENCE: medium -- the method is explicitly marked deleted with no direct replacement; multiple new net.minecraft.client.renderer.rendertype.RenderType-returning methods exist but none can be confidently identified as the semantic equivalent without knowing the original method's exact purpose
    }

    /** was net/minecraft/client/model/monster/piglin/PiglinModel.field_27464Lnet/minecraft/client/model/geom/ModelPart; */
    public static net.minecraft.client.model.geom.ModelPart field_27464() {
        throw new UnsupportedOperationException("CenturyBridge: field_27464 was a static net.minecraft.client.model.geom.ModelPart on net.minecraft.client.model.monster.piglin.PiglinModel, now only passed via constructor as an instance field; no static equivalent exists to reconstruct the old value");
    }

    /** was net/minecraft/client/model/monster/piglin/PiglinModel.method_47656(Lnet/minecraft/client/model/geom/builders/CubeDeformation;Lnet/minecraft/client/model/geom/builders/MeshDefinition;)V */
    public static void method_47656(net.minecraft.client.model.monster.piglin.PiglinModel self, net.minecraft.client.model.geom.builders.CubeDeformation a0, net.minecraft.client.model.geom.builders.MeshDefinition a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.monster.piglin.PiglinModel.method_47656(net.minecraft.client.model.geom.builders.CubeDeformation, net.minecraft.client.model.geom.builders.MeshDefinition) was removed in 1.21.2 with no direct replacement; new particle methods use incompatible parameter types (net.minecraft.client.renderer.entity.state.EntityRenderState/class_10034/class_10054)");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeBookComponent.method_2596(Lnet/minecraft/world/item/crafting/RecipeHolder;Ljava/util/List;)V */
    public static void method_2596(net.minecraft.client.gui.screens.recipebook.RecipeBookComponent self, net.minecraft.world.item.crafting.RecipeHolder a0, java.util.List a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.recipebook.RecipeBookComponent.method_2596(net.minecraft.world.item.crafting.RecipeHolder, List) was removed in 1.21.2; chat signing API reworked to fillGhostRecipe/fillGhostRecipe/recipeShown with incompatible types (net.minecraft.client.gui.screens.recipebook.GhostSlots/class_10295/class_10298/class_10352), no conversion from net.minecraft.world.item.crafting.RecipeHolder available");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeBookComponent.method_2600(Lnet/minecraft/world/inventory/Slot;)V */
    public static void method_2600(net.minecraft.client.gui.screens.recipebook.RecipeBookComponent self, net.minecraft.world.inventory.Slot a0) {
        self.slotClicked(a0);
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeBookComponent.method_60344(Lnet/minecraft/world/item/crafting/Ingredient;IIII)V */
    public static void method_60344(net.minecraft.client.gui.screens.recipebook.RecipeBookComponent self, net.minecraft.world.item.crafting.Ingredient a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_60344(Ingredient,int,int,int,int) deleted in 1.21.2; new net.minecraft.client.gui.screens.recipebook.RecipeBookComponent API has no Ingredient-accepting method — rendering restructured around net.minecraft.client.gui.GuiGraphics/class_1735");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeBookPage.method_2630(Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;)V */
    public static void method_2630(net.minecraft.client.gui.screens.recipebook.RecipeBookPage self, net.minecraft.client.gui.screens.recipebook.RecipeBookComponent a0) {
        self.setInvisible();
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeBookPage.method_2631()Lnet/minecraft/world/item/crafting/RecipeHolder; */
    public static net.minecraft.world.item.crafting.RecipeHolder method_2631(net.minecraft.client.gui.screens.recipebook.RecipeBookPage self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.recipebook.RecipeBookPage.method_2631() (returning net.minecraft.world.item.crafting.RecipeHolder) was removed in 1.21.2; no remaining accessor on net.minecraft.client.gui.screens.recipebook.RecipeBookPage yields net.minecraft.world.item.crafting.RecipeHolder -- getLastClickedRecipe() returns the unrelated net.minecraft.world.item.crafting.display.RecipeDisplayId, so the old contract has no faithful bridge");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeButton.method_2643()Lnet/minecraft/world/item/crafting/RecipeHolder; */
    public static net.minecraft.world.item.crafting.RecipeHolder method_2643(net.minecraft.client.gui.screens.recipebook.RecipeButton self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.recipebook.RecipeButton.method_2643() returned net.minecraft.world.item.crafting.RecipeHolder (RecipeResultCollection), removed in 1.21.2; the new API exposes net.minecraft.world.item.crafting.display.RecipeDisplayId via getCurrentRecipe() instead, with no way to obtain or construct a net.minecraft.world.item.crafting.RecipeHolder from net.minecraft.client.gui.screens.recipebook.RecipeButton");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeCollection.method_2647(Lnet/minecraft/stats/RecipeBook;)V */
    public static void method_2647(net.minecraft.client.gui.screens.recipebook.RecipeCollection self, net.minecraft.stats.RecipeBook a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.recipebook.RecipeCollection.method_2647(DyeColor) was removed in 1.21.2 with no direct replacement; sign color is now handled via net.minecraft.world.item.crafting.display.RecipeDisplayId (isCraftable), which has a different contract (returns boolean, not void) and no DyeColor conversion is available in the new API surface");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeCollection.method_2649(Lnet/minecraft/world/entity/player/StackedContents;IILnet/minecraft/stats/RecipeBook;)V */
    public static void method_2649(net.minecraft.client.gui.screens.recipebook.RecipeCollection self, net.minecraft.world.entity.player.StackedContents a0, int a1, int a2, net.minecraft.stats.RecipeBook a3) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.recipebook.RecipeCollection.method_2649 (MerchantScreen trade-slot render helper taking Merchant/int/int/ItemRenderer) was removed in 1.21.2 with no direct equivalent; the new net.minecraft.client.gui.screens.recipebook.RecipeCollection API surface exposes only query/filter methods (selectRecipes/64885/64886) and no ItemRenderer-based rendering path");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeCollection.method_2657()Z */
    public static boolean method_2657(net.minecraft.client.gui.screens.recipebook.RecipeCollection self) {
        return self.getRecipes().isEmpty();
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeCollection.method_48479()Lnet/minecraft/core/RegistryAccess; */
    public static net.minecraft.core.RegistryAccess method_48479(net.minecraft.client.gui.screens.recipebook.RecipeCollection self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.recipebook.RecipeCollection no longer stores a net.minecraft.core.RegistryAccess (RegistryWrapper.WrapperLookup); method_48479 has no equivalent in 1.21.2");
    }

    /** was net/minecraft/client/gui/screens/recipebook/FurnaceRecipeBookComponent.method_17065()Ljava/util/Set; */
    public static java.util.Set method_17065(net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent self) {
        throw new UnsupportedOperationException("CenturyBridge: method_17065 returned a Set and was deleted in 1.21.2 with no direct replacement in net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent");
    }

    /** was net/minecraft/client/gui/screens/recipebook/RecipeUpdateListener.method_2659()Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent; */
    public static net.minecraft.client.gui.screens.recipebook.RecipeBookComponent method_2659(net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener.method_2659() was deleted in 1.21.2 with no direct replacement; new net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener API (recipesUpdated, fillGhostRecipe) cannot produce net.minecraft.client.gui.screens.recipebook.RecipeBookComponent");
    }

    /** was net/minecraft/client/gui/screens/packs/TransferableSelectionList.method_20140(Lnet/minecraft/client/gui/screens/packs/TransferableSelectionList;I)I */
    public static int method_20140(net.minecraft.client.gui.screens.packs.TransferableSelectionList self, net.minecraft.client.gui.screens.packs.TransferableSelectionList a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.packs.TransferableSelectionList.method_20140(net.minecraft.client.gui.screens.packs.TransferableSelectionList, int) was removed in 1.21.2 with no direct equivalent in the new API surface");
    }

    /** was net/minecraft/client/model/animal/equine/AbstractEquineModel.method_17084(Lnet/minecraft/world/entity/animal/equine/AbstractHorse;FFF)V */
    public static void method_17084(net.minecraft.client.model.animal.equine.AbstractEquineModel self, net.minecraft.world.entity.animal.equine.AbstractHorse a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17084(ParticleEffect, float, float, float) was removed in 1.21.2; the replacement a(net.minecraft.client.renderer.entity.state.EntityRenderState) takes a different parameter type that cannot be constructed from the old ParticleEffect + position without its API surface");
    }


    /** was net/minecraft/client/model/object/boat/BoatModel.method_22952(Lnet/minecraft/world/entity/vehicle/boat/Boat;FFFFF)V */
    public static void method_22952(net.minecraft.client.model.object.boat.BoatModel self, net.minecraft.world.entity.vehicle.boat.Boat a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: ParticleManager.method_22952(Particle,5float) was removed in 1.21.2; the new API has no per-particle method accepting a Particle and float parameters");
    }

    /** was net/minecraft/client/model/object/book/BookModel.method_24184(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V */
    public static void method_24184(net.minecraft.client.model.object.book.BookModel self, com.mojang.blaze3d.vertex.PoseStack a0, com.mojang.blaze3d.vertex.VertexConsumer a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.object.book.BookModel.method_24184(MatrixStack, VertexConsumerProvider, int, int, int) was removed in 1.21.2; no remaining member accepts MatrixStack/VertexConsumerProvider, rendering must migrate to DrawContext");
    }

    /** was net/minecraft/client/model/geom/ModelLayerLocation.method_35743()Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_35743(net.minecraft.client.model.geom.ModelLayerLocation self) {
        return self.model();
    }

    /** was net/minecraft/client/model/geom/ModelLayerLocation.method_35744()Ljava/lang/String; */
    public static java.lang.String method_35744(net.minecraft.client.model.geom.ModelLayerLocation self) {
        return self.layer();
    }

    /** was net/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent.getHeight()I */
    public static int method_32661(net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent self) {
        throw new UnsupportedOperationException("CenturyBridge: getHeight now requires a TextRenderer (net.minecraft.client.gui.Font) argument not available in the old no-arg signature");
    }


    /** was net/minecraft/client/model/monster/ravager/RavagerModel.method_17089(Lnet/minecraft/world/entity/monster/Ravager;FFF)V */
    public static void method_17089(net.minecraft.client.model.monster.ravager.RavagerModel self, net.minecraft.world.entity.monster.Ravager a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17089 was removed in 1.21.2 model refactor; no method in the new API surface accepts (net.minecraft.world.entity.monster.Ravager, float, float, float)");
    }

    /** was net/minecraft/client/model/animal/golem/IronGolemModel.method_17095(Lnet/minecraft/world/entity/animal/golem/IronGolem;FFF)V */
    public static void method_17095(net.minecraft.client.model.animal.golem.IronGolemModel self, net.minecraft.world.entity.animal.golem.IronGolem a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17095(ItemStack,float,float,float) was removed in 1.21.2; the renderer now uses state objects (net.minecraft.client.renderer.entity.state.EntityRenderState/class_10037) that cannot be constructed from the old parameters without additional rendering context");
    }


    /** was net/minecraft/client/renderer/entity/GlowSquidRenderer.getTextureLocation(Lnet/minecraft/world/entity/animal/squid/GlowSquid;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_33431(net.minecraft.client.renderer.entity.GlowSquidRenderer self, net.minecraft.world.entity.animal.squid.GlowSquid a0) {
        throw new UnsupportedOperationException("CenturyBridge: getTextureLocation(net.minecraft.world.entity.animal.squid.GlowSquid)->net.minecraft.resources.Identifier was deleted in 1.21.2; new net.minecraft.client.renderer.entity.GlowSquidRenderer returns net.minecraft.resources.Identifier only from a(net.minecraft.client.renderer.entity.state.SquidRenderState) and b(net.minecraft.client.renderer.entity.state.LivingEntityRenderState), neither of which accepts net.minecraft.world.entity.animal.squid.GlowSquid");
    }

    /** was net/minecraft/client/model/EntityModel.method_2816(Lnet/minecraft/world/entity/Entity;FFF)V */
    public static void method_2816(net.minecraft.client.model.EntityModel self, net.minecraft.world.entity.Entity a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.EntityModel.method_2816(Entity,float,float,float) was deleted in 1.21.2; the new model API uses setupAnim(net.minecraft.client.renderer.entity.state.EntityRenderState) with a render-state object that cannot be constructed from the old (Entity,float,float,float) parameters");
    }

    /** was net/minecraft/client/model/animal/panda/PandaModel.method_17102(Lnet/minecraft/world/entity/animal/panda/Panda;FFF)V */
    public static void method_17102(net.minecraft.client.model.animal.panda.PandaModel self, net.minecraft.world.entity.animal.panda.Panda a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17102 (vertex with matrix transform) was removed in 1.21.2; VertexConsumer no longer accepts matrix-transformed float coordinates directly -- the new API uses net.minecraft.client.renderer.entity.state.EntityRenderState/class_10042/class_10050 types whose semantics are incompatible with the old float-coordinate submission pattern");
    }

    /** was net/minecraft/client/model/player/PlayerModel.method_2823(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V */
    public static void method_2823(net.minecraft.client.model.player.PlayerModel self, com.mojang.blaze3d.vertex.PoseStack a0, com.mojang.blaze3d.vertex.VertexConsumer a1, int a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.player.PlayerModel.method_2823(MatrixStack, VertexConsumerProvider, int, int) was removed; 1.21.2 splash rendering requires new context types (net.minecraft.client.renderer.entity.state.HumanoidRenderState/class_10055/class_1306) not derivable from the old parameters");
        // CB-CONFIDENCE: high -- old signature explicitly deleted with no direct replacement; new API requires context objects unavailable from old params
    }

    /** was net/minecraft/client/model/player/PlayerModel.method_2824(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V */
    public static void method_2824(net.minecraft.client.model.player.PlayerModel self, com.mojang.blaze3d.vertex.PoseStack a0, com.mojang.blaze3d.vertex.VertexConsumer a1, int a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.player.PlayerModel.method_2824(MatrixStack, VertexConsumerProvider, int, int) was removed in 1.21.2; the rendering API now uses DrawContext instead of separate MatrixStack/VertexConsumerProvider, and no equivalent method exists in the new API surface");
    }

    /** was com/mojang/blaze3d/opengl/GlProgram.field_29482Lcom/mojang/blaze3d/opengl/Uniform; */
    public static com.mojang.blaze3d.opengl.Uniform field_29482() {
        return null;
    }

    /** was com/mojang/blaze3d/opengl/GlProgram.method_35787()Ljava/lang/String; */
    public static java.lang.String method_35787(com.mojang.blaze3d.opengl.GlProgram self) {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.blaze3d.opengl.GlProgram.method_35787() returned the shader program name, which is no longer stored or accessible on the new com.mojang.blaze3d.opengl.GlProgram");
    }

    /** was net/minecraft/client/model/animal/rabbit/RabbitModel.method_17115(Lnet/minecraft/world/entity/animal/rabbit/Rabbit;FFF)V */
    public static void method_17115(net.minecraft.client.model.animal.rabbit.RabbitModel self, net.minecraft.world.entity.animal.rabbit.Rabbit a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_17115(net.minecraft.world.entity.animal.rabbit.Rabbit, float, float, float) was removed in 1.21.2; the rendering pipeline was restructured (setupAnim now takes net.minecraft.client.renderer.entity.state.RabbitRenderState) and no direct equivalent exists");
    }

    /** was net/minecraft/client/model/animal/sheep/SheepFurModel.method_17118(Lnet/minecraft/world/entity/animal/sheep/Sheep;FFF)V */
    public static void method_17118(net.minecraft.client.model.animal.sheep.SheepFurModel self, net.minecraft.world.entity.animal.sheep.Sheep a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.animal.sheep.SheepFurModel.method_17118(net.minecraft.world.entity.animal.sheep.Sheep,float,float,float) was removed in 1.21.2 with no direct replacement; the surviving methods (a(net.minecraft.client.renderer.entity.state.EntityRenderState), a(net.minecraft.client.renderer.entity.state.LivingEntityRenderState), setupAnim(net.minecraft.client.renderer.entity.state.SheepRenderState)) require new context types that cannot be constructed from the old arguments");
    }

    /** was net/minecraft/client/model/animal/sheep/SheepModel.method_17120(Lnet/minecraft/world/entity/animal/sheep/Sheep;FFF)V */
    public static void method_17120(net.minecraft.client.model.animal.sheep.SheepModel self, net.minecraft.world.entity.animal.sheep.Sheep a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.animal.sheep.SheepModel.method_17120(net.minecraft.world.entity.animal.sheep.Sheep,float,float,float) was deleted in 1.21.2; camera update was restructured around new context types (net.minecraft.client.renderer.entity.state.EntityRenderState/class_10042/class_10063) and none accept the old GameRenderer+3-float contract");
        // CB-CONFIDENCE: high -- the new API surface contains no method accepting net.minecraft.world.entity.animal.sheep.Sheep or three floats; the old contract is genuinely inexpressible
    }

    /** was net/minecraft/client/model/monster/shulker/ShulkerModel.method_2829()Lnet/minecraft/client/model/geom/ModelPart; */
    public static net.minecraft.client.model.geom.ModelPart method_2829(net.minecraft.client.model.monster.shulker.ShulkerModel self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.monster.shulker.ShulkerModel.method_2829() returned net.minecraft.client.model.geom.ModelPart (the reference passed to its constructor) but 1.21.2 removed this getter; no method on net.minecraft.client.model.monster.shulker.ShulkerModel returns net.minecraft.client.model.geom.ModelPart anymore");
    }

    /** was net/minecraft/client/multiplayer/ClientLevel.method_8435(J)V */
    public static void method_8435(net.minecraft.client.multiplayer.ClientLevel self, long a0) {
        throw new UnsupportedOperationException("CenturyBridge: ClientWorld.tickEntities(long) was removed in 1.21.2; entity ticking restructured with no method accepting a time parameter");
    }

    /** was net/minecraft/client/renderer/entity/TadpoleRenderer.method_41966(Lnet/minecraft/world/entity/animal/frog/Tadpole;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_41966(net.minecraft.client.renderer.entity.TadpoleRenderer self, net.minecraft.world.entity.animal.frog.Tadpole a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.TadpoleRenderer.method_41966 was removed; replacement b() requires net.minecraft.client.renderer.entity.state.LivingEntityRenderState but old arg is net.minecraft.world.entity.animal.frog.Tadpole with no available conversion");
        // CB-CONFIDENCE: high -- the old parameter type net.minecraft.world.entity.animal.frog.Tadpole and the new b() parameter type net.minecraft.client.renderer.entity.state.LivingEntityRenderState are distinct intermediary classes with no conversion surface provided, so no faithful bridge can be written
    }

    /** was net/minecraft/client/renderer/GameRenderer.field_29403Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram field_29403() {
        return null;
    }

    /** was net/minecraft/client/renderer/GameRenderer.field_4015Lnet/minecraft/client/Minecraft; */
    public static net.minecraft.client.Minecraft field_4015() {
        return net.minecraft.client.Minecraft.getInstance();
    }

    /** was net/minecraft/client/renderer/GameRenderer.field_4024Lnet/minecraft/client/renderer/PostChain; */
    public static net.minecraft.client.renderer.PostChain field_4024() {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.camera (field_4024) was removed in 1.21.2 with no direct replacement; new API surface exposes no accessor returning net.minecraft.client.renderer.PostChain");
        // CB-CONFIDENCY: high -- prompt explicitly states deletion with no direct replacement, and no method in the new net.minecraft.client.renderer.GameRenderer surface returns net.minecraft.client.renderer.PostChain
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_3183()Lnet/minecraft/client/renderer/PostChain; */
    public static net.minecraft.client.renderer.PostChain method_3183(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() was removed in 1.21.2; camera is no longer stored on or accessible from GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_3194()Lnet/minecraft/client/renderer/MapRenderer; */
    public static net.minecraft.client.renderer.MapRenderer method_3194(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() (method_3194) was removed in 1.21.2; Camera is no longer stored in GameRenderer and no equivalent accessor exists on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34496()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34496(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34496() returned PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) for direct manipulation; 1.21.2 reworked post-processing to net.minecraft.client.DeltaTracker-based pipelines and no longer exposes the processor instance");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34497()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34497(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() was removed in 1.21.2; camera is no longer stored on or accessible from GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34498()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34498(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.GameRenderer.method_34498() returned the loaded ShaderEffect (com.mojang.blaze3d.opengl.GlProgram); in 1.21.2 the post-effect pipeline was reworked and GameRenderer no longer holds a com.mojang.blaze3d.opengl.GlProgram instance (only an identifier via currentPostEffect)");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34499()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34499(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34499() returned the managed PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); in 1.21.2 post-effect handling was moved out of net.minecraft.client.renderer.GameRenderer and no equivalent accessor exists on the new GameRenderer surface");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34501()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34501(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34501() returning PostEffectProcessor was removed in 1.21.2; post-effect processing was reworked with no equivalent on GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34502()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34502(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34502() returned the PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); in 1.21.2 the post-effect pipeline was reworked and net.minecraft.client.renderer.GameRenderer no longer holds/exposes a com.mojang.blaze3d.opengl.GlProgram instance (only currentPostEffect() exposes the post-effect Identifier), so the old contract cannot be faithfully bridged");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34503()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34503(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34503() returned TickDeltaManager (com.mojang.blaze3d.opengl.GlProgram) which was removed in 1.21.2; the render tick counter (net.minecraft.client.DeltaTracker) is now passed as a parameter to render methods and is no longer stored in or accessible from GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34504()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34504(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34504() (getPostProcessor) was removed in 1.21.2 with no direct replacement; post-processing is no longer exposed on GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34505()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34505(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34505() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) with no direct equivalent in 1.21.2; the post-effect pipeline was reworked around net.minecraft.client.DeltaTracker and no accessor on net.minecraft.client.renderer.GameRenderer yields com.mojang.blaze3d.opengl.GlProgram anymore");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34506()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34506(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34506() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) which no longer exists in 1.21.2; the post-processing pipeline was reworked");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34507()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34507(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34507() returned the post-processing ShaderEffect (com.mojang.blaze3d.opengl.GlProgram), which was removed from GameRenderer in 1.21.2; the post-processing pipeline is now managed outside net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34508()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34508(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34508() returned the post-processing PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram), which is no longer held by GameRenderer in 1.21.2; the post-effect pipeline was reworked and no equivalent instance is exposed");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34509()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34509(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34509() (getPostProcessor returning PostEffectProcessor) has no equivalent in 1.21.2; post-processing was reworked and com.mojang.blaze3d.opengl.GlProgram is no longer held by net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34510()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34510(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.GameRenderer.method_34510() returned the PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); in 1.21.2 GameRenderer only exposes a setter preloadUiShader(net.minecraft.server.packs.resources.ResourceProvider) for the renamed processor type and no getter, so the instance cannot be retrieved.");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34511()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34511(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34511() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) for the blur effect, which is no longer exposed on GameRenderer in 1.21.2; blur is now controlled via boolean flags (isPanoramicMode/method_35768-35770) and the processor is managed internally");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34512()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34512(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); the post-processing pipeline was restructured in 1.21.2 and the processor is not held by net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34513()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34513(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34513() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); in 1.21.2 the post-effect pipeline was restructured out of net.minecraft.client.renderer.GameRenderer and no accessor on the new GameRenderer surface yields a com.mojang.blaze3d.opengl.GlProgram instance");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34514()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34514(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34514() (getPostProcessor returning PostEffectProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and GameRenderer no longer holds a com.mojang.blaze3d.opengl.GlProgram instance");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34515()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34515(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34515() returning com.mojang.blaze3d.opengl.GlProgram was removed in 1.21.2; no method on net.minecraft.client.renderer.GameRenderer returns com.mojang.blaze3d.opengl.GlProgram in the new API");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34516()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34516(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.GameRenderer.method_34516 (returns com.mojang.blaze3d.opengl.GlProgram PostEffectProcessor) is deleted in 1.21.2; post-effect pipeline reworked around net.minecraft.client.DeltaTracker with no accessor returning the old type");
        // CB-CONFIDENCE: high -- the new net.minecraft.client.renderer.GameRenderer API surface contains no method returning com.mojang.blaze3d.opengl.GlProgram, and the post-effect system was reworked to net.minecraft.client.DeltaTracker (renderLevel/render), so the old contract is genuinely inexpressible.
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34518()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34518(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34518 (getPostProcessor returning com.mojang.blaze3d.opengl.GlProgram) has no equivalent in 1.21.2; post-processing was reworked to net.minecraft.client.DeltaTracker with only setters (renderLevel/render) and no getter exposed");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34519()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34519(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34519 (getPostProcessor) removed in 1.21.2; post-processing reworked to net.minecraft.client.DeltaTracker, no getter for com.mojang.blaze3d.opengl.GlProgram remains on GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34520()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34520(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getPostProcessor() (method_34520) removed in 1.21.2; post-effect system reworked, net.minecraft.client.renderer.GameRenderer no longer exposes com.mojang.blaze3d.opengl.GlProgram");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34523()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34523(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34523() (getPostProcessor returning PostEffectProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and net.minecraft.client.renderer.GameRenderer no longer exposes a com.mojang.blaze3d.opengl.GlProgram instance");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34524()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34524(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.GameRenderer.method_34524() returned the active PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram), but 1.21.2 reworked post-effects to use net.minecraft.client.DeltaTracker and no longer exposes a com.mojang.blaze3d.opengl.GlProgram instance from GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34525()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34525(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a ShaderEffect (com.mojang.blaze3d.opengl.GlProgram); post-processing pipeline reworked in 1.21.2 with no equivalent accessor");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34526()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34526(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34526() returned PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram), removed in 1.21.2 post-processing rework; no equivalent accessor on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34527()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34527(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer#method_34527 (getPostProcessor) removed in 1.21.2; post-effect processor no longer exposed by GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34529()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34529(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34529 returned a com.mojang.blaze3d.opengl.GlProgram PostEffectProcessor (blur post-effect) but 1.21.2 refactored the post-effect pipeline to net.minecraft.client.DeltaTracker; GameRenderer no longer exposes a com.mojang.blaze3d.opengl.GlProgram instance");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34530()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34530(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34530 returned GameRenderer's PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram), which is no longer exposed in 1.21.2; the post-effect pipeline was reworked to a frame-graph-based system with no direct accessor for the processor");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34531()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34531(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer holds or exposes a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); post-processing was reworked in 1.21.2 and the processor is managed through a different mechanism (see currentPostEffect/renderLevel/render)");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34532()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34532(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34532() was removed in 1.21.2; post-effect processing was refactored and GameRenderer no longer holds or exposes a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram)");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34533()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34533(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34533 returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) which is no longer exposed by GameRenderer in 1.21.2; the post-processing pipeline was reworked to use net.minecraft.client.DeltaTracker (see renderLevel/render)");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34534()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34534(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer holds or exposes a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); in 1.21.2 post-effect processors are passed externally as net.minecraft.client.DeltaTracker to renderLevel/render, so the getter has no faithful equivalent");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34535()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34535(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34535 (getPostProcessor returning com.mojang.blaze3d.opengl.GlProgram) was removed in 1.21.2; GameRenderer no longer holds a com.mojang.blaze3d.opengl.GlProgram ShaderEffect reference -- post-processing was restructured");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34536()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34536(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34536 (blur PostEffectProcessor getter) was removed in 1.21.2; post-processing is now driven by net.minecraft.client.DeltaTracker via renderLevel/render and is no longer exposed as a retrievable instance on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34538(Lnet/minecraft/server/packs/resources/ResourceProvider;)V */
    public static void method_34538(net.minecraft.client.renderer.GameRenderer self, net.minecraft.server.packs.resources.ResourceProvider a0) {
        self.preloadUiShader(a0);
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34539()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34539(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.GameRenderer.method_34539() returned com.mojang.blaze3d.opengl.GlProgram (PostEffectProcessor), but 1.21.2 removed it from GameRenderer; post-processing was refactored to use net.minecraft.client.DeltaTracker via renderLevel/render with no accessor for the old type");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34540()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34540(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34540 returned PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) which is no longer exposed by GameRenderer in 1.21.2; post-processing pipeline was refactored");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34542()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34542(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34542 (getPostProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and net.minecraft.client.renderer.GameRenderer no longer holds a com.mojang.blaze3d.opengl.GlProgram instance");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34543()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34543(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34543() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) that GameRenderer no longer holds in 1.21.2; the post-effect pipeline was restructured (see net.minecraft.client.DeltaTracker and renderLevel/render)");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34546()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34546(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34546() returned PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram), which has no equivalent in 1.21.2 GameRenderer; the post-effect pipeline was restructured with no direct replacement");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34547()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34547(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34547() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) which is no longer exposed by GameRenderer in 1.21.2; the post-processing pipeline was reworked and no equivalent accessor exists on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_34548()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34548(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.GameRenderer.method_34548() returned a com.mojang.blaze3d.opengl.GlProgram PostEffectProcessor; in 1.21.2 the post-effect pipeline was reworked and GameRenderer no longer exposes individual PostEffectProcessor instances");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_35767(Ljava/lang/String;)Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_35767(net.minecraft.client.renderer.GameRenderer self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_35767(String)PostEffectProcessor was removed in 1.21.2; post-effect processor loading was restructured out of net.minecraft.client.renderer.GameRenderer with no replacement method on this class");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_36432()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_36432(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer stores a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); post-effects are now identified by Identifier (net.minecraft.resources.Identifier) via currentPostEffect, and the processor is managed externally");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_36433()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_36433(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_36433() (getPostProcessor -> PostEffectProcessor) was removed in 1.21.2; the post-processing pipeline is no longer exposed as a com.mojang.blaze3d.opengl.GlProgram instance on GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_42595()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_42595(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_42595 (PostEffectProcessor accessor) was removed in 1.21.2; post-processing pipeline reworked with no equivalent accessor on net.minecraft.client.renderer.GameRenderer");
        // CB-CONFIDENCE: high -- the new API surface has no method returning com.mojang.blaze3d.opengl.GlProgram and the member is explicitly marked deleted with no direct replacement
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_45774()Lnet/minecraft/server/packs/resources/PreparableReloadListener; */
    public static net.minecraft.server.packs.resources.PreparableReloadListener method_45774(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_45774() was deleted in 1.21.2 with no direct replacement; the Camera type it returned (net.minecraft.server.packs.resources.PreparableReloadListener) is no longer exposed by GameRenderer, whose getCamera (getMainCamera) now returns net.minecraft.client.Camera");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_49037()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_49037(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_49037() returned its PostEffectProcessor, which no longer exists on GameRenderer in 1.21.2; post-processing moved to the new render pipeline");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_49038()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_49038(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_49038() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) that GameRenderer no longer holds in 1.21.2; the post-effect pipeline was reworked and no equivalent accessor exists on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_51771()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_51771(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_51771 (returning com.mojang.blaze3d.opengl.GlProgram) was removed from GameRenderer in 1.21.2; the post-effect/frustum object it returned is no longer accessible from GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_51772()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_51772(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_51772() returned a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) that is no longer held by GameRenderer in 1.21.2; post-effect processing was restructured out of this class");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_51773()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_51773(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram) in 1.21.2; the post-processing pipeline was moved out of GameRenderer and no field or method of that type remains on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_51774()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_51774(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_51774() returned the PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); 1.21.2 reworked post-processing to a pipeline and GameRenderer no longer exposes a PostEffectProcessor instance");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_55249()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_55249(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_55249 (getPostProcessor returning PostEffectProcessor/class_5944) was removed in 1.21.2; the post-effect system was reworked around net.minecraft.client.DeltaTracker and no com.mojang.blaze3d.opengl.GlProgram accessor remains on GameRenderer");
    }

    /** was net/minecraft/client/renderer/GameRenderer.method_56846()Lcom/mojang/blaze3d/opengl/GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_56846(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (com.mojang.blaze3d.opengl.GlProgram); the post-effect pipeline was reworked in 1.21.2 and the getter has no equivalent on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/client/renderer/fog/FogRenderer.method_23792()V */
    public static void method_23792(net.minecraft.client.renderer.fog.FogRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_23792 was removed in 1.21.2 with no direct replacement in net.minecraft.client.renderer.fog.FogRenderer");
    }

    /** was net/minecraft/client/model/object/boat/RaftModel.method_45710(Lnet/minecraft/client/model/geom/ModelPart;)Lcom/google/common/collect/ImmutableList$Builder; */
    public static com.google.common.collect.ImmutableList.Builder method_45710(net.minecraft.client.model.object.boat.RaftModel self, net.minecraft.client.model.geom.ModelPart a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.object.boat.RaftModel.method_45710(Text) returned ImmutableList.Builder for multi-segment text processing; 1.21.2 restructured to return single OrderedText via createRaftModel/createChestRaftModel with no builder or list equivalent");
    }

    /** was net/minecraft/client/model/object/boat/RaftModel.method_45711(Lnet/minecraft/world/entity/vehicle/boat/Boat;FFFFF)V */
    public static void method_45711(net.minecraft.client.model.object.boat.RaftModel self, net.minecraft.world.entity.vehicle.boat.Boat a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.object.boat.RaftModel.method_45711(VertexConsumer, float, float, float, float, float) was removed in 1.21.2 with no direct replacement; the new API only exposes createRaftModel()/createChestRaftModel() returning net.minecraft.client.model.geom.builders.LayerDefinition, and no equivalent vertex-drawing method exists on net.minecraft.client.model.object.boat.RaftModel");
    }

    /** was net/minecraft/client/model/object/boat/RaftModel.addCommonParts(Lnet/minecraft/client/model/geom/builders/PartDefinition;)V */
    public static void method_45713(net.minecraft.client.model.object.boat.RaftModel self, net.minecraft.client.model.geom.builders.PartDefinition a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.object.boat.RaftModel.addCommonParts(net.minecraft.client.model.geom.builders.PartDefinition) was removed in 1.21.2 with no direct replacement; the new net.minecraft.client.model.object.boat.RaftModel API only exposes getters (createRaftModel/createChestRaftModel returning net.minecraft.client.model.geom.builders.LayerDefinition) and no mutation path accepting a net.minecraft.client.model.geom.builders.PartDefinition");
    }

    /** was net/minecraft/client/model/object/boat/RaftModel.method_45715()Lcom/google/common/collect/ImmutableList; */
    public static com.google.common.collect.ImmutableList method_45715(net.minecraft.client.model.object.boat.RaftModel self) {
        return com.google.common.collect.ImmutableList.of(self.createRaftModel(), self.createChestRaftModel());
    }

    /** was net/minecraft/class_7761$class_7762.field_40525Lnet/minecraft/client/model/geom/ModelPart; */
    public static net.minecraft.client.model.geom.ModelPart field_40525() {
        throw new UnsupportedOperationException("CenturyBridge: field_40525 was a static net.minecraft.client.model.geom.ModelPart field deleted in 1.21.2; new API only has instance fields field_40526/40527/40528 with no static equivalent");
        // CB-CONFIDENCE: high -- the old field was static and deleted; the new API exposes only instance fields with no static constant or factory to reconstruct the old value
    }

    /** was net/minecraft/client/renderer/entity/DisplayRenderer.method_49050(Lnet/minecraft/world/entity/Display;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_49050(net.minecraft.client.renderer.entity.DisplayRenderer self, net.minecraft.world.entity.Display a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_49050 was deleted in 1.21.2; no new API on net.minecraft.client.renderer.entity.DisplayRenderer returns an Identifier from net.minecraft.world.entity.Display");
    }

    /** was net/minecraft/client/renderer/blockentity/ChestRenderer.method_32148()Lnet/minecraft/client/model/geom/builders/LayerDefinition; */
    public static net.minecraft.client.model.geom.builders.LayerDefinition method_32148(net.minecraft.client.renderer.blockentity.ChestRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.blockentity.ChestRenderer.method_32148() has no equivalent in 1.21.2; the new net.minecraft.client.renderer.blockentity.ChestRenderer exposes only a constructor and a void render method, with no accessor returning net.minecraft.client.model.geom.builders.LayerDefinition");
    }

    /** was net/minecraft/client/renderer/blockentity/ChestRenderer.method_32149()Lnet/minecraft/client/model/geom/builders/LayerDefinition; */
    public static net.minecraft.client.model.geom.builders.LayerDefinition method_32149(net.minecraft.client.renderer.blockentity.ChestRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.blockentity.ChestRenderer.method_32149() returned a net.minecraft.client.model.geom.builders.LayerDefinition (PostEffectProcessor) but the 1.21.2 net.minecraft.client.renderer.blockentity.ChestRenderer surface exposes no field or method yielding one; post-effect handling was reworked out of this class");
        // CB-CONFIDENCE: high -- the new API surface lists no member returning net.minecraft.client.model.geom.builders.LayerDefinition, so no faithful bridge exists
    }

    /** was net/minecraft/client/renderer/entity/ArrowRenderer.method_23153(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIFFIIII)V */
    public static void method_23153(net.minecraft.client.renderer.entity.ArrowRenderer self, com.mojang.blaze3d.vertex.PoseStack.Pose a0, com.mojang.blaze3d.vertex.VertexConsumer a1, int a2, int a3, int a4, float a5, float a6, int a7, int a8, int a9, int a10) {
        throw new UnsupportedOperationException("CenturyBridge: SignBlockEntityRenderer.method_23153 rendered sign text from individual style parameters (MatrixStack.Entry, text colors, glow flags, scale); 1.21.2 restructured sign text rendering to require a SignText (net.minecraft.client.renderer.entity.state.EntityRenderState) object and a full MatrixStack (com.mojang.blaze3d.vertex.PoseStack), neither constructable from the old method's primitive parameters");
    }

    /** was net/minecraft/client/renderer/entity/BlazeRenderer.method_3881(Lnet/minecraft/world/entity/monster/Blaze;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3881(net.minecraft.client.renderer.entity.BlazeRenderer self, net.minecraft.world.entity.monster.Blaze a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3881(Identifier)->Packet has no 1.21.2 equivalent; new b(net.minecraft.client.renderer.entity.state.LivingEntityRenderState)->Packet requires a RecipeCollection (net.minecraft.client.renderer.entity.state.LivingEntityRenderState) not derivable from an Identifier via net.minecraft.client.renderer.entity.BlazeRenderer's API");
    }

    /** was net/minecraft/client/renderer/entity/CaveSpiderRenderer.getTextureLocation(Lnet/minecraft/world/entity/monster/spider/CaveSpider;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3885(net.minecraft.client.renderer.entity.CaveSpiderRenderer self, net.minecraft.world.entity.monster.spider.CaveSpider a0) {
        throw new UnsupportedOperationException("CenturyBridge: getTextureLocation was deleted with no direct replacement; method b takes net.minecraft.client.renderer.entity.state.LivingEntityRenderState not net.minecraft.world.entity.monster.spider.CaveSpider, no conversion available");
    }

    /** was net/minecraft/client/renderer/entity/CaveSpiderRenderer.method_3886(Lnet/minecraft/world/entity/monster/spider/CaveSpider;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_3886(net.minecraft.client.renderer.entity.CaveSpiderRenderer self, net.minecraft.world.entity.monster.spider.CaveSpider a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.CaveSpiderRenderer.method_3886 was deleted in 1.21.2 with no direct replacement; the remaining API surface (constructor + identifier lookup) cannot reproduce the former render-path behavior");
    }

    /** was net/minecraft/client/renderer/entity/CodRenderer.method_3896(Lnet/minecraft/world/entity/animal/fish/Cod;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V */
    public static void method_3896(net.minecraft.client.renderer.entity.CodRenderer self, net.minecraft.world.entity.animal.fish.Cod a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_3896 was removed in 1.21.2; the new render API (method a) requires a pre-populated RenderState (net.minecraft.client.renderer.entity.state.LivingEntityRenderState) which cannot be constructed from the old (net.minecraft.world.entity.animal.fish.Cod, 4 floats) parameters");
        // CB-CONFIDENCE: medium -- method explicitly deleted; new API uses RenderState-based paradigm incompatible with old direct-data parameters
    }

    /** was net/minecraft/client/renderer/entity/CodRenderer.method_3897(Lnet/minecraft/world/entity/animal/fish/Cod;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3897(net.minecraft.client.renderer.entity.CodRenderer self, net.minecraft.world.entity.animal.fish.Cod a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.CodRenderer.method_3897(net.minecraft.world.entity.animal.fish.Cod) was deleted in 1.21.2; the structurally similar b(net.minecraft.client.renderer.entity.state.LivingEntityRenderState) takes a different parameter type with no known conversion from net.minecraft.world.entity.animal.fish.Cod");
    }

    /** was net/minecraft/client/renderer/entity/DragonFireballRenderer.method_3905(Lnet/minecraft/world/entity/projectile/hurtingprojectile/DragonFireball;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3905(net.minecraft.client.renderer.entity.DragonFireballRenderer self, net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3905 returned net.minecraft.resources.Identifier (Identifier) from net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball (Camera), but 1.21.2 net.minecraft.client.renderer.entity.DragonFireballRenderer has no Identifier-returning equivalent; the API was restructured around net.minecraft.client.renderer.entity.state.EntityRenderState and int-returning methods");
    }


    /** was net/minecraft/client/renderer/entity/EndCrystalRenderer.method_3909(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3909(net.minecraft.client.renderer.entity.EndCrystalRenderer self, net.minecraft.world.entity.boss.enderdragon.EndCrystal a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3909 returned a per-particle texture Identifier (net.minecraft.resources.Identifier); 1.21.2 particles use sprite-atlas descriptors (net.minecraft.client.renderer.entity.state.EndCrystalRenderState via createRenderState / net.minecraft.client.renderer.entity.state.EntityRenderState via d) with no equivalent single texture Identifier");
    }

    /** was net/minecraft/client/renderer/entity/ElderGuardianRenderer.method_3910(Lnet/minecraft/world/entity/monster/Guardian;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_3910(net.minecraft.client.renderer.entity.ElderGuardianRenderer self, net.minecraft.world.entity.monster.Guardian a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_3910 rendered an ItemStack (net.minecraft.world.entity.monster.Guardian) via MatrixStack (com.mojang.blaze3d.vertex.PoseStack); net.minecraft.client.renderer.entity.ElderGuardianRenderer in 1.21.2 only exposes Identifier-returning methods (a/b) and no longer handles ItemStack rendering");
    }

    /** was net/minecraft/client/renderer/entity/EnderDragonRenderer.method_32165()Lnet/minecraft/client/model/geom/builders/LayerDefinition; */
    public static net.minecraft.client.model.geom.builders.LayerDefinition method_32165(net.minecraft.client.renderer.entity.EnderDragonRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.EnderDragonRenderer.method_32165() returned a net.minecraft.client.model.geom.builders.LayerDefinition (PostEffectProcessor) that net.minecraft.client.renderer.entity.EnderDragonRenderer no longer exposes in 1.21.2; the post-effect pipeline was reworked and no equivalent accessor exists");
    }

    /** was net/minecraft/client/model/object/projectile/WindChargeModel.method_55239(Lnet/minecraft/world/entity/projectile/hurtingprojectile/windcharge/AbstractWindCharge;FFFFF)V */
    public static void method_55239(net.minecraft.client.model.object.projectile.WindChargeModel self, net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.object.projectile.WindChargeModel.method_55239(net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge,float,float,float,float,float) was removed in 1.21.2 with no direct replacement; new a(net.minecraft.client.renderer.entity.state.EntityRenderState) cannot be bridged without net.minecraft.client.renderer.entity.state.EntityRenderState's constructor API to wrap the old (Random + 5 floats) arguments");
    }

    /** was net/minecraft/client/renderer/entity/EntityRenderer.method_3931(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3931(net.minecraft.client.renderer.entity.EntityRenderer self, net.minecraft.world.entity.Entity a0) {
        throw new UnsupportedOperationException("CenturyBridge: EntityRenderer.getTexture(Entity) was removed in 1.21.2; textures are now resolved through the render state system, not via EntityRenderer");
        // CB-CONFIDENCE: high -- the new net.minecraft.client.renderer.entity.EntityRenderer API surface has no method returning net.minecraft.resources.Identifier (Identifier); getTexture was genuinely removed in the 1.21.2 entity renderer refactor
    }

    /** was net/minecraft/client/renderer/entity/WindChargeRenderer.createRenderState(Lnet/minecraft/world/entity/projectile/hurtingprojectile/windcharge/AbstractWindCharge;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_55269(net.minecraft.client.renderer.entity.WindChargeRenderer self, net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge a0) {
        throw new UnsupportedOperationException("CenturyBridge: createRenderState was removed in 1.21.2 with no direct replacement; the new net.minecraft.client.renderer.entity.WindChargeRenderer API exposes no path from net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.AbstractWindCharge to net.minecraft.resources.Identifier");
    }

    /** was net/minecraft/client/renderer/entity/EvokerFangsRenderer.method_3963(Lnet/minecraft/world/entity/projectile/EvokerFangs;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3963(net.minecraft.client.renderer.entity.EvokerFangsRenderer self, net.minecraft.world.entity.projectile.EvokerFangs a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3963 returned an Identifier (model/texture id) for an ItemStack; 1.21.2 replaced identifier-based lookups with state-based rendering via extractRenderState(net.minecraft.world.entity.projectile.EvokerFangs, net.minecraft.client.renderer.entity.state.EvokerFangsRenderState, float) and no longer exposes the Identifier");
    }

    /** was net/minecraft/client/renderer/entity/FallingBlockRenderer.method_3964(Lnet/minecraft/world/entity/item/FallingBlockEntity;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3964(net.minecraft.client.renderer.entity.FallingBlockRenderer self, net.minecraft.world.entity.item.FallingBlockEntity a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.FallingBlockRenderer.method_3964(Vec3d)->Identifier was removed in 1.21.2 with no direct replacement; no method on the new net.minecraft.client.renderer.entity.FallingBlockRenderer returns an Identifier");
    }

    /** was net/minecraft/client/renderer/entity/ExperienceOrbRenderer.method_3967(Lnet/minecraft/world/entity/ExperienceOrb;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3967(net.minecraft.client.renderer.entity.ExperienceOrbRenderer self, net.minecraft.world.entity.ExperienceOrb a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3967(Camera) returned net.minecraft.resources.Identifier (Matrix4f) computed from the camera alone; 1.21.2 replaced this with extractRenderState(Camera, net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState, float) which writes into a net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState holder and requires a tick-delta parameter, and net.minecraft.resources.Identifier itself was removed in the JOML migration");
    }

    /** was net/minecraft/client/renderer/entity/FireworkEntityRenderer.method_3969(Lnet/minecraft/world/entity/projectile/FireworkRocketEntity;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3969(net.minecraft.client.renderer.entity.FireworkEntityRenderer self, net.minecraft.world.entity.projectile.FireworkRocketEntity a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.FireworkEntityRenderer.method_3969(ItemStack)Identifier was removed in 1.21.2; ItemRenderer no longer resolves an ItemStack to a texture Identifier (texture/sprite resolution moved to the new model system, and no method on net.minecraft.client.renderer.entity.FireworkEntityRenderer returns net.minecraft.resources.Identifier)");
    }

    /** was net/minecraft/client/renderer/entity/GhastRenderer.method_3973(Lnet/minecraft/world/entity/monster/Ghast;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_3973(net.minecraft.client.renderer.entity.GhastRenderer self, net.minecraft.world.entity.monster.Ghast a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        self.extractRenderState(a0, self.createRenderState(), a2);
    }

    /** was net/minecraft/client/renderer/entity/FishingHookRenderer.method_3975(Lnet/minecraft/world/entity/projectile/FishingHook;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_3975(net.minecraft.client.renderer.entity.FishingHookRenderer self, net.minecraft.world.entity.projectile.FishingHook a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3975 returned an Identifier derived from the Camera; 1.21.2 replaced it with boolean predicates (affectedByCulling/shouldRender) and direct rendering (extractRenderState), so the Identifier is no longer exposed");
    }

    /** was net/minecraft/client/renderer/entity/GiantMobRenderer.method_3980(Lnet/minecraft/world/entity/monster/Giant;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_3980(net.minecraft.client.renderer.entity.GiantMobRenderer self, net.minecraft.world.entity.monster.Giant a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        self.extractRenderState(a0, self.createRenderState(), a2);
    }

    /** was net/minecraft/client/renderer/entity/HuskRenderer.method_3985(Lnet/minecraft/world/entity/monster/zombie/Zombie;Lcom/mojang/blaze3d/vertex/PoseStack;F)V */
    public static void method_3985(net.minecraft.client.renderer.entity.HuskRenderer self, net.minecraft.world.entity.monster.zombie.Zombie a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.entity.HuskRenderer.method_3985(ItemStack, MatrixStack, float) was removed in 1.21.2; net.minecraft.client.renderer.entity.HuskRenderer no longer exposes rendering methods, only identifier accessors a() and b()");
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

    /** was net/minecraft/client/model/AnimationUtils.animateCrossbowCharge(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/world/entity/LivingEntity;Z)V */
    public static void method_25446(net.minecraft.client.model.geom.ModelPart a0, net.minecraft.client.model.geom.ModelPart a1, net.minecraft.world.entity.LivingEntity a2, boolean a3) {
        throw new UnsupportedOperationException("CenturyBridge: cannot derive float and int from LivingEntity without knowing the new API semantics");
    }

    /** was net/minecraft/client/model/AnimationUtils.swingWeaponDown(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/world/entity/Mob;FF)V */
    public static void method_29351(net.minecraft.client.model.geom.ModelPart a0, net.minecraft.client.model.geom.ModelPart a1, net.minecraft.world.entity.Mob a2, float a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.AnimationUtils.swingWeaponDown now takes EquipmentSlot (net.minecraft.world.entity.HumanoidArm) instead of LivingEntity (net.minecraft.world.entity.Mob); cannot derive slot from entity without additional context");
    }

    /** was net/minecraft/client/renderer/fog/FogRenderer.setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/fog/FogRenderer$FogMode;FZF)V */
    public static void method_3211(net.minecraft.client.Camera a0, net.minecraft.client.renderer.fog.FogRenderer.FogMode a1, float a2, boolean a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: setupFog now requires a Vector4f (computed by computeFogColor which needs a net.minecraft.client.multiplayer.ClientLevel world) that was previously internal; no equivalent without world access");
    }

    /** was net/minecraft/client/telemetry/TelemetryPropertyMap/createCodec(Ljava/util/List;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_47761(java.util.List a0) {
        return net.minecraft.client.telemetry.TelemetryPropertyMap.createCodec(a0).codec();
    }

    /** was net/minecraft/client/renderer/entity/EndCrystalRenderer.getY(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;F)F */
    public static float method_23155(net.minecraft.world.entity.boss.enderdragon.EndCrystal a0, float a1) {
        return net.minecraft.client.renderer.entity.EndCrystalRenderer.getY(a1);
    }
}
