package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Entity's protected checkSupportingBlock / checkFallDamage, for 1.21.3's ServerPlayer.doCheckFallDamage (CbRestPlayer). */
@Mixin(class_1297.class)
public interface CbRestEntityInvoker {
    @Invoker("method_51703")
    void cb$checkSupportingBlock(boolean onGround, class_243 movement);

    @Invoker("method_5623")
    void cb$checkFallDamage(double y, boolean onGround, class_2680 state, class_2338 pos);
}
