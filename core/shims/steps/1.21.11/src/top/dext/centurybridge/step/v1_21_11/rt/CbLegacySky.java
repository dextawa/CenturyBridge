package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_9975;

/**
 * 1.21.10 SkyRenderer (class_9975) private members reached through a mod's access widener (javap -c -p of both code
 * jars, both client jars):
 * - SUN_LOCATION / END_FLASH_LOCATION named textures/environment/sun.png and end_flash.png; 1.21.11 moved both files,
 *   byte-identical (md5), to textures/environment/celestial/ (stitched into the celestials atlas, whose directory
 *   source still leaves each file loadable as a standalone texture). The getters name the moved files.
 * - sunTexture / endFlashTexture held TextureManager.getTexture(location) (1.21.10 method_74757, which also turned
 *   mipmaps off; 1.21.11 textures are created with one mip level). The getters answer the same cached texture for the
 *   moved file. Writes have no 1.21.11 reader (the sky draws from the atlas) and stay open.
 * - field_57935 was the first RenderSystem.getSequentialBuffer(QUADS) the constructor kept; that call answers the one
 *   shared quad index buffer, the same object 1.21.11 keeps in field_62961.
 * MOON_LOCATION (moon_phases.png, a 4x2 sheet) has no 1.21.11 file: its tiles are the eight celestial/moon sprites and
 * stays open, as do the moon texture field and the buffer builders.
 */
@Environment(EnvType.CLIENT)
public final class CbLegacySky {

    private static final class_2960 SUN = class_2960.method_60656("textures/environment/celestial/sun.png");
    private static final class_2960 END_FLASH = class_2960.method_60656("textures/environment/celestial/end_flash.png");

    /** SUN_LOCATION */
    public static class_2960 field_53141() {
        return SUN;
    }

    /** END_FLASH_LOCATION */
    public static class_2960 field_62044() {
        return END_FLASH;
    }

    /** sunTexture */
    public static class_1044 field_62962(class_9975 sky) {
        return class_310.method_1551().method_1531().method_4619(SUN);
    }

    /** endFlashTexture */
    public static class_1044 field_62949(class_9975 sky) {
        return class_310.method_1551().method_1531().method_4619(END_FLASH);
    }

    /** the constructor's first sequential quad index buffer */
    public static RenderSystem.class_5590 field_57935(class_9975 sky) {
        return RenderSystem.getSequentialBuffer(VertexFormat.class_5596.field_27382);
    }

    private CbLegacySky() {
    }
}
