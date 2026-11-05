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
 * - MOON_LOCATION (moon_phases.png, a 4x2 sheet) has no 1.21.11 file: its tile i is the celestial/moon sprite of MoonPhase
 *   ordinal i (pixel-for-pixel, all eight: 1.21.10 sheet vs 1.21.11 files; 1.21.11's enum order FULL_MOON ..
 *   WAXING_GIBBOUS, sprite name = getSerializedName). The getter names CenturyBridge's sheet, assembled once from the
 *   eight sprite files of the current resources and registered with the TextureManager under that name; moonTexture
 *   answers it. A resource pack reload after the first use does not rebuild the sheet (known), and the sheet is a
 *   registered texture, not a resource file (a mod opening MOON_LOCATION through the resource manager finds nothing).
 * The buffer builders stay open.
 */
@Environment(EnvType.CLIENT)
public final class CbLegacySky {

    private static final class_2960 SUN = class_2960.method_60656("textures/environment/celestial/sun.png");
    private static final class_2960 END_FLASH = class_2960.method_60656("textures/environment/celestial/end_flash.png");
    private static final class_2960 MOON = class_2960.method_60655("centurybridge", "textures/environment/moon_phases.png");
    private static volatile boolean moonBuilt;

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

    /** MOON_LOCATION */
    public static class_2960 field_53142() {
        return MOON;
    }

    /** moonTexture: 1.21.10 loaded MOON_LOCATION in the constructor; here the sheet is built at first use (render thread) */
    public static class_1044 field_62947(class_9975 sky) {
        net.minecraft.class_1060 textures = class_310.method_1551().method_1531();
        if (!moonBuilt) {
            synchronized (CbLegacySky.class) {
                if (!moonBuilt) {
                    textures.method_4616(MOON, new net.minecraft.class_1043(MOON::toString, moonSheet()));
                    moonBuilt = true;
                }
            }
        }
        return textures.method_4619(MOON);
    }

    private static net.minecraft.class_1011 moonSheet() {
        net.minecraft.class_12131[] phases = net.minecraft.class_12131.values();
        net.minecraft.class_3300 resources = class_310.method_1551().method_1478();
        net.minecraft.class_1011 sheet = null;
        for (int i = 0; i < phases.length; i++) {
            class_2960 file = class_2960.method_60656("textures/environment/celestial/moon/" + phases[i].method_15434() + ".png");
            try (java.io.InputStream in = resources.method_14486(file).orElseThrow(
                    () -> new java.io.FileNotFoundException(file.toString())).method_14482();
                 net.minecraft.class_1011 tile = net.minecraft.class_1011.method_4309(in)) {
                int w = tile.method_4307();
                int h = tile.method_4323();
                if (sheet == null) {
                    sheet = new net.minecraft.class_1011(w * 4, h * 2, false);
                }
                int ox = (i % 4) * w;
                int oy = (i / 4) * h;
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        sheet.method_61941(ox + x, oy + y, tile.method_61940(x, y));
                    }
                }
            } catch (java.io.IOException e) {
                if (sheet != null) {
                    sheet.close();
                }
                throw new java.io.UncheckedIOException("moon phase sprite " + file, e);
            }
        }
        return sheet;
    }

    /** the constructor's first sequential quad index buffer */
    public static RenderSystem.class_5590 field_57935(class_9975 sky) {
        return RenderSystem.getSequentialBuffer(VertexFormat.class_5596.field_27382);
    }

    private CbLegacySky() {
    }
}
