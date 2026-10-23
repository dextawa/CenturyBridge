package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1796;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** ItemCooldowns' protected onCooldownStarted / onCooldownEnded, for 1.21.1's per-item calls (CbItemCooldowns) */
@Mixin(class_1796.class)
public interface CbItemCooldownsInvoker {

    @Invoker("method_7902")
    void cb$onCooldownStarted(class_2960 group, int ticks);

    @Invoker("method_7901")
    void cb$onCooldownEnded(class_2960 group);
}
