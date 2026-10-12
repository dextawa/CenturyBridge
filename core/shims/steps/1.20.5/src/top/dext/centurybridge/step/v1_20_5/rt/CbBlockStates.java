package top.dext.centurybridge.step.v1_20_5.rt;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Map;

/**
 * A state's property values on 1.20.5, which holds them in a Reference2ObjectArrayMap where
 * 1.20.4 held an ImmutableMap (StateHolder, BlockState, FluidState, StateDefinition.Factory): the
 * same entries in the same order either way. A mod constructing or making states passes its
 * ImmutableMap's entries on; a mod reading a state's values gets them as the ImmutableMap 1.20.4
 * answered (the map the state holds, unchanged when it already is one).
 */
public final class CbBlockStates {

    /** the 1.20.4 StateDefinition.Factory a mod's lambda or method reference implements (lambda adapter) */
    @FunctionalInterface
    public interface LegacyFactory {
        Object create(Object owner, ImmutableMap<?, ?> values, com.mojang.serialization.MapCodec<?> propertiesCodec);
    }

    /** ImmutableMap -> the Reference2ObjectArrayMap 1.20.5 builds a state from (StateHolder / BlockState / FluidState constructors, Factory.create) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Reference2ObjectArrayMap values(ImmutableMap values) {
        return values == null ? null : new Reference2ObjectArrayMap(values);
    }

    /** a state's values as 1.20.4's ImmutableMap (StateHolder.getValues(), a mod Factory's create) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ImmutableMap immutable(Map values) {
        return values == null || values instanceof ImmutableMap ? (ImmutableMap) values : ImmutableMap.copyOf(values);
    }

    /** the values 1.20.5 hands a mod's own Factory implementation, as 1.20.4's ImmutableMap (override hook wrap) */
    @SuppressWarnings("rawtypes")
    public static ImmutableMap fromState(Reference2ObjectArrayMap values) {
        return immutable(values);
    }

    /** a mod's 1.20.4 factory as 1.20.5's (lambdaAdapters): 1.20.5 hands it the values it builds, the mod gets them as 1.20.4's map */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2689.class_2691 factory(LegacyFactory legacy) {
        return (owner, values, codec) -> legacy.create(owner, immutable(values), codec);
    }

    private CbBlockStates() {
    }
}
