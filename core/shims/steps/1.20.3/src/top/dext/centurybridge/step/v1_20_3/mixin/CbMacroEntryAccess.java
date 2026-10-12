package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** MacroFunction.MacroEntry's template (a package-private class: named by string), for 1.20.2's MacroEntry of a macro line */
@Mixin(targets = "net/minecraft/class_8641$class_8870")
public interface CbMacroEntryAccess {

    /** template */
    @Accessor("field_46760")
    net.minecraft.class_8873 cb$template();
}
