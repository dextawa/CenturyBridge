package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_5712;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbGameEvents;

/**
 * A converted GameEvent subclass's super(name, radius) becomes super(radius) with the name
 * stashed; once GameEvent(radius) has returned, the event the name belongs to is known.
 */
@Mixin(class_5712.class)
public abstract class CbGameEventName {

    @Inject(method = "<init>(I)V", at = @At("RETURN"))
    private void cb$claimName(int radius, CallbackInfo ci) {
        CbGameEvents.claim((class_5712) (Object) this);
    }
}
