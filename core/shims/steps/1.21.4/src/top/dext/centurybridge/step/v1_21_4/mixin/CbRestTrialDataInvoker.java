package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1952;
import net.minecraft.class_5819;
import net.minecraft.class_8963;
import net.minecraft.class_8966;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** TrialSpawnerData.getOrCreateNextSpawnData (protected); 1.21.3's setEntityId wrote into it (CbRestBlockEntities). */
@Mixin(class_8966.class)
public interface CbRestTrialDataInvoker {
    @Invoker("method_55188")
    class_1952 cb$getOrCreateNextSpawnData(class_8963 spawner, class_5819 random);
}
