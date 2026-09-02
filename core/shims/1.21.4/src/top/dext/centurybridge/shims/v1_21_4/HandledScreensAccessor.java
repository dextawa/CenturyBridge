package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_1703;
import net.minecraft.class_3917;
import net.minecraft.class_3929;
import net.minecraft.class_3936;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** HandledScreens.register is private; the 1.20.1 fapi ScreenRegistry reached it the same way (see CbScreenRegistry). */
@Mixin(class_3929.class)
public interface HandledScreensAccessor {

    @Invoker("method_17542")
    static <M extends class_1703, U extends class_437 & class_3936<M>> void cbRegister(
            class_3917<? extends M> type, class_3929.class_3930<M, U> provider) {
        throw new AssertionError("mixin invoker not applied");
    }
}
