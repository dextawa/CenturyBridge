package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2401;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** LeverBlock members 1.20.6's pull body reaches that 1.21 keeps private. */
@Mixin(class_2401.class)
public interface CbWorldLeverInvoker {

    @Invoker("method_10309")
    void cb$updateNeighbours(class_2680 state, class_1937 level, class_2338 pos);
}
