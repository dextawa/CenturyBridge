package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Mob members 1.21.2 keeps protected that the layer's 1.21.1 shapes call (virtually, so overrides answer). */
@Mixin(class_1308.class)
public interface CbEntityMobInvoker {

    @Invoker("method_5955")
    boolean cb$canReplaceCurrentItem(class_1799 candidate, class_1799 existing, class_1304 slot);
}
