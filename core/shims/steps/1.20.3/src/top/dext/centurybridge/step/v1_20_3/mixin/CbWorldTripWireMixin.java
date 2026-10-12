package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2537;
import net.minecraft.class_2538;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldTripWireHook;

/** TripWireBlock updates its hooks through the 1.20.3 static calculateState: a 1.20.2 hook's override as in CbWorldTripWireHookMixin */
@Mixin(class_2538.class)
public abstract class CbWorldTripWireMixin {

    @Redirect(method = "method_10779", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2537;method_10776(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;ZZILnet/minecraft/class_2680;)V"))
    private void cb$legacyCalculateState(class_1937 level, class_2338 pos, class_2680 state, boolean attaching,
                                         boolean shouldNotifyNeighbours, int searchRange, class_2680 tripwire) {
        if (state.method_26204() instanceof CbWorldTripWireHook hook) {
            hook.method_10776(level, pos, state, attaching, shouldNotifyNeighbours, searchRange, tripwire);
        } else {
            class_2537.method_10776(level, pos, state, attaching, shouldNotifyNeighbours, searchRange, tripwire);
        }
    }
}
