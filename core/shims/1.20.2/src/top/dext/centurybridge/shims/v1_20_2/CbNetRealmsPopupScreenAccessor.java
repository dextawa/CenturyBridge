package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_437;
import net.minecraft.class_8656;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * AddRealmPopupScreen's background screen: 1.20.2 opens the purchase popup as its own screen drawn
 * over the main screen that opened it. CbNetRealmsPopupBridge asks it whether the popup a main
 * screen's button opened is the one showing.
 */
@Mixin(class_8656.class)
public interface CbNetRealmsPopupScreenAccessor {

    @Accessor("field_45268")
    class_437 cb$backgroundScreen();
}
