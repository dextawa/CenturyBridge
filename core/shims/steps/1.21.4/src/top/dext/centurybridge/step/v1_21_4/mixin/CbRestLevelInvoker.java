package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_5577;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Level.getEntities() (protected): the dragon parts a 1.21.3 level subclass holds (CbRestLevel). */
@Mixin(class_1937.class)
public interface CbRestLevelInvoker {
    @Invoker("method_31592")
    class_5577<class_1297> cb$entities();
}
