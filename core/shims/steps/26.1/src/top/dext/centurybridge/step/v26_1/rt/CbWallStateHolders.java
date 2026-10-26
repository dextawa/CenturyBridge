package top.dext.centurybridge.step.v26_1.rt;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * 1.21.11's StateHolder.populateNeighbours(statesByValues) on 26.1. 1.21.11 kept, per property, the state for
 * each of the property's values, looked up in the map of every state by its property values (javap
 * class_2688.method_28496: the values with that property changed, in getPossibleValues order), and threw
 * IllegalStateException when the neighbours were already set. 26.1 keeps the same table as an array,
 * neighbors[property index][property.getInternalIndex(value)], given once through initializeNeighbors (which
 * throws the same way). The table is built from the map as 1.21.11 built it and handed to initializeNeighbors
 * (the layer's access widener opens it and the state's property keys and values).
 */
public final class CbWallStateHolders {
    private CbWallStateHolders() {
    }

    /** 1.21.11's populateNeighbours(map), declared by a mod's state subclass (adapterHooks) */
    public interface LegacyNeighbours {
        void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, ?> statesByValues);
    }

    /** a mod's call of populateNeighbours(map): a 1.21.11 override's answer, else 1.21.11's body */
    public static void populateNeighbours(StateHolder<?, ?> state, Map<Map<Property<?>, Comparable<?>>, ?> statesByValues) {
        if (state instanceof LegacyNeighbours legacy) {
            legacy.populateNeighbours(statesByValues);
            return;
        }
        superPopulateNeighbours(state, statesByValues);
    }

    /** 1.21.11's populateNeighbours body, into 26.1's neighbour table */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void superPopulateNeighbours(StateHolder<?, ?> state, Map<Map<Property<?>, Comparable<?>>, ?> statesByValues) {
        Property<?>[] keys = state.propertyKeys;
        Comparable<?>[] values = state.propertyValues;
        Object[][] neighbors = new StateHolder[keys.length][];
        for (int i = 0; i < keys.length; i++) {
            Property property = keys[i];
            List<?> possible = property.getPossibleValues();
            Object[] row = new StateHolder[possible.size()];
            for (Object value : possible) {
                Reference2ObjectArrayMap<Property<?>, Comparable<?>> key = new Reference2ObjectArrayMap<>(keys.length);
                for (int k = 0; k < keys.length; k++) {
                    key.put(keys[k], k == i ? (Comparable<?>) value : values[k]);
                }
                row[property.getInternalIndex((Comparable) value)] = statesByValues.get(key);
            }
            neighbors[i] = row;
        }
        ((StateHolder) state).initializeNeighbors(neighbors);
    }
}
