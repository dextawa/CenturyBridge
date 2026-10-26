package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import top.dext.centurybridge.step.v1_20_3.facade.class_2991$class_6346;
import top.dext.centurybridge.step.v1_20_3.rt.CbFunctions;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * ServerFunctionManager's 1.20.2 members on 1.20.3's execution ({@link CbFunctions}): getCommandLimit(),
 * and execute(function, source[, tracer, arguments]) answering how many commands ran (1.20.3's answers
 * nothing). The two-argument execute goes through the four-argument one, as 1.20.2's did.
 */
@Mixin(value = net.minecraft.class_2991.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbServerFunctionManagerMixin {

    @Shadow
    @Final
    private net.minecraft.server.MinecraftServer field_13419;

    /** getCommandLimit(), 1.20.2's: the maxCommandChainLength game rule */
    public int method_12902() {
        return field_13419.method_3767().method_8356(net.minecraft.class_1928.field_19408);
    }

    /** execute(function, source), 1.20.2's: without a tracer or arguments; 0 when the function cannot be instantiated */
    public int method_12904(net.minecraft.class_2158<net.minecraft.class_2168> function, net.minecraft.class_2168 source) {
        try {
            return method_36341(function, source, null, null);
        } catch (net.minecraft.class_8643 e) {
            return 0;
        }
    }

    public int method_36341(net.minecraft.class_2158<net.minecraft.class_2168> function, net.minecraft.class_2168 source,
                            class_2991$class_6346 tracer, net.minecraft.class_2487 arguments) throws net.minecraft.class_8643 {
        return CbFunctions.execute((net.minecraft.class_2991) (Object) this, function, source, tracer, arguments);
    }
}
