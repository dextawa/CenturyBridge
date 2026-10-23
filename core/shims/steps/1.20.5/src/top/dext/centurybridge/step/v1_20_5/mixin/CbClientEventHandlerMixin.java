package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/** ContainerEventHandler.setInitialFocus(child): 1.20.4's default set the focus to the child, as it read */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4069.class)
public interface CbClientEventHandlerMixin {

    /** 1.20.4 ContainerEventHandler.setInitialFocus(child) */
    default void method_20086(net.minecraft.class_364 child) {
        ((net.minecraft.class_4069) this).method_25395(child);
    }
}
