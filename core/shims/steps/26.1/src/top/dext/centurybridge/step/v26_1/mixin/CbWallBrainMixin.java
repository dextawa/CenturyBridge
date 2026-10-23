package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Map;
import java.util.Optional;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemorySlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.facade.ExpirableValue;
import top.dext.centurybridge.step.v26_1.rt.CbWallBrain;

/**
 * 1.21.11's Brain.getMemories() (covers): the brain's memory map, each registered memory type to its
 * value if it has one, as 1.21.11 handed out its own map. 26.1 keeps a MemorySlot per type; the map is
 * a live view of them (rt/CbWallBrain.memories). Declared on Brain itself so a mod's override and its
 * super call stay what they were.
 */
@Mixin(Brain.class)
public abstract class CbWallBrainMixin {

    @Shadow
    @Final
    private Map<MemoryModuleType<?>, MemorySlot<?>> memories;

    public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
        return CbWallBrain.memories(this.memories);
    }
}
