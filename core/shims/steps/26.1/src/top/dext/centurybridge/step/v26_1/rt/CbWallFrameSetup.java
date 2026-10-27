package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GlobalSettingsUniform;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;

/**
 * 1.21.11 frame-setup calls 26.1 reshaped (javap of both):
 * <ul>
 * <li>FogRenderer.setupFog(camera, renderDistance, deltaTracker, darkenWorldAmount, level) computed the fog,
 * wrote it into the fog buffer and answered its colour; 26.1's setupFog answers the fog data and
 * updateBuffer(data) writes it -- both, then the data's colour;</li>
 * <li>GlobalSettingsUniform.update(.., camera, ..) used the camera's position; 26.1's takes the position.</li>
 * </ul>
 * Each: a mod's call reaches a 1.21.11 override when its class declares one (adapterHooks), else 1.21.11's
 * body on 26.1's member; a 1.21.11 override's super call is 1.21.11's body.
 */
@Environment(EnvType.CLIENT)
public final class CbWallFrameSetup {
    private CbWallFrameSetup() {
    }

    // ---- FogRenderer.setupFog ----

    /** 1.21.11's setupFog, declared by a mod's fog renderer subclass (adapterHooks) */
    public interface LegacySetupFog {
        Vector4f setupFog(Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level);
    }

    /** a mod's call of fogRenderer.setupFog(..) */
    public static Vector4f setupFog(FogRenderer renderer, Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenWorldAmount,
        ClientLevel level) {
        if (renderer instanceof LegacySetupFog legacy) {
            return legacy.setupFog(camera, renderDistance, deltaTracker, darkenWorldAmount, level);
        }
        return superSetupFog(renderer, camera, renderDistance, deltaTracker, darkenWorldAmount, level);
    }

    /** 1.21.11's body: the fog computed and written into the buffer, its colour answered */
    public static Vector4f superSetupFog(FogRenderer renderer, Camera camera, int renderDistance, DeltaTracker deltaTracker, float darkenWorldAmount,
        ClientLevel level) {
        FogData data = renderer.setupFog(camera, renderDistance, deltaTracker, darkenWorldAmount, level);
        renderer.updateBuffer(data);
        return data.color;
    }

    // ---- GlobalSettingsUniform.update ----

    /** 1.21.11's update, declared by a mod's subclass (adapterHooks) */
    public interface LegacyGlobalSettings {
        void update(int width, int height, double glintStrength, long gameTime, DeltaTracker deltaTracker, int menuBlurRadius, Camera camera,
            boolean useRgss);
    }

    /** a mod's call of globalSettings.update(..) */
    public static void update(GlobalSettingsUniform uniform, int width, int height, double glintStrength, long gameTime, DeltaTracker deltaTracker,
        int menuBlurRadius, Camera camera, boolean useRgss) {
        if (uniform instanceof LegacyGlobalSettings legacy) {
            legacy.update(width, height, glintStrength, gameTime, deltaTracker, menuBlurRadius, camera, useRgss);
            return;
        }
        superUpdate(uniform, width, height, glintStrength, gameTime, deltaTracker, menuBlurRadius, camera, useRgss);
    }

    /** 1.21.11's body: from the camera's position */
    public static void superUpdate(GlobalSettingsUniform uniform, int width, int height, double glintStrength, long gameTime, DeltaTracker deltaTracker,
        int menuBlurRadius, Camera camera, boolean useRgss) {
        uniform.update(width, height, glintStrength, gameTime, deltaTracker, menuBlurRadius, camera.position(), useRgss);
    }
}
