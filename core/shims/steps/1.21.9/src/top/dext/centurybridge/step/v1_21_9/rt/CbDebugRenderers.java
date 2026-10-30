package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_863;

/**
 * 1.21.8 Minecraft.debugRenderer (public final, made in Minecraft's constructor): 1.21.9 moved the game's one
 * DebugRenderer into LevelRenderer (field_63067, public final, made in its constructor), which renders it. A converted
 * mod's read of Minecraft.debugRenderer reads that one (instanceFieldGetters, s9e-debugrenderer.json).
 */
@Environment(EnvType.CLIENT)
public final class CbDebugRenderers {
    private CbDebugRenderers() {
    }

    /** Minecraft.debugRenderer */
    public static class_863 field_1709(class_310 minecraft) {
        return minecraft.field_1769.field_63067;
    }
}
