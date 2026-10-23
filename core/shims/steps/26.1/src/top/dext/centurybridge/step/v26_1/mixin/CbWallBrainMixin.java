package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
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
 * 1.21.11 Brain members 26.1 dropped, given back on Brain itself (covers) so a mod's override and its
 * super call stay what they were:
 * <ul>
 * <li>getMemories(): the brain's memory map, each registered memory type to its value if it has one, as
 *     1.21.11 handed out its own map; 26.1 keeps a MemorySlot per type, and the map is a live view of
 *     them (rt/CbWallBrain.memories).</li>
 * <li>serializeStart(ops): the brain saved, 1.21.11's {"memories": ...} -- 26.1's Brain.Packed codec
 *     writes the same format from pack(). (26.1 saves through pack(); an override of serializeStart
 *     reaches it through the override hook, CbWallBrain.pack.)</li>
 * <li>copyWithoutBehaviors(): rt/CbWallBrain.copyWithoutBehaviors.</li>
 * </ul>
 */
@Mixin(Brain.class)
public abstract class CbWallBrainMixin {

    @Shadow
    @Final
    private Map<MemoryModuleType<?>, MemorySlot<?>> memories;

    @Shadow
    public abstract Brain.Packed pack();

    public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
        return CbWallBrain.memories(this.memories);
    }

    public <T> DataResult<T> serializeStart(DynamicOps<T> ops) {
        return Brain.Packed.CODEC.encodeStart(ops, this.pack());
    }

    public Brain<?> copyWithoutBehaviors() {
        return CbWallBrain.copyWithoutBehaviors((Brain<?>) (Object) this);
    }
}
