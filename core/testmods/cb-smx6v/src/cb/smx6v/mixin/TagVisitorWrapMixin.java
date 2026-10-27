package cb.smx6v.mixin;

import cb.smx6v.Driver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * A native 1.21 mod's @WrapOperation on visitCompound's nested visit (class_5628.method_32292 -> method_60585), a call the
 * 1.21 step layer routes (CbNbtTagVisitorRestoredMixinHooks wraps it). Counts and passes on.
 */
@Mixin(net.minecraft.class_5628.class)
public abstract class TagVisitorWrapMixin {

    @WrapOperation(method = "method_32292(Lnet/minecraft/class_2487;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5628;method_60585(Lnet/minecraft/class_2520;Z)V"))
    private void cbsmx6v$nested(net.minecraft.class_5628 self, net.minecraft.class_2520 tag, boolean indent, Operation<Void> original) {
        Driver.nativeNestedWraps++;
        original.call(self, tag, indent);
    }
}
