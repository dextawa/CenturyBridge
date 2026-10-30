package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1060;
import net.minecraft.class_1079;
import net.minecraft.class_10725;
import net.minecraft.class_2960;
import net.minecraft.class_7677;
import net.minecraft.class_8689;
import net.minecraft.class_8690;

/**
 * 1.21.8's GuiSpriteManager (class_8658), 1.21.8's bodies (javap -c): the gui atlas (textures/atlas/gui.png,
 * definition AtlasIds.GUI) stitched with the animation and GUI metadata sections, getSprite made public, a sprite's
 * scaling read from its GUI metadata (GuiMetadataSection.DEFAULT when it has none; 1.21.9 reads the section through
 * SpriteContents.getAdditionalMetadata, as its GuiGraphics does). 1.21.9 keeps the same atlas, definition and sprite ids
 * in AtlasManager; Minecraft.getGuiSprites() answers a view of it (see {@link TextureAtlasHolder}).
 */
@Environment(EnvType.CLIENT)
public class GuiSpriteManager extends TextureAtlasHolder {
    public static final Set<class_7677<?>> field_45338 = Set.of(class_1079.field_55537, class_8689.field_45647);

    public GuiSpriteManager(class_1060 textureManager) {
        super(textureManager, class_2960.method_60656("textures/atlas/gui.png"), class_10725.field_56385, field_45338);
    }

    protected GuiSpriteManager(Supplier<class_1059> view) {
        super(view);
    }

    /** a view of AtlasManager's gui atlas */
    public static GuiSpriteManager cb$view(Supplier<class_1059> atlas) {
        return new GuiSpriteManager(atlas);
    }

    @Override
    public class_1058 method_18667(class_2960 sprite) {
        return super.method_18667(sprite);
    }

    public class_8690 method_52714(class_1058 sprite) {
        return this.method_52715(sprite).comp_1636();
    }

    public class_8689 method_52715(class_1058 sprite) {
        return sprite.method_45851().method_73021(class_8689.field_45647).orElse(class_8689.field_45645);
    }
}
