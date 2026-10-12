package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2537;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldTripWireHook;

/**
 * TripWireHookBlock's own updates (placement, scheduled tick, removal) call the 1.20.3 static calculateState;
 * 1.20.2 called the instance method, which a hook subclass could override. A 1.20.2 hook that does
 * (CbWorldTripWireHook) is called again; any other hook gets the 1.20.3 static.
 */
@Mixin(class_2537.class)
public abstract class CbWorldTripWireHookMixin {

    @Redirect(method = {"method_9567", "method_9588", "method_9536"}, at = @At(value = "INVOKE",
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
