package top.dext.centurybridge.step.v26_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2 com.mojang.blaze3d.platform.BackendOptions, gone in 26.2: a record of one flag, built by 26.1.2's Minecraft from
 * Options.exclusiveFullscreen and passed to GLX._initGlfw / RenderSystem.initBackendSystem, neither of which read it (javap:
 * 26.1.2 _initGlfw's body is 26.2's no-argument one but for local slots). 26.2's Window takes the flag itself. Kept so a
 * 26.1.2 construction links; the calls drop it (s26h-backend.json).
 */
@Environment(EnvType.CLIENT)
public record BackendOptions(boolean exclusiveFullScreen) {
}
