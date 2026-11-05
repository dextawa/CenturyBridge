package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * 1.21.10 FaceBakery (class_796) private statics reached through a mod's access widener (javap -c -p -constants of
 * both code jars): field_32799 (= 3, a compile-time constant) and field_60149 (new Vector3f(1, 1, 1), the unit scale
 * its clinit stored) are gone in 1.21.11, whose baker writes quads as Vector3fc positions instead of int[] vertex data.
 * The int[]-vertex helpers stay open: 1.21.11 BakedQuad has no int[] for them to read.
 */
@Environment(EnvType.CLIENT)
public final class CbLegacyFaceBakery {

    private static final Vector3fc UNIT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);

    public static int field_32799() {
        return 3;
    }

    public static Vector3fc field_60149() {
        return UNIT_SCALE;
    }

    private CbLegacyFaceBakery() {
    }
}
