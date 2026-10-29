package cb.swalln.mixin;

import cb.swalln.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.1's Brain.Provider.makeBrain, mixed at the point the 26.1 step layer's CbWallBrainExtrasMixin also mixes: a
 * @ModifyArg on the Brain.<init> call adds LIKED_NOTEBLOCK_POSITION to a piglin's memories. With the layer on it must
 * still act (Driver "native.brainProviderArg"), and what the layer adds must not take it away.
 */
@Mixin(Brain.Provider.class)
public abstract class BrainProviderNativeMixin {
    @Inject(method = "makeBrain", remap = false, at = @At("HEAD"))
    private void cbswalln$building(LivingEntity entity, Brain.Packed packed, CallbackInfoReturnable<Brain<?>> cir) {
        Driver.BUILDING.set(entity);
    }

    @ModifyArg(method = "makeBrain", remap = false, index = 0, at = @At(value = "INVOKE", remap = false,
        target = "Lnet/minecraft/world/entity/ai/Brain;<init>(Ljava/util/Collection;Ljava/util/Collection;Ljava/util/List;Lnet/minecraft/world/entity/ai/memory/MemoryMap;Lnet/minecraft/util/RandomSource;)V"))
    private Collection<?> cbswalln$memories(Collection<?> memories) {
        if (!(Driver.BUILDING.get() instanceof Piglin)) {
            return memories;
        }
        List<Object> out = new ArrayList<>(memories);
        out.add(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        return out;
    }
}
