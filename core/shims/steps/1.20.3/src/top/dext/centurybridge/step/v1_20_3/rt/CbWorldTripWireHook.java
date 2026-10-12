package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

/**
 * 1.20.2's TripWireHookBlock.calculateState was an instance method mods overrode; 1.20.3's is static, so an
 * override is never called. The converter adds this interface to a 1.20.2 hook that overrides it (its own
 * method implements it); CbWorldTripWireHookMixin / CbWorldTripWireMixin route 1.20.3's calls for such a hook
 * to the override, as 1.20.2's virtual call did.
 */
public interface CbWorldTripWireHook {
    void method_10776(class_1937 level, class_2338 pos, class_2680 state, boolean attaching, boolean shouldNotifyNeighbours,
                      int searchRange, class_2680 tripwire);
}
