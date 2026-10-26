package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 LevelRenderer methods with nothing left to do in 1.21.2:
 * - method_3267: empty in 1.21.1 itself (its body is a bare return);
 * - graphicsChanged (method_35774): set up or tore down the Fabulous transparency targets for the new graphics mode;
 *   1.21.2 sets them up for every frame from the current mode (its renderLevel asks Minecraft.useShaderTransparency()),
 *   so a mode change takes effect without it.
 * The level events and jukebox songs 1.21.1 handled here (levelEvent, globalLevelEvent, playJukeboxSong,
 * stopJukeboxSongAndNotifyNearby): 1.21.2's LevelEventHandler of the level, which 1.21.2 calls instead and which hands
 * the calls back to a mod subclass's overrides (CbRenderLevel.routes).
 * And 1.21.1's renderSky / renderClouds, which 1.21.2 draws as passes of its frame graph:
 * - renderSky(frustumMatrix, projectionMatrix, partialTick, camera, isFoggy, skyFogSetup): 1.21.1's checks (fog setup
 *   run first; nothing when foggy, in powder snow or lava, or when a mob effect blocks the sky), then 1.21.2's sky drawing
 *   (the sky pass's body, method_62215: the end sky or the overworld sky disc, sunrise, sun, moon, stars and dark disc,
 *   with the fog the setup left) under the frustum matrix, which 1.21.1 put on its pose stack and 1.21.2's pass finds on
 *   the model-view stack; nothing for a dimension without a sky (1.21.2's sky pass is added only for one with a sky).
 * - renderClouds(poseStack, frustumMatrix, projectionMatrix, partialTick, camX, camY, camZ): nothing for a dimension
 *   without clouds (NaN height), else 1.21.2's CloudRenderer with what 1.21.1 drew from -- the level's cloud colour, the
 *   cloud option, the dimension's cloud height + 0.33, the pose stack times the frustum matrix as model view, the given
 *   camera position and ticks + partialTick (1.21.2's clouds pass passes the same values).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_761.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderLevelMixin implements top.dext.centurybridge.step.v1_21_2.rt.CbRenderLevel.LegacyEvents {

    @Shadow
    private net.minecraft.class_638 field_4085;

    @Shadow
    private int field_4073;

    @Shadow
    private boolean method_43788(net.minecraft.class_4184 camera) {
        throw new AssertionError();
    }

    @Shadow
    private void method_62215(net.minecraft.class_9958 fog, net.minecraft.class_5294.class_5401 skyType, float partialTick, net.minecraft.class_5294 effects) {
        throw new AssertionError();
    }

    /** 1.21.1 LevelRenderer.method_3267 (empty there) */
    public void method_3267() {
    }

    /** 1.21.1 LevelRenderer.graphicsChanged() */
    public void method_35774() {
    }

    /** 1.21.1 renderSky(frustumMatrix, projectionMatrix, partialTick, camera, isFoggy, skyFogSetup) */
    public void method_3257(org.joml.Matrix4f frustumMatrix, org.joml.Matrix4f projectionMatrix, float partialTick, net.minecraft.class_4184 camera,
                            boolean isFoggy, Runnable skyFogSetup) {
        skyFogSetup.run();
        if (isFoggy) {
            return;
        }
        net.minecraft.class_5636 fogType = camera.method_19334();
        if (fogType == net.minecraft.class_5636.field_27887 || fogType == net.minecraft.class_5636.field_27885 || this.method_43788(camera)) {
            return;
        }
        net.minecraft.class_5294 effects = this.field_4085.method_28103();
        net.minecraft.class_5294.class_5401 skyType = effects.method_29992();
        if (skyType == net.minecraft.class_5294.class_5401.field_25639) {
            return;
        }
        org.joml.Matrix4fStack modelView = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(frustumMatrix);
        try {
            this.method_62215(com.mojang.blaze3d.systems.RenderSystem.getShaderFog(), skyType, partialTick, effects);
        } finally {
            modelView.popMatrix();
        }
    }

    /** 1.21.1 renderClouds(poseStack, frustumMatrix, projectionMatrix, partialTick, camX, camY, camZ) */
    public void method_3259(net.minecraft.class_4587 poseStack, org.joml.Matrix4f frustumMatrix, org.joml.Matrix4f projectionMatrix, float partialTick,
                            double camX, double camY, double camZ) {
        float height = this.field_4085.method_28103().method_28108();
        if (Float.isNaN(height)) {
            return;
        }
        net.minecraft.class_310 minecraft = net.minecraft.class_310.method_1551();
        org.joml.Matrix4f modelView = new org.joml.Matrix4f(poseStack.method_23760().method_23761()).mul(frustumMatrix);
        ((net.minecraft.class_761) (Object) this).method_62196().method_62168(this.field_4085.method_23785(partialTick),
            minecraft.field_1690.method_1632(), height + 0.33F, modelView, projectionMatrix, new net.minecraft.class_243(camX, camY, camZ),
            (float) this.field_4073 + partialTick);
    }

    /** 1.21.1 levelEvent(type, pos, data) */
    @Override
    public void method_8567(int type, net.minecraft.class_2338 pos, int data) {
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderLevel.levelEvent(this.field_4085, type, pos, data);
    }

    /** 1.21.1 globalLevelEvent(type, pos, data) */
    @Override
    public void method_8564(int type, net.minecraft.class_2338 pos, int data) {
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderLevel.globalLevelEvent(this.field_4085, type, pos, data);
    }

    /** 1.21.1 playJukeboxSong(song, pos) */
    @Override
    public void method_60891(net.minecraft.class_6880<net.minecraft.class_9793> song, net.minecraft.class_2338 pos) {
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderLevel.playJukeboxSong(this.field_4085, song, pos);
    }

    /** 1.21.1 stopJukeboxSongAndNotifyNearby(pos) */
    @Override
    public void method_60889(net.minecraft.class_2338 pos) {
        top.dext.centurybridge.step.v1_21_2.rt.CbRenderLevel.stopJukeboxSong(this.field_4085, pos);
    }
}
