package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.platform.BlendFactor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2's DestFactor, which 26.2 merged with SourceFactor into BlendFactor (classRenames): DestFactor lacked
 * SRC_ALPHA_SATURATE (a source-only factor in GL), so its values() and valueOf answer as 26.1.2's enum did, in its
 * declaration order (javap of 26.1.2).
 */
@Environment(EnvType.CLIENT)
public final class CbS26Blend {
    private CbS26Blend() {}

    private static final BlendFactor[] DEST = {
        BlendFactor.CONSTANT_ALPHA, BlendFactor.CONSTANT_COLOR, BlendFactor.DST_ALPHA, BlendFactor.DST_COLOR, BlendFactor.ONE,
        BlendFactor.ONE_MINUS_CONSTANT_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_COLOR, BlendFactor.ONE_MINUS_DST_ALPHA,
        BlendFactor.ONE_MINUS_DST_COLOR, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.SRC_ALPHA,
        BlendFactor.SRC_COLOR, BlendFactor.ZERO};

    /** 26.1.2 DestFactor.values() */
    public static BlendFactor[] destValues() {
        return DEST.clone();
    }

    /** 26.1.2 DestFactor.valueOf(name) */
    public static BlendFactor destValueOf(String name) {
        for (BlendFactor f : DEST) {
            if (f.name().equals(name)) {
                return f;
            }
        }
        if (name == null) {
            throw new NullPointerException("Name is null");
        }
        throw new IllegalArgumentException("No enum constant com.mojang.blaze3d.platform.DestFactor." + name);
    }
}
