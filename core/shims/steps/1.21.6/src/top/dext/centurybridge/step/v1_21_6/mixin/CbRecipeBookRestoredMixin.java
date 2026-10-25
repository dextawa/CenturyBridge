package top.dext.centurybridge.step.v1_21_6.mixin;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_1860;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_3439;
import net.minecraft.class_3441;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_6.rt.CbRecipeBooks;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's ServerRecipeBook.toNbt() / fromNbt(CompoundTag, Predicate) and its private KEY list codec (field_56611),
 * restored on ServerRecipeBook with 1.21.5's bodies, instruction for instruction where 1.21.6 has the members (javap):
 * a 1.21.5 call or super call links to them, a 1.21.5 subclass overrides them, a 1.21.5 mixin anchors in them. Two
 * calls name what 1.21.6 dropped and go through the layer instead: RecipeBookSettings.write (restored by
 * CbRecipeBookSettingsRestoredMixin, reached through its interface) and the static RecipeBookSettings.read
 * (CbRecipeBooks). Priority below every mod's (CbVanillaReads.PRIORITY).
 */
@Mixin(value = class_3441.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRecipeBookRestoredMixin extends class_3439 implements CbRecipeBooks.LegacyBook {

    private static final Codec<List<class_5321<class_1860<?>>>> field_56611 = class_1860.field_56667.listOf();

    @Shadow
    @Final
    protected Set<class_5321<class_1860<?>>> field_54550;

    @Shadow
    @Final
    protected Set<class_5321<class_1860<?>>> field_54551;

    @Shadow
    public abstract void method_64588(class_5321<class_1860<?>> key);

    @Shadow
    private void method_64596(class_5321<class_1860<?>> key) {
        throw new AssertionError();
    }

    @Shadow
    private void method_20732(List<class_5321<class_1860<?>>> keys, Consumer<class_5321<class_1860<?>>> add,
            Predicate<class_5321<class_1860<?>>> isRecognized) {
        throw new AssertionError();
    }

    @CbVanillaReads("CbRecipeBookRouteMixin: ServerPlayer.addAdditionalSaveData asks it after 1.21.6's save and stores its "
        + "tag as \"recipeBook\" when it answers otherwise than the unmixable copy (CbRecipeBooks.toTagCopy)")
    @Override
    public class_2487 method_14902() {
        class_2487 tag = new class_2487();
        ((CbRecipeBooks.LegacySettings) (Object) this.method_30173()).method_30189(tag);
        class_2499 recipes = new class_2499();
        for (class_5321<class_1860<?>> key : this.field_54550) {
            recipes.add(class_2519.method_23256(key.method_29177().toString()));
        }
        tag.method_10566("recipes", recipes);
        class_2499 shown = new class_2499();
        for (class_5321<class_1860<?>> key : this.field_54551) {
            shown.add(class_2519.method_23256(key.method_29177().toString()));
        }
        tag.method_10566("toBeDisplayed", shown);
        return tag;
    }

    @CbVanillaReads("CbRecipeBookRouteMixin: ServerPlayer.readAdditionalSaveData has it read the saved \"recipeBook\" tag "
        + "into a fresh book next to the unmixable copy (CbRecipeBooks.fromTagCopy); when the two books differ, the "
        + "player's book is reset and read by it, as 1.21.5 read a fresh player's book")
    @Override
    public void method_14901(class_2487 tag, Predicate<class_5321<class_1860<?>>> isRecognized) {
        this.method_30174(CbRecipeBooks.method_30183(tag));
        List<class_5321<class_1860<?>>> recipes = tag.method_67491("recipes", field_56611).orElse(List.of());
        this.method_20732(recipes, this::method_64588, isRecognized);
        List<class_5321<class_1860<?>>> shown = tag.method_67491("toBeDisplayed", field_56611).orElse(List.of());
        this.method_20732(shown, this::method_64596, isRecognized);
    }
}
