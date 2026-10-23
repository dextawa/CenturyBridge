package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.fabric.api.event.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.registry.RegistryEntryRemovedCallback;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.registry.CbFapiRegistries;

/**
 * SimpleRegistry.add (method_10272) of an id the registry already holds with another object: 1.20.4's
 * Fabric API told RegistryEntryRemovedCallback listeners of the object giving way (from SimpleRegistry.set,
 * which 1.20.5 no longer has), with its raw id. Only registries a 1.20.4 listener watches are looked at.
 */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbFapiRegistryRemovalMixin<T> {

    @Inject(method = "method_10272", at = @At("HEAD"))
    private void cb$entryRemoved(net.minecraft.class_5321<T> key, T value, net.minecraft.class_9248 info,
                                 CallbackInfoReturnable<net.minecraft.class_6880.class_6883<T>> cir) {
        @SuppressWarnings("unchecked")
        net.minecraft.class_2378<T> self = (net.minecraft.class_2378<T>) (Object) this;
        Event<RegistryEntryRemovedCallback<T>> event = CbFapiRegistries.removedEventIfAny(self);
        if (event == null || key == null || !self.method_10250(key.method_29177())) {
            return;
        }
        T old = self.method_10223(key.method_29177());
        if (old != null && old != value) {
            event.invoker().onEntryRemoved(self.method_10206(old), key.method_29177(), old);
        }
    }
}
