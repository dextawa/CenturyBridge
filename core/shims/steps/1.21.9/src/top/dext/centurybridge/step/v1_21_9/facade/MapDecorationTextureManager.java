package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_10725;
import net.minecraft.class_20;
import net.minecraft.class_2960;

/**
 * 1.21.8's MapDecorationTextureManager (class_9443), 1.21.8's bodies (javap -c): the map decorations atlas
 * (textures/atlas/map_decorations.png, definition AtlasIds.MAP_DECORATIONS), a decoration's sprite by its sprite
 * location. 1.21.9 keeps the same atlas, definition and sprite ids in AtlasManager; Minecraft.getMapDecorationTextures()
 * answers a view of it (see {@link TextureAtlasHolder}).
 */
@Environment(EnvType.CLIENT)
public class MapDecorationTextureManager extends TextureAtlasHolder {

    public MapDecorationTextureManager(class_1060 textureManager) {
        super(textureManager, class_2960.method_60656("textures/atlas/map_decorations.png"), class_10725.field_56386);
    }

    protected MapDecorationTextureManager(Supplier<class_1059> view) {
        super(view);
    }

    /** a view of AtlasManager's map decorations atlas */
    public static MapDecorationTextureManager cb$view(Supplier<class_1059> atlas) {
        return new MapDecorationTextureManager(atlas);
    }

    public class_1058 method_58516(class_20 decoration) {
        return this.method_18667(decoration.method_58451());
    }
}
