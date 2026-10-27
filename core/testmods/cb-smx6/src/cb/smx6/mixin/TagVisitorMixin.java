package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod's mixins on TextComponentTagVisitor's private nested visit (method_59850(Tag, boolean)Component): a
 * cancellable HEAD that answers "<cb>" for the nested tag while Driver.foldNested is set, and an INVOKE-anchored injector on
 * its visit call (method_32305), counting nested visits.
 */
@Mixin(net.minecraft.class_5628.class)
public abstract class TagVisitorMixin {

    @Inject(method = "method_59850", at = @At("HEAD"), cancellable = true)
    private void cbsmx6$nested(net.minecraft.class_2520 tag, boolean indent, CallbackInfoReturnable<net.minecraft.class_2561> cir) {
        Driver.nestedAsked++;
        if (Driver.foldNested) {
            cir.setReturnValue(net.minecraft.class_2561.method_43470("<cb>"));
        }
    }

    @Inject(method = "method_59850", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5628;method_32305(Lnet/minecraft/class_2520;)Lnet/minecraft/class_2561;"))
    private void cbsmx6$visited(net.minecraft.class_2520 tag, boolean indent, CallbackInfoReturnable<net.minecraft.class_2561> cir) {
        Driver.nestedVisits++;
    }
}
