package cb.s6b.mixin;

import java.util.function.Predicate;
import net.minecraft.class_2487;
import net.minecraft.class_3441;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a 1.21.5 mod's mixin on ServerRecipeBook.toNbt / fromNbt: extra data in the recipe book tag */
@Mixin(class_3441.class)
public abstract class S6bRecipeBookMixin {

    @Inject(method = "method_14902()Lnet/minecraft/class_2487;", at = @At("RETURN"))
    private void cbs6b$save(CallbackInfoReturnable<class_2487> cir) {
        cir.getReturnValue().method_10582("cb_s6b_extra", "kept");
    }

    @Inject(method = "method_14901(Lnet/minecraft/class_2487;Ljava/util/function/Predicate;)V", at = @At("TAIL"))
    private void cbs6b$load(class_2487 tag, Predicate<?> recognized, CallbackInfo ci) {
        cb.s6b.S6bTestMod.recipeExtra = tag.method_68564("cb_s6b_extra", "");
    }
}
