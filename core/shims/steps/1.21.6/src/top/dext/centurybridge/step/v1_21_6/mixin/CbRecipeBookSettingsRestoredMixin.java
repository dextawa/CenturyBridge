package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_2487;
import net.minecraft.class_5411;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_6.rt.CbRecipeBooks;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's RecipeBookSettings.write(CompoundTag), restored on RecipeBookSettings with 1.21.5's body (CbRecipeBooks):
 * the restored ServerRecipeBook.toNbt calls it, as 1.21.5's did. Priority below every mod's. The layer's own reads
 * (CbRecipeBooks.LegacySettings: the same tag from the four per-type fields, and a fresh book's all-false settings)
 * touch the fields directly, so the route adds no call a mod could mix.
 */
@Mixin(value = class_5411.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeBookSettingsRestoredMixin implements CbRecipeBooks.LegacySettings {

    /** the four types' settings, in RecipeBookType order (crafting, furnace, blast furnace, smoker; javap) */
    @Shadow
    private class_5411.class_5412 field_60336;

    @Shadow
    private class_5411.class_5412 field_60337;

    @Shadow
    private class_5411.class_5412 field_60338;

    @Shadow
    private class_5411.class_5412 field_60339;

    @CbVanillaReads("CbRecipeBookRouteMixin: through the restored ServerRecipeBook.toNbt, which ServerPlayer's save asks")
    @Override
    public void method_30189(class_2487 tag) {
        CbRecipeBooks.writeSettings((class_5411) (Object) this, tag);
    }

    @Override
    public void cb$writeCopy(class_2487 tag) {
        class_5411.class_5412[] all = {field_60336, field_60337, field_60338, field_60339};
        for (int i = 0; i < all.length; i++) {
            tag.method_10556(CbRecipeBooks.KEYS[i][0], all[i].comp_3247());
            tag.method_10556(CbRecipeBooks.KEYS[i][1], all[i].comp_3248());
        }
    }

    @Override
    public void cb$clear() {
        field_60336 = field_60337 = field_60338 = field_60339 = class_5411.class_5412.field_54549;
    }
}
