package cb.s9rn.mixin;

import cb.s9rn.Driver;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.class_332;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.9 mixins on the calls the layer's selection route modifies in AbstractSelectionList.renderItem */
@Mixin(class_350.class)
public abstract class ListRenderItemMixin {
    @ModifyExpressionValue(method = "method_44397(Lnet/minecraft/class_332;IIFLnet/minecraft/class_350$class_351;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_350;method_73379()Z"))
    private boolean cbs9rn$selectable(boolean original) {
        if (Driver.isTestList(this)) {
            Driver.NATIVE_SELECTABLE.incrementAndGet();
        }
        return original;
    }

    // the entry type is protected in 1.21.9: received as Object (@Coerce)
    @Inject(method = "method_44398(Lnet/minecraft/class_332;Lnet/minecraft/class_350$class_351;I)V", at = @At("HEAD"))
    private void cbs9rn$box(class_332 graphics, @Coerce Object entry, int color, CallbackInfo ci) {
        if (Driver.isTestList(this)) {
            Driver.BOXED.add(((class_350<?>) (Object) this).method_25396().indexOf(entry));
        }
    }
}
