package top.dext.centurybridge.step.v26_1.mixin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryMap;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemorySlot;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Brain's protected constructor and its memory slots and sensors, for rt/CbWallBrain (1.21.11's public
 * Brain constructor, Brain.codec, copyWithoutBehaviors).
 */
@Mixin(Brain.class)
public interface CbWallBrainAccess {

    @Invoker("<init>")
    @SuppressWarnings("rawtypes")
    static Brain cb$new(Collection<? extends MemoryModuleType<?>> memoryTypes, Collection sensorTypes, List activities,
            MemoryMap memories, RandomSource random) {
        throw new AssertionError();
    }

    @Accessor("memories")
    Map<MemoryModuleType<?>, MemorySlot<?>> cb$memories();

    @Accessor("sensors")
    @SuppressWarnings("rawtypes")
    Map<SensorType, Sensor> cb$sensors();
}
