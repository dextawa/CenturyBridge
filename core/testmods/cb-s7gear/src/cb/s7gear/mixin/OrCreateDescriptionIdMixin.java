package cb.s7gear.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.1 mod mixin on Item.getOrCreateDescriptionId(): beetroot's description id is cb.s7gear.beetroot (GearTestMod.nameReads) */
@Mixin(class_1792.class)
public abstract class OrCreateDescriptionIdMixin {

    @Inject(method = "method_7869()Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void cbs7gear$beetroot(CallbackInfoReturnable<String> cir) {
        if ("beetroot".equals(class_7923.field_41178.method_10221((class_1792) (Object) this).method_12832())) {
            cir.setReturnValue("cb.s7gear.beetroot");
        }
    }
}
