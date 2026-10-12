package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** MacroFunction's lines, for 1.20.2's getEntries() of a macro function */
@Mixin(net.minecraft.class_8641.class)
public interface CbMacroFunctionAccess {

    /** entries */
    @Accessor("field_46759")
    java.util.List<?> cb$entries();
}
