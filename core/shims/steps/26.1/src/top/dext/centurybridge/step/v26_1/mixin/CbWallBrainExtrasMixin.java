package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Collection;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallBrainExtras;

/**
 * The memories and sensors 1.21.11 mods added to a mob's SENSOR_TYPES / MEMORY_TYPES, added to the ones 26.1's
 * Brain.Provider.makeBrain passes to Brain.<init> for that mob (rt/CbWallBrainExtras): two @ModifyArg on the
 * constructor call, a union with what they are handed (26.1's own, or what a native mixin on the same call made of
 * it), never a replacement. The entity is taken at makeBrain's head.
 */
@Mixin(Brain.Provider.class)
public abstract class CbWallBrainExtrasMixin {

    @Inject(method = "makeBrain", at = @At("HEAD"))
    private void cb$building(LivingEntity entity, Brain.Packed packed, CallbackInfoReturnable<Brain<?>> cir) {
        CbWallBrainExtras.building(entity);
    }

    @ModifyArg(method = "makeBrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;<init>(Ljava/util/Collection;Ljava/util/Collection;Ljava/util/List;Lnet/minecraft/world/entity/ai/memory/MemoryMap;Lnet/minecraft/util/RandomSource;)V"), index = 0)
    private Collection<?> cb$memories(Collection<?> memories) {
        return CbWallBrainExtras.memories(memories);
    }

    @ModifyArg(method = "makeBrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;<init>(Ljava/util/Collection;Ljava/util/Collection;Ljava/util/List;Lnet/minecraft/world/entity/ai/memory/MemoryMap;Lnet/minecraft/util/RandomSource;)V"), index = 1)
    private Collection<?> cb$sensors(Collection<?> sensors) {
        return CbWallBrainExtras.sensors(sensors);
    }
}
