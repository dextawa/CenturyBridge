package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ShapedRecipePattern's pattern data, a record component in 1.20.6 and a private field without accessor in 1.21 */
@Mixin(net.minecraft.class_8957.class)
public interface CbItemShapedPatternAccessor {

    @Accessor("field_51640")
    java.util.Optional<net.minecraft.class_8957.class_8958> cbItem$data();
}
