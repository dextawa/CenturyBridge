package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3's builtin model markers on 1.21.4 (SpecialModels facade). 1.21.3 made both as empty BlockModels named
 * "generation marker" (gui light FRONT) and "block entity marker" (SIDE): the root of a builtin/generated chain was the
 * first, of a builtin/entity chain the second. 1.21.4 resolves builtin/generated to its ItemModelGenerator model and has no
 * builtin/entity; 1.21.3's root of such a chain is answered again (getRootModel, CbModelBlockModel), and a chain ending in
 * a marker bakes as 1.21.3 baked it (CbModelBlockModelMixin).
 */
@Environment(EnvType.CLIENT)
public final class CbModelSpecial {
    private CbModelSpecial() {
    }

    private static net.minecraft.class_793 generated;
    private static net.minecraft.class_793 blockEntity;
    private static net.minecraft.class_801 generator;

    private static net.minecraft.class_793 marker(String name, net.minecraft.class_1100.class_4751 guiLight) {
        net.minecraft.class_793 model = CbModelBlockModel.create(null, List.of(), Map.of(), null, guiLight, net.minecraft.class_809.field_4301, List.of());
        ((CbModelBlockModel.CbLegacyBlockModel) model).cb$setName(name);
        return model;
    }

    /** GENERATED_MARKER */
    public static synchronized net.minecraft.class_793 generatedMarker() {
        if (generated == null) {
            generated = marker("generation marker", net.minecraft.class_1100.class_4751.field_21858);
        }
        return generated;
    }

    /** BLOCK_ENTITY_MARKER */
    public static synchronized net.minecraft.class_793 blockEntityMarker() {
        if (blockEntity == null) {
            blockEntity = marker("block entity marker", net.minecraft.class_1100.class_4751.field_21859);
        }
        return blockEntity;
    }

    /** the item model generator a chain ending in GENERATED_MARKER bakes through (1.21.4's builtin/generated model) */
    public static synchronized net.minecraft.class_801 generator() {
        if (generator == null) {
            generator = new net.minecraft.class_801();
        }
        return generator;
    }
}
