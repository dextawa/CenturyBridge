package cb.swall.mixin;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.21.11 Villager's private MEMORY_TYPES (class_1646.field_18538), read and replaced through a @Mutable accessor as
 * 1.21.11-era mods did. On 26.1 both go to the given-back list (fieldGetters / staticFieldSetters, rt/brainlists).
 */
@Mixin(net.minecraft.class_1646.class)
public interface VillagerMemoryAccessor {
    @Accessor(value = "field_18538", remap = false)
    static ImmutableList<net.minecraft.class_4140<?>> cbswall$memories() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor(value = "field_18538", remap = false)
    static void cbswall$setMemories(ImmutableList<net.minecraft.class_4140<?>> value) {
        throw new AssertionError();
    }
}
