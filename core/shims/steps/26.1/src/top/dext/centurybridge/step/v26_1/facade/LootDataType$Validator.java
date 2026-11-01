package top.dext.centurybridge.step.v26_1.facade;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.ValidationContext;

/**
 * 1.21.11's LootDataType.Validator (net/minecraft/class_8490$class_8491), gone in 26.1, whose LootDataType holds a
 * ContextGetter (the key set to validate a value against) and validates every value itself. 1.21.11 code building a
 * LootDataType with one of these gets a 26.1 type through rt/CbWallLoot, which keeps the validator and runs it where
 * 1.21.11 ran it.
 */
@FunctionalInterface
public interface LootDataType$Validator<T> {

    /** 1.21.11 run(context, key, value): validate value, reported under the context's reporter */
    void run(ValidationContext context, ResourceKey<T> key, T value);
}
