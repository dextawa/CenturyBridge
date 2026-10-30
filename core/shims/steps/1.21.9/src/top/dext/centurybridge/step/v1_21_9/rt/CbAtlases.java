package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1059;
import net.minecraft.class_11697;
import net.minecraft.class_2960;

/**
 * 1.21.8 looked atlases up by texture location (ModelManager.getAtlas, AtlasSet.getAtlas: textures/atlas/blocks.png
 * ...); 1.21.9's AtlasManager keys them by definition id (getAtlasOrThrow(AtlasIds.BLOCKS)) and registers each with the
 * texture manager under its location. This finds an AtlasManager atlas by location, without touching the texture
 * manager (whose getTexture would load a texture for an unknown location).
 */
@Environment(EnvType.CLIENT)
public final class CbAtlases {
    private CbAtlases() {
    }

    /** the atlas whose texture location is {@code location}, null when the AtlasManager holds none */
    public static class_1059 byLocation(class_11697 atlases, class_2960 location) {
        class_1059[] found = new class_1059[1];
        atlases.method_73156((id, atlas) -> {
            if (atlas.method_24106().equals(location)) {
                found[0] = atlas;
            }
        });
        return found[0];
    }
}
