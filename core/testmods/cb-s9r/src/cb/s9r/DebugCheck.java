package cb.s9r;

import net.minecraft.class_310;
import net.minecraft.class_863;

/** 1.21.8 DebugRenderer, reached the 1.21.8 way (Minecraft.debugRenderer): the toggles answer the new state; clear() */
public final class DebugCheck {
    private DebugCheck() {
    }

    static class_863 renderer() {
        return class_310.method_1551().field_1709;
    }

    /** "the identity hash of Minecraft.debugRenderer" (the driver compares it with LevelRenderer's) */
    public static int rendererId() {
        return System.identityHashCode(renderer());
    }

    public static boolean switchChunkBorder() {
        return renderer().method_3713();
    }

    public static boolean toggleOctree() {
        return renderer().method_62979();
    }

    public static void clear() {
        renderer().method_20413();
    }
}
