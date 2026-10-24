package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * 1.21.11's LightTexture statics, gone with the class in 26.1 (its packed-light helpers live on in
 * LightCoordsUtil, with arithmetic instead of logical shifts). Kept with 1.21.11's bodies, so a packed
 * value outside the light range unpacks exactly as it did.
 */
public final class CbWallLight {

    private CbWallLight() {
    }

    /** 1.21.11 LightTexture.FULL_BRIGHT */
    public static int field_32767() {
        return 15728880;
    }

    /** 1.21.11 LightTexture.FULL_SKY */
    public static int field_32768() {
        return 15728640;
    }

    /** 1.21.11 LightTexture.FULL_BLOCK */
    public static int field_32769() {
        return 240;
    }

    /** 1.21.11 LightTexture.pack(block, sky) */
    public static int pack(int block, int sky) {
        return block << 4 | sky << 20;
    }

    /** 1.21.11 LightTexture.block(packed) */
    public static int block(int packed) {
        return packed >>> 4 & 15;
    }

    /** 1.21.11 LightTexture.sky(packed) */
    public static int sky(int packed) {
        return packed >>> 20 & 15;
    }

    /** 1.21.11 LightTexture.lightCoordsWithEmission(packed, emission) */
    public static int lightCoordsWithEmission(int packed, int emission) {
        if (emission == 0) {
            return packed;
        }
        int sky = Math.max(sky(packed), emission);
        int block = Math.max(block(packed), emission);
        return pack(block, sky);
    }

    /** 1.21.11 LightTexture.getBrightness(float ambientLight, int level) */
    public static float getBrightness(float ambientLight, int level) {
        float f = level / 15.0F;
        float curve = f / (4.0F - 3.0F * f);
        return Mth.lerp(curve, ambientLight, 1.0F);
    }

    /** 1.21.11 LightTexture.getBrightness(DimensionType, int level) */
    public static float getBrightness(DimensionType type, int level) {
        return getBrightness(type.ambientLight(), level);
    }
}
