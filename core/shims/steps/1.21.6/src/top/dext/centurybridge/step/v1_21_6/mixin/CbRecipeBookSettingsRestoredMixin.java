package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_2487;
import net.minecraft.class_5411;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbRecipeBooks;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's RecipeBookSettings.write(CompoundTag), restored on RecipeBookSettings with 1.21.5's body (CbRecipeBooks):
 * the restored ServerRecipeBook.toNbt calls it, as 1.21.5's did. Priority below every mod's.
 */
@Mixin(value = class_5411.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeBookSettingsRestoredMixin implements CbRecipeBooks.LegacySettings {

    @CbVanillaReads("CbRecipeBookRouteMixin: through the restored ServerRecipeBook.toNbt, which ServerPlayer's save asks")
    @Override
    public void method_30189(class_2487 tag) {
        CbRecipeBooks.writeSettings((class_5411) (Object) this, tag);
    }
}
