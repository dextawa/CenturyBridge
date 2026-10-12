package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** MacroFunction.PlainTextEntry's compiled line (a package-private class: named by string) */
@Mixin(targets = "net/minecraft/class_8641$class_8871")
public interface CbPlainTextEntryAccess {

    /** compiledAction */
    @Accessor("field_46762")
    net.minecraft.class_8856<net.minecraft.class_2168> cb$action();
}
