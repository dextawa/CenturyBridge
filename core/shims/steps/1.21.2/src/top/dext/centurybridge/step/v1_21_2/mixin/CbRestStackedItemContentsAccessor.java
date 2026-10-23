package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1662;
import net.minecraft.class_9875;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * StackedItemContents.raw (private final): the item counts a 1.21.2 StackedItemContents asks. Set to a 1.21.1
 * mod's StackedContents (1.21.2's StackedContents of item holders) so that 1.21.2's recipe matching reads the
 * counts the mod filled (rt/CbRestRecipeBooks).
 */
@Mixin(class_9875.class)
public interface CbRestStackedItemContentsAccessor {

    @Accessor("field_52506")
    class_1662<?> cb$raw();

    @Mutable
    @Accessor("field_52506")
    void cb$setRaw(class_1662<?> raw);
}
