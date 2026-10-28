package cb.s9r;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_5599;
import net.minecraft.class_5601;
import net.minecraft.class_5602;
import net.minecraft.class_630;
import org.joml.Vector3f;

/**
 * 1.21.8 armor layers: bakes ModelLayers.<MODEL>_INNER_ARMOR / _OUTER_ARMOR through the game's EntityModelSet, as a
 * 1.21.8 armor renderer does, and answers each humanoid part's vertices (ModelPart.getExtentsForGui-style collection,
 * no pose). Read by the native driver through reflection, which compares them with 1.21.9's own slot layers.
 */
public final class ArmorCheck {
    public static final String[] PARTS = {"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};

    private ArmorCheck() {
    }

    /** "player" / "zombie_baby" / "armor_stand" -> {inner part -> vertices, outer part -> vertices} */
    public static Map<String, Map<String, Set<Vector3f>>[]> bake() {
        class_5599 models = class_310.method_1551().method_31974();
        Map<String, Map<String, Set<Vector3f>>[]> out = new LinkedHashMap<>();
        out.put("player", pair(models, class_5602.field_27579, class_5602.field_27580));
        out.put("zombie_baby", pair(models, class_5602.field_53009, class_5602.field_53010));
        out.put("armor_stand", pair(models, class_5602.field_27639, class_5602.field_27677));
        return out;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Set<Vector3f>>[] pair(class_5599 models, class_5601 inner, class_5601 outer) {
        return new Map[]{parts(models.method_32072(inner)), parts(models.method_32072(outer))};
    }

    public static Map<String, Set<Vector3f>> parts(class_630 root) {
        Map<String, Set<Vector3f>> out = new LinkedHashMap<>();
        for (String name : PARTS) {
            Set<Vector3f> v = new HashSet<>();
            root.method_32086(name).method_72152(new class_4587(), v);
            out.put(name, v);
        }
        return out;
    }
}
