package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1917;
import net.minecraft.class_1937;
import net.minecraft.class_1952;
import net.minecraft.class_2338;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** BaseSpawner's private display entity and spawn data source, for 1.20.2's getOrCreateDisplayEntity (CbWorldEntities) */
@Mixin(class_1917.class)
public interface CbWorldBaseSpawnerAccess {

    @Accessor("field_9153")
    class_1297 cb$displayEntity();

    @Accessor("field_9153")
    void cb$setDisplayEntity(class_1297 entity);

    @Invoker("method_46404")
    class_1952 cb$getOrCreateNextSpawnData(class_1937 level, class_5819 random, class_2338 pos);
}
