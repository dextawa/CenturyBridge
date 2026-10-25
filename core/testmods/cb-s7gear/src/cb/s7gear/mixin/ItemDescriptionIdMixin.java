package cb.s7gear.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on Item.getDescriptionId(stack): sticks are named cb.s7gear.stick (GearTestMod.nameReads) */
@Mixin(class_1792.class)
public abstract class ItemDescriptionIdMixin {

    @Inject(method = "method_7866(Lnet/minecraft/class_1799;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$stickName(class_1799 stack, CallbackInfoReturnable<String> cir) {
        if ("stick".equals(class_7923.field_41178.method_10221((class_1792) (Object) this).method_12832())) {
            cir.setReturnValue("cb.s7gear.stick");
        }
    }
}
