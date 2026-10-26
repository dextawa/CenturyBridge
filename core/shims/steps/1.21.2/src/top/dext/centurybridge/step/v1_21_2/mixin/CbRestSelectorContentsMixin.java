package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_2561;
import net.minecraft.class_2579;
import net.minecraft.class_5250;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SelectorContents.resolve of a 1.21.1-made selector text whose pattern did not parse: 1.21.1 kept the
 * pattern with a null selector and resolved it to empty text (resolve: "source == null || selector ==
 * null -> Component.empty()"). 1.21.2's contents always carry a parsed selector (its codec refuses the
 * rest), and its resolve would dereference the null one. Only CbRestCommands.newSelectorContents makes
 * such contents; for them resolve answers as 1.21.1's did.
 */
@Mixin(class_2579.class)
public abstract class CbRestSelectorContentsMixin {

    @Inject(method = "method_10890", at = @At("HEAD"), cancellable = true)
    private void cb$unparsedSelector(CallbackInfoReturnable<class_5250> cir) {
        if (((class_2579) (Object) this).comp_3080().comp_3068() == null) {
            cir.setReturnValue(class_2561.method_43473());
        }
    }
}
