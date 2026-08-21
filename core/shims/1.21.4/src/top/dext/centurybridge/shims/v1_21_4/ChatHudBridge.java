package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;

/**
 * ChatHud kept its message-history accessor across 1.20.2 but changed the
 * return type from List to class_8623, which IS an AbstractList -- so the old
 * descriptor is satisfied by simply returning the new value.
 *
 * Four models called this unbridgeable across six retry rounds and I accepted
 * that verdict; re-reading the stub by hand showed the delegation is one line.
 */
@Mixin(class_338.class)
public abstract class ChatHudBridge {

    public java.util.List<String> method_1809() {
        return ((class_338) (Object) this).method_1809();
    }
}
