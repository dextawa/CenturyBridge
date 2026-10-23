package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1865;
import net.minecraft.class_3956;
import net.minecraft.class_3972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemRecipeClasses;

/**
 * The recipe type (and for 1.21.1's SingleItemRecipe the serializer) a converted 1.21.1 AbstractCookingRecipe /
 * SingleItemRecipe subclass passed its super constructor: 1.21.2's constructors take neither (the class answers
 * getType() / getSerializer() itself), 1.21.1's answered them from these fields (CbItemRecipeClasses). 1.21.2's
 * AbstractCookingRecipe extends SingleItemRecipe, so this one class carries them for both.
 */
@Mixin(class_3972.class)
public abstract class CbItemSingleRecipeFieldsMixin implements CbItemRecipeClasses.LegacyFields {

    @Unique
    private class_3956<?> cb$type;

    @Unique
    private class_1865<?> cb$serializer;

    @Override
    public class_3956<?> cb$type() {
        return cb$type;
    }

    @Override
    public void cb$setType(class_3956<?> type) {
        this.cb$type = type;
    }

    @Override
    public class_1865<?> cb$serializer() {
        return cb$serializer;
    }

    @Override
    public void cb$setSerializer(class_1865<?> serializer) {
        this.cb$serializer = serializer;
    }
}
