package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.21.9's AbstractSelectionList.removeEntry / removeEntryFromTop (protected, answering nothing), for the restored 1.21.8 shapes */
@Mixin(class_350.class)
public interface CbSelectionListRemoveInvoker {

    @Invoker("method_25330")
    void cb$removeEntry(class_350.class_351<?> entry);

    @Invoker("method_44650")
    void cb$removeEntryFromTop(class_350.class_351<?> entry);
}
