package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_2945;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityData;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetEntityDataBuilderAccess;

/**
 * Entity's constructor: 1.20.4 made the entity's SynchedEntityData before it called
 * defineSynchedData(), so a 1.20.4 body (reached through the override hook of the new
 * defineSynchedData(Builder)) defines into {@code this.entityData}. 1.20.5 assigns the field only
 * after defineSynchedData(builder) returns. Here it is assigned right before that call, to the data
 * object build() is going to answer (see {@link CbNetEntityData}); 1.20.5's own assignment after the
 * call stores the same object again.
 *
 * Scoped by the entity itself, not by a stack: a nested entity constructed inside a define, an old
 * body's super.defineSynchedData() (rewritten to the new shape with this entity's builder), and an
 * exception thrown and caught in between all leave each entity reading its own data object.
 */
@Mixin(class_1297.class)
public abstract class CbNetEntityDataConstructionBridge {

    @Shadow
    @Final
    @Mutable
    protected class_2945 field_6011;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1297;method_5693(Lnet/minecraft/class_2945$class_9222;)V"))
    private class_2945.class_9222 cb$openEntityData(class_2945.class_9222 builder) {
        this.field_6011 = ((CbNetEntityDataBuilderAccess) builder).cb$openView();
        return builder;
    }
}
