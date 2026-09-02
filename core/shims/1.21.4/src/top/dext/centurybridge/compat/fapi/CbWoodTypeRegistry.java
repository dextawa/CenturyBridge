package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_2498;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_4719;
import net.minecraft.class_8177;

/**
 * fabric-object-builder-api-v1's WoodTypeRegistry (removed in 1.21.2); same shapes as 1.20.1, straight onto
 * vanilla's private register (fapi's access widener opens it at runtime).
 * classRename: net/fabricmc/fabric/api/object/builder/v1/block/type/WoodTypeRegistry -> this class.
 */
public final class CbWoodTypeRegistry {

    public static class_4719 register(class_2960 id, class_8177 setType) {
        return class_4719.method_24027(new class_4719(id.toString(), setType));
    }

    public static class_4719 register(class_2960 id, class_8177 setType, class_2498 soundType, class_2498 hangingSignSoundType,
            class_3414 fenceGateClose, class_3414 fenceGateOpen) {
        return class_4719.method_24027(new class_4719(id.toString(), setType, soundType, hangingSignSoundType, fenceGateClose, fenceGateOpen));
    }

    private CbWoodTypeRegistry() {
    }
}
