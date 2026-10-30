package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_10725;
import net.minecraft.class_1535;
import net.minecraft.class_2960;

/**
 * 1.21.8's PaintingTextureManager (class_4044), 1.21.8's bodies (javap -c): the paintings atlas
 * (textures/atlas/paintings.png, definition AtlasIds.PAINTINGS), a variant's sprite by its asset id, the back sprite.
 * 1.21.9 keeps the same atlas, definition and sprite ids in AtlasManager; Minecraft.getPaintingTextures() answers a
 * view of it (see {@link TextureAtlasHolder}).
 */
@Environment(EnvType.CLIENT)
public class PaintingTextureManager extends TextureAtlasHolder {
    public static final class_2960 field_18032 = class_2960.method_60656("back");

    public PaintingTextureManager(class_1060 textureManager) {
        super(textureManager, class_2960.method_60656("textures/atlas/paintings.png"), class_10725.field_56388);
    }

    protected PaintingTextureManager(Supplier<class_1059> view) {
        super(view);
    }

    /** a view of AtlasManager's paintings atlas */
    public static PaintingTextureManager cb$view(Supplier<class_1059> atlas) {
        return new PaintingTextureManager(atlas);
    }

    public class_1058 method_18345(class_1535 variant) {
        return this.method_18667(variant.comp_2672());
    }

    public class_1058 method_18342() {
        return this.method_18667(field_18032);
    }
}
