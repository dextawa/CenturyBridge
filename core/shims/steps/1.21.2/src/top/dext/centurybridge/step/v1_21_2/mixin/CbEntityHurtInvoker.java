package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Entity's protected 1.21.2 members CbEntityHurt calls: isInvulnerableToBase (1.21.1's Entity.isInvulnerableTo body) and markHurt. */
@Mixin(class_1297.class)
public interface CbEntityHurtInvoker {

    @Invoker("method_64421")
    boolean cb$isInvulnerableToBase(class_1282 source);

    @Invoker("method_5785")
    void cb$markHurt();
}
