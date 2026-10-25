package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_11368;
import net.minecraft.class_11372;
import net.minecraft.class_2487;
import net.minecraft.class_3222;
import net.minecraft.class_3441;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbRecipeBooks;
import top.dext.centurybridge.step.v1_21_6.rt.CbValueIo;

/**
 * Routes 1.21.6's player save and load past the restored 1.21.5 recipe book members (CbRecipeBookRestoredMixin),
 * unchanged unless a converted mod changed them. 1.21.5's ServerPlayer stored recipeBook.toNbt() as "recipeBook" and
 * read a fresh player's book with fromNbt(tag.getCompoundOrEmpty("recipeBook"), recipe exists); 1.21.6 stores and
 * reads ServerRecipeBook.Packed through its codec (javap). Both of 1.21.6's paths run as they are (a native 1.21.6
 * mod's mixin on them still applies); after each, at TAIL, the restored member is asked next to its unmixable copy
 * (CbRecipeBooks): only when the two answer differently does 1.21.5's answer replace 1.21.6's -- the restored tag
 * stored as "recipeBook" (with whatever a mod's mixin put in it), or the player's book reset and read by the
 * restored fromNbt.
 */
@Mixin(class_3222.class)
public abstract class CbRecipeBookRouteMixin {

    @Inject(method = "method_5652(Lnet/minecraft/class_11372;)V", at = @At("TAIL"))
    private void cb$legacyRecipeBookSave(class_11372 output, CallbackInfo ci) {
        class_3441 book = ((class_3222) (Object) this).method_14253();
        class_2487 legacy = ((CbRecipeBooks.LegacyBook) book).method_14902();
        if (!legacy.equals(CbRecipeBooks.toTagCopy(book))) {
            CbValueIo.tag(output).method_10566("recipeBook", legacy);
        }
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_11368;)V", at = @At("TAIL"))
    private void cb$legacyRecipeBookLoad(class_11368 input, CallbackInfo ci) {
        class_3222 self = (class_3222) (Object) this;
        class_2487 tag = CbValueIo.tag(input).method_68568("recipeBook");
        java.util.function.Predicate<net.minecraft.class_5321<net.minecraft.class_1860<?>>> recognized =
            key -> self.method_5682().method_3772().method_8130(key).isPresent();
        class_3441 mixed = new class_3441((key, displays) -> { });
        class_3441 plain = new class_3441((key, displays) -> { });
        ((CbRecipeBooks.LegacyBook) mixed).method_14901(tag, recognized);
        CbRecipeBooks.fromTagCopy(plain, tag, recognized);
        if (!CbRecipeBooks.sameState(mixed, plain)) {
            class_3441 book = self.method_14253();
            CbRecipeBooks.reset(book);
            ((CbRecipeBooks.LegacyBook) book).method_14901(tag, recognized);
        }
    }
}
