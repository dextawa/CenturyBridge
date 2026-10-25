package cb.s7gear.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_1812;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on PotionItem.getDescriptionId(stack): ".cbmixed" appended (GearTestMod.nameReads) */
@Mixin(class_1812.class)
public abstract class PotionDescriptionIdMixin {

    @Inject(method = "method_7866(Lnet/minecraft/class_1799;)Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void cbs7gear$mixed(class_1799 stack, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(cir.getReturnValue() + ".cbmixed");
    }
}
