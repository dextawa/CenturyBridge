package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** DropperBlock's private DISPENSE_BEHAVIOUR, 1.20.4's getDispenseMethod answer for a dropper (CbBlockDispense) */
@Mixin(net.minecraft.class_2325.class)
public interface CbBlockDropperAccessor {

    @Accessor("field_10949")
    static net.minecraft.class_2357 cb$dispenseBehaviour() {
        throw new AssertionError();
    }
}
