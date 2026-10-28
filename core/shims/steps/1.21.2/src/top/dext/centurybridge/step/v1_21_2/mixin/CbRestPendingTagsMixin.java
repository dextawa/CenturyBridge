package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.facade.TagManager;

/**
 * MappedRegistry.prepareTagReload(result) returning: the tags TagLoader loaded for this registry, by the pending tags
 * made of them (facade/TagManager.loaded), for 1.21.1's TagManager results of the reload (CbRestTagResultsMixin). 1.21.2
 * binds the pending tags only after the reload; their holder sets cannot be read before (javap: MappedRegistry$3 keeps
 * the loaded map in a field of its own and hands it to the new named sets in apply()). Reads only.
 */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbRestPendingTagsMixin {

    @Inject(method = "method_62683(Lnet/minecraft/class_3503$class_6863;)Lnet/minecraft/class_2378$class_10106;", at = @At("RETURN"))
    @SuppressWarnings("rawtypes")
    private void cb$pendingTags(net.minecraft.class_3503.class_6863 result, CallbackInfoReturnable<net.minecraft.class_2378.class_10106> cir) {
        TagManager.loaded(cir.getReturnValue(), result);
    }
}
