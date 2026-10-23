package top.dext.centurybridge.step.v1_21.rt;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import net.minecraft.class_9331;
import net.minecraft.class_9336;

/**
 * Command arguments: the item an item argument names carries its components as a DataComponentPatch in 1.21
 * where 1.20.6 carried a DataComponentMap; an entity selector takes a list of predicates where 1.20.6 took one.
 * Wired from s6world.json.
 */
public final class CbWorldCommands {

    /**
     * The components of a 1.20.6 ItemInput / ItemResult as 1.21's patch. 1.20.6's createItemStack applied the
     * map onto a fresh stack of the item (ItemStack.applyComponents sets each component); a patch that sets
     * each component of the map makes 1.21's ItemStack(item, count, patch) the same stack.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_9326 patchOf(class_9323 components) {
        if (components == null) {
            return null;
        }
        class_9326.class_9327 patch = class_9326.method_57841();
        for (class_9336<?> component : components) {
            patch.method_57855(component);
        }
        return patch.method_57852();
    }

    /**
     * 1.20.6 ItemResult.components(): the components the parsed item sets. A 1.21 patch may also remove a
     * component of the item ("!name"), which a 1.20.6 component map has no way to say; the map carries the
     * components set.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_9323 mapOf(class_9326 patch) {
        if (patch == null) {
            return null;
        }
        class_9323.class_9324 map = class_9323.method_57827();
        for (Map.Entry<class_9331<?>, Optional<?>> e : patch.method_57846()) {
            if (e.getValue().isPresent()) {
                map.method_57840((class_9331) e.getKey(), e.getValue().get());
            }
        }
        return map.method_57838();
    }

    /** a 1.20.6 entity selector's one predicate as 1.21's list, which the selector tests entity by entity, all of them */
    public static List<Predicate<class_1297>> predicateList(Predicate<class_1297> predicate) {
        return predicate == null ? List.of() : List.of(predicate);
    }

    private CbWorldCommands() {
    }
}
