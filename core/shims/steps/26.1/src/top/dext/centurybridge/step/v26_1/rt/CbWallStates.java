package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * 1.21.11's StateHolder construction on 26.1's. 1.21.11 built a state from its owner, its property values as a
 * Reference2ObjectArrayMap and the properties codec of its StateDefinition, which the state kept
 * (propertiesCodec) for StateHolder.codec; 26.1 builds it from the owner and parallel property / value arrays,
 * and the definition keeps the codec.
 * <ul>
 * <li>a 1.21.11 constructor call (new or super): the map's keys and values in its order, which is the order the
 *     definition handed them out in; the codec is the definition's, so it is not carried;</li>
 * <li>a 1.21.11 StateDefinition.Factory (a lambda, a method reference to a state constructor, or a class):
 *     26.1 calls create(owner, keys, values); the 1.21.11 factory takes them as the map and the definition's
 *     properties codec ({@link DefinitionCodec}, resolved when used: the definition is still being built when
 *     it makes its states);</li>
 * <li>getValues() (the map), propertiesCodec (the definition's), and StateHolder.codec(ownerCodec, defaultState)
 *     with 1.21.11's body.</li>
 * </ul>
 * The definition of a state is its owner's: a block's or a fluid's. A state of another owner type has no way
 * back to its definition on 26.1; its codec fails when used, saying so.
 */
public final class CbWallStates {

    private CbWallStates() {
    }

    /** 1.21.11's StateDefinition.Factory.create(owner, values, propertiesCodec) */
    @FunctionalInterface
    public interface LegacyFactory {
        Object create(Object owner, Reference2ObjectArrayMap values, MapCodec codec);
    }

    // ---- construction ----

    /** the keys of a 1.21.11 values map, in its order */
    public static Property<?>[] keys(Reference2ObjectArrayMap values) {
        Property<?>[] keys = new Property<?>[values.size()];
        int i = 0;
        for (Object k : values.keySet()) {
            keys[i++] = (Property<?>) k;
        }
        return keys;
    }

    /** the values of a 1.21.11 values map, in its order */
    public static Comparable<?>[] values(Reference2ObjectArrayMap values) {
        Comparable<?>[] out = new Comparable<?>[values.size()];
        int i = 0;
        for (Object v : values.values()) {
            out[i++] = (Comparable<?>) v;
        }
        return out;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Reference2ObjectArrayMap map(Property<?>[] keys, Comparable<?>[] values) {
        return new Reference2ObjectArrayMap(keys, values, keys.length);
    }

    /** a 1.21.11 factory as 26.1's: the map of the arrays and the owner's definition's codec */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static StateDefinition.Factory adapt(LegacyFactory factory) {
        return (owner, keys, values) -> factory.create(owner, map(keys, values), new DefinitionCodec(owner));
    }

    /**
     * the overriding side of the same: a 1.21.11 class implementing StateDefinition.Factory, called by 26.1 with
     * the arrays (overrideHooks bridge)
     */
    public static Object create(LegacyFactory factory, Object owner, Property<?>[] keys, Comparable<?>[] values) {
        return factory.create(owner, map(keys, values), new DefinitionCodec(owner));
    }

    // ---- reading ----

    /** 1.21.11 StateHolder.getValues(): the property values, in the state's order */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map getValues(StateHolder<?, ?> state) {
        Reference2ObjectArrayMap map = new Reference2ObjectArrayMap();
        state.getValues().forEach(v -> map.put(v.property(), v.value()));
        return Collections.unmodifiableMap(map);
    }

    private static StateDefinition<?, ?> definition(Object owner) {
        if (owner instanceof Block block) {
            return block.getStateDefinition();
        }
        if (owner instanceof Fluid fluid) {
            return fluid.getStateDefinition();
        }
        return null;
    }

    private static Object owner(StateHolder<?, ?> state) {
        if (state instanceof BlockState b) {
            return b.getBlock();
        }
        if (state instanceof FluidState f) {
            return f.getType();
        }
        return state.owner;
    }

    /** 1.21.11 StateHolder.propertiesCodec (the state's definition's) */
    @SuppressWarnings("rawtypes")
    public static MapCodec propertiesCodec(StateHolder<?, ?> state) {
        return new DefinitionCodec(owner(state));
    }

    /**
     * 1.21.11 StateHolder.codec(ownerCodec, defaultState): dispatched on the owner under "Name"; a state with
     * properties reads them, leniently, under "Properties", defaulting to the owner's default state
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Codec codec(Codec ownerCodec, Function defaultState) {
        return ownerCodec.dispatch("Name", s -> owner((StateHolder<?, ?>) s), o -> {
            StateHolder<?, ?> d = (StateHolder<?, ?>) defaultState.apply(o);
            if (d.getProperties().isEmpty()) {
                return MapCodec.unit(d);
            }
            return propertiesCodec(d).codec().lenientOptionalFieldOf("Properties")
                .xmap(opt -> ((Optional) opt).orElse(d), Optional::of);
        });
    }

    /**
     * the properties codec of an owner's definition, looked up when used (a factory runs while the definition is
     * being built, before the owner answers it)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final class DefinitionCodec extends MapCodec {
        private final Object owner;

        DefinitionCodec(Object owner) {
            this.owner = owner;
        }

        private MapCodec target() {
            StateDefinition<?, ?> def = definition(owner);
            if (def == null) {
                throw new IllegalStateException(owner + ": 1.21.11 state properties codec of an owner that is neither a"
                    + " block nor a fluid; 26.1 states do not lead back to their definition");
            }
            return def.propertiesCodec();
        }

        @Override
        public Stream keys(DynamicOps ops) {
            return target().keys(ops);
        }

        @Override
        public DataResult decode(DynamicOps ops, MapLike input) {
            return target().decode(ops, input);
        }

        @Override
        public RecordBuilder encode(Object input, DynamicOps ops, RecordBuilder prefix) {
            return target().encode(input, ops, prefix);
        }

        @Override
        public String toString() {
            return "CbWallStates.DefinitionCodec[" + owner + "]";
        }
    }
}
