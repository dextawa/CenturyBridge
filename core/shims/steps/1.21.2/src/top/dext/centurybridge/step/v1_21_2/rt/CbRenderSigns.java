package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.facade.SignModel;

/**
 * 1.21.1's sign model statics over 1.21.2's. 1.21.1 had one sign layer per wood type ("sign/&lt;wood&gt;", board and
 * stick, the stick hidden for wall signs); 1.21.2 has a standing one (board and stick, the same boxes: compared in both
 * jars) and a wall one (board only). The 1.21.1 layer is the standing one.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderSigns {

    private CbRenderSigns() {
    }

    /** ModelLayers.createSignModelName(woodType) */
    public static net.minecraft.class_5601 method_32078(net.minecraft.class_4719 woodType) {
        return net.minecraft.class_5602.method_62130(woodType);
    }

    /** SignRenderer.createSignModel(modelSet, woodType) */
    public static SignModel method_32157(net.minecraft.class_5599 modelSet, net.minecraft.class_4719 woodType) {
        return new SignModel(modelSet.method_32072(method_32078(woodType)));
    }

    /** SignRenderer.createSignLayer() */
    public static net.minecraft.class_5607 method_32154() {
        return net.minecraft.class_837.method_32154(true);
    }

    /** HangingSignRenderer.HangingSignModel.root (instanceFieldGetters): the model's root, 1.21.2's Model.root() */
    public static net.minecraft.class_630 field_40525(net.minecraft.class_7761.class_7762 model) {
        return model.method_63512();
    }
}
