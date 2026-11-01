package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5604;
import net.minecraft.class_630;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Model geometry vectors 1.21.9 retyped from Vector3f to Vector3fc (javap -c both code jars):
 *  - CubeDefinition origin / dimensions (field_27709 / field_27710): 1.21.9's constructor still stores
 *    new Vector3f(..), so the object a 1.21.8 mod read is handed back as it is (writes into it still land);
 *  - ModelPart.Polygon normal (comp_3185): 1.21.8 held a fresh copy of the face direction's normal
 *    (Direction.step() copies), 1.21.9 holds Direction's own shared vector -- answered as a copy, as 1.21.8's was
 *    per polygon (a write into it no longer reaches the polygon: known loss);
 *  - ModelPart.Vertex pos (comp_3186): 1.21.9 keeps the three floats (comp_4804/4805/4806, the same unscaled
 *    coordinates 1.21.8's Vector3f held) -- answered as a new Vector3f (writes no longer reach the vertex: known loss);
 *    new Vertex(Vector3f, u, v) is 1.21.9's (x, y, z, u, v), as 1.21.8's float constructor delegated.
 */
@Environment(EnvType.CLIENT)
public final class CbModelVectors {
    private CbModelVectors() {
    }

    private static Vector3f mutable(Vector3fc v) {
        return v instanceof Vector3f m ? m : new Vector3f(v);
    }

    /** CubeDefinition.origin */
    public static Vector3f field_27709(class_5604 cube) {
        return mutable(cube.field_27709);
    }

    /** CubeDefinition.dimensions */
    public static Vector3f field_27710(class_5604 cube) {
        return mutable(cube.field_27710);
    }

    /** ModelPart.Polygon.normal() / .normal */
    public static Vector3f comp_3185(class_630.class_593 polygon) {
        return new Vector3f(polygon.comp_3185());
    }

    /** ModelPart.Vertex.pos() / .pos */
    public static Vector3f comp_3186(class_630.class_618 vertex) {
        return new Vector3f(vertex.comp_4804(), vertex.comp_4805(), vertex.comp_4806());
    }

    /** new ModelPart.Vertex(pos, u, v) */
    public static class_630.class_618 createVertex(Vector3f pos, float u, float v) {
        return new class_630.class_618(pos.x, pos.y, pos.z, u, v);
    }
}
