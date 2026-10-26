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
 * reads ServerRecipeBook.Packed through its codec (javap; the same keys). Both of 1.21.6's paths run as they are (a
 * native 1.21.6 mod's mixins on them and on pack / loadUntrusted still apply, once each); after each, at TAIL, the
 * restored member is asked only when a converted mod mixed it (CbMixedMembers, the conversion's signal) or the book is
 * a subclass (CbRecipeBooks.routeSave / routeLoad) -- otherwise nothing more runs. Save: the restored tag replaces
 * 1.21.6's "recipeBook" when it differs from the book's own state written the 1.21.5 way (LegacyBook.cb$tagCopy,
 * read from the fields). Load: the player's book is reset to a fresh book's state and read by the restored fromNbt.
 */
@Mixin(class_3222.class)
public abstract class CbRecipeBookRouteMixin {

    @Inject(method = "method_5652(Lnet/minecraft/class_11372;)V", at = @At("TAIL"))
    private void cb$legacyRecipeBookSave(class_11372 output, CallbackInfo ci) {
        class_3441 book = ((class_3222) (Object) this).method_14253();
        if (!CbRecipeBooks.routeSave(book)) {
            return;
        }
        CbRecipeBooks.LegacyBook legacy = (CbRecipeBooks.LegacyBook) book;
        class_2487 tag = legacy.method_14902();
        if (!tag.equals(legacy.cb$tagCopy())) {
            CbValueIo.tag(output).method_10566("recipeBook", tag);
        }
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_11368;)V", at = @At("TAIL"))
    private void cb$legacyRecipeBookLoad(class_11368 input, CallbackInfo ci) {
        class_3222 self = (class_3222) (Object) this;
        class_3441 book = self.method_14253();
        if (!CbRecipeBooks.routeLoad(book)) {
            return;
        }
        class_2487 tag = CbValueIo.tag(input).method_68568("recipeBook");
        CbRecipeBooks.LegacyBook legacy = (CbRecipeBooks.LegacyBook) book;
        legacy.cb$reset();
        legacy.method_14901(tag, key -> self.method_5682().method_3772().method_8130(key).isPresent());
    }
}
