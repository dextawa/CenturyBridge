package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.platform.Lighting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Lighting's light directions as 26.1.2 declared them (javap of both): 26.2 retyped the six private static constants from
 * Vector3f to Vector3fc, so a 26.1.2 read of them (an @Accessor such as the climbed 1.20.5 layer's CbClientLightingAccessor,
 * InvalidAccessorException on 26.2) names a field 26.2 no longer has. 26.2's static initializer still stores a new, normalized
 * Vector3f in each, so the getter answers that same object, as 26.1.2's field held it (a copy only if some other mod put a
 * value that is no Vector3f there). The fields are opened by the layer's access widener.
 */
@Environment(EnvType.CLIENT)
public final class CbS26LightingClient {
    private CbS26LightingClient() {}

    private static Vector3f v(Vector3fc v) {
        return v instanceof Vector3f f ? f : new Vector3f(v);
    }

    /** 26.1.2 Lighting.DIFFUSE_LIGHT_0 */
    public static Vector3f DIFFUSE_LIGHT_0() {
        return v(Lighting.DIFFUSE_LIGHT_0);
    }

    /** 26.1.2 Lighting.DIFFUSE_LIGHT_1 */
    public static Vector3f DIFFUSE_LIGHT_1() {
        return v(Lighting.DIFFUSE_LIGHT_1);
    }

    /** 26.1.2 Lighting.NETHER_DIFFUSE_LIGHT_0 */
    public static Vector3f NETHER_DIFFUSE_LIGHT_0() {
        return v(Lighting.NETHER_DIFFUSE_LIGHT_0);
    }

    /** 26.1.2 Lighting.NETHER_DIFFUSE_LIGHT_1 */
    public static Vector3f NETHER_DIFFUSE_LIGHT_1() {
        return v(Lighting.NETHER_DIFFUSE_LIGHT_1);
    }

    /** 26.1.2 Lighting.INVENTORY_DIFFUSE_LIGHT_0 */
    public static Vector3f INVENTORY_DIFFUSE_LIGHT_0() {
        return v(Lighting.INVENTORY_DIFFUSE_LIGHT_0);
    }

    /** 26.1.2 Lighting.INVENTORY_DIFFUSE_LIGHT_1 */
    public static Vector3f INVENTORY_DIFFUSE_LIGHT_1() {
        return v(Lighting.INVENTORY_DIFFUSE_LIGHT_1);
    }
}
