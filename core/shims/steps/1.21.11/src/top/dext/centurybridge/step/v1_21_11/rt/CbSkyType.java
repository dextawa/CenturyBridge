package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12076;
import net.minecraft.class_2874;
import top.dext.centurybridge.step.v1_21_11.facade.DimensionSpecialEffects;

/**
 * 1.21.10's SkyRenderState.skyType (DimensionSpecialEffects.SkyType) is 1.21.11's SkyRenderState.skybox
 * (DimensionType.Skybox): NONE -> NONE, NORMAL -> OVERWORLD, END -> END, the same three skies drawn the same way
 * (LevelRenderer's sky pass skips NONE and draws the end sky for END in both).
 */
@Environment(EnvType.CLIENT)
public final class CbSkyType {
    private CbSkyType() {
    }

    public static DimensionSpecialEffects.SkyType toV(class_2874.class_12326 skybox) {
        if (skybox == class_2874.class_12326.field_64385) {
            return DimensionSpecialEffects.SkyType.field_25639;
        }
        if (skybox == class_2874.class_12326.field_64387) {
            return DimensionSpecialEffects.SkyType.field_25641;
        }
        return DimensionSpecialEffects.SkyType.field_25640;
    }

    public static class_2874.class_12326 toW(DimensionSpecialEffects.SkyType skyType) {
        return switch (skyType) {
            case field_25639 -> class_2874.class_12326.field_64385;
            case field_25640 -> class_2874.class_12326.field_64386;
            case field_25641 -> class_2874.class_12326.field_64387;
        };
    }

    /** SkyRenderState.skyType read */
    public static DimensionSpecialEffects.SkyType field_63088(class_12076 state) {
        return toV(state.field_64464);
    }

    /** SkyRenderState.skyType write */
    public static void field_63088(class_12076 state, DimensionSpecialEffects.SkyType skyType) {
        state.field_64464 = toW(skyType);
    }
}
