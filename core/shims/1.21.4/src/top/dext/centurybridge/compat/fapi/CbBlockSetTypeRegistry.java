package top.dext.centurybridge.compat.fapi;

import net.minecraft.class_2498;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_8177;

/**
 * fabric-object-builder-api-v1's BlockSetTypeRegistry (removed in 1.21.2; vanilla's own register is what it
 * wrapped). 1.21.4 BlockSetType gained wind-charge / arrow-button / pressure-plate sensitivity fields; the
 * 1.20.1 shape implied the permissive defaults (what fapi's BlockSetTypeBuilder still uses). The private
 * register is opened at runtime by fapi's access widener (compile against the widened stub).
 * classRename: net/fabricmc/fabric/api/object/builder/v1/block/type/BlockSetTypeRegistry -> this class.
 */
public final class CbBlockSetTypeRegistry {

    public static class_8177 registerWood(class_2960 id) {
        return class_8177.method_49233(new class_8177(id.toString()));
    }

    public static class_8177 register(class_2960 id, boolean openByHand, class_2498 soundType, class_3414 doorClose,
            class_3414 doorOpen, class_3414 trapdoorClose, class_3414 trapdoorOpen, class_3414 pressurePlateClickOff,
            class_3414 pressurePlateClickOn, class_3414 buttonClickOff, class_3414 buttonClickOn) {
        return class_8177.method_49233(new class_8177(id.toString(), openByHand, true, true, class_8177.class_2441.field_11361,
            soundType, doorClose, doorOpen, trapdoorClose, trapdoorOpen, pressurePlateClickOff, pressurePlateClickOn,
            buttonClickOff, buttonClickOn));
    }

    private CbBlockSetTypeRegistry() {
    }
}
