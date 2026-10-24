package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_4861;
import net.minecraft.class_8047;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_2.rt.CbItemCombiner;

/**
 * 1.21.1 ItemCombinerMenu on 1.21.2's (CbItemCombiner):
 * <ul>
 * <li>the slot definition 1.21.2's constructor takes is, for a menu whose class declares 1.21.1's
 *     createInputSlotDefinitions(), that method's answer -- asked where 1.21.1's constructor asked it, just after the
 *     player field is set; a 1.21.1 constructor call that passed {@link CbItemCombiner#DEFERRED} for a class without
 *     it is 1.21.1's AbstractMethodError. The definition is kept for 1.21.1's getSlotToQuickMoveTo.</li>
 * <li>quickMoveStack moving a stack into the input slots starts at the menu's own 1.21.1 getSlotToQuickMoveTo(stack)
 *     when its class declares one (1.21.2 starts at slot 0).</li>
 * </ul>
 */
@Mixin(class_4861.class)
public abstract class CbItemCombinerMenuMixin implements CbItemCombiner.Defined {

    @Unique
    private class_8047 cb$definition;

    @ModifyVariable(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/class_4861;field_22482:Lnet/minecraft/class_1657;",
        opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER), argsOnly = true)
    private class_8047 cb$legacyDefinition(class_8047 definition) {
        if ((Object) this instanceof CbItemCombiner.Legacy_createInputSlotDefinitions legacy) {
            definition = legacy.method_48352();
        } else if (definition == CbItemCombiner.DEFERRED) {
            throw new AbstractMethodError(getClass().getName() + " does not implement 1.21.1 "
                + "ItemCombinerMenu.createInputSlotDefinitions");
        }
        this.cb$definition = definition;
        return definition;
    }

    @Override
    public class_8047 cb$definition() {
        return this.cb$definition;
    }

    @ModifyArg(method = "method_7601(Lnet/minecraft/class_1657;I)Lnet/minecraft/class_1799;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4861;method_7616(Lnet/minecraft/class_1799;IIZ)Z", ordinal = 2),
        index = 1)
    private int cb$quickMoveStart(class_1799 stack, int start, int end, boolean reverse) {
        if ((Object) this instanceof CbItemCombiner.Legacy_getSlotToQuickMoveTo legacy) {
            return legacy.method_48357(stack);
        }
        return start;
    }
}
