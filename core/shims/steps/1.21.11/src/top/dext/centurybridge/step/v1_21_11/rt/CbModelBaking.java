package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10819;
import net.minecraft.class_10889;
import net.minecraft.class_2960;
import net.minecraft.class_7775;
import net.minecraft.class_9826;
import org.joml.Vector3fc;

/**
 * 1.21.10 FaceBakery.bakeQuad(from, to, face, sprite, direction, state, rotation, shade, light) and
 * SimpleUnbakedGeometry.bake(elements, textures, sprites, state, debugName) on 1.21.11's shapes (javap -c -p both code
 * jars).
 * <ul>
 * <li>bakeQuad gained a first argument, ModelBaker.Interner (method_76676(Vector3fc)), which 1.21.11 passes each baked
 *     position through. 1.21.10's body interned nothing; vanilla's interner (ModelBakery's, class_1088$class_12357) is
 *     a Guava strong interner, which only shares equal vectors. Identity gives 1.21.10's values exactly.</li>
 * <li>SimpleUnbakedGeometry.bake (static, so no override) took a SpriteGetter third; 1.21.11 takes the ModelBaker and
 *     reads from it only sprites() (method_65732) and interner() (method_76674), javap of its body. The 1.21.10 call's
 *     getter is handed in as a ModelBaker answering those two: the getter and the identity interner. The other ModelBaker
 *     members have no 1.21.10 counterpart for this call (1.21.10 passed no baker) and are not read by the method.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbModelBaking {
    private CbModelBaking() {
    }

    private static final class_7775.class_12356 IDENTITY = new class_7775.class_12356() {
        @Override
        public Vector3fc method_76676(Vector3fc v) {
            return v;
        }
    };

    /** the interner 1.21.10's bakeQuad stands for: none (identity) */
    public static class_7775.class_12356 identityInterner() {
        return IDENTITY;
    }

    /** a 1.21.10 call's SpriteGetter as the ModelBaker 1.21.11's SimpleUnbakedGeometry.bake reads it from */
    public static class_7775 baker(class_9826 sprites) {
        return new class_7775() {
            @Override
            public class_9826 method_65732() {
                return sprites;
            }

            @Override
            public class_7775.class_12356 method_76674() {
                return IDENTITY;
            }

            @Override
            public class_10819 method_45872(class_2960 id) {
                throw new UnsupportedOperationException("1.21.10 SimpleUnbakedGeometry.bake passed sprites only, no model baker");
            }

            @Override
            public class_10889 method_76673() {
                throw new UnsupportedOperationException("1.21.10 SimpleUnbakedGeometry.bake passed sprites only, no model baker");
            }

            @Override
            public <T> T method_68549(class_7775.class_10897<T> key) {
                throw new UnsupportedOperationException("1.21.10 SimpleUnbakedGeometry.bake passed sprites only, no model baker");
            }
        };
    }
}
