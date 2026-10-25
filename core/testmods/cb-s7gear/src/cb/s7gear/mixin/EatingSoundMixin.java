package cb.s7gear.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on Item.getEatingSound(): cookies are eaten with an anvil's landing sound (GearTestMod.soundReads) */
@Mixin(class_1792.class)
public abstract class EatingSoundMixin {

    @Inject(method = "method_21830()Lnet/minecraft/class_3414;", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$cookieAnvil(CallbackInfoReturnable<class_3414> cir) {
        if ("cookie".equals(class_7923.field_41178.method_10221((class_1792) (Object) this).method_12832())) {
            cir.setReturnValue(class_7923.field_41172.method_10223(class_2960.method_60656("block.anvil.land")));
        }
    }
}
