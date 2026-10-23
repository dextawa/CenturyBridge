package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelSpecial;

/**
 * 1.21.3 SpecialModels (class_10101, gone in 1.21.4): the ids of the builtin models and their markers.
 * <ul>
 * <li>builtin/generated is 1.21.4's ItemModelGenerator model (its id, and an instance of it as the marker: 1.21.4 resolves
 *     the id to that model, which generates the item's quads from its layer textures as 1.21.3's marker made the bakery do);</li>
 * <li>builtin/entity has no 1.21.4 model; its marker is the same empty BlockModel 1.21.3 made, which a 1.21.3 bake of a chain
 *     ending in it answers as a BuiltInModel (see CbModelBlockModel).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public class SpecialModels {
    /** BUILTIN_GENERATED */
    public static final net.minecraft.class_2960 field_53677 = net.minecraft.class_801.field_55273;
    /** BUILTIN_BLOCK_ENTITY */
    public static final net.minecraft.class_2960 field_53678 = method_62664("entity");
    /** GENERATED_MARKER */
    public static final net.minecraft.class_1100 field_53679 = CbModelSpecial.generatedMarker();
    /** BLOCK_ENTITY_MARKER */
    public static final net.minecraft.class_1100 field_53680 = CbModelSpecial.blockEntityMarker();

    public SpecialModels() {
    }

    /** builtin(name): minecraft:builtin/name */
    public static net.minecraft.class_2960 method_62664(String name) {
        return net.minecraft.class_2960.method_60656("builtin/" + name);
    }
}
