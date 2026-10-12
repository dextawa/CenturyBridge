package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** LevelStorageAccess's private checkLock(), which 1.20.2's readers ran first (CbWorldStorage) */
@Mixin(class_32.class_5143.class)
public interface CbWorldLevelStorageAccessInvoker {

    @Invoker("method_27017")
    void cb$checkLock();
}
