package top.dext.centurybridge.step.v26_1.rt;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * 1.21.11 block state members 26.1 renamed or reshaped:
 * <ul>
 * <li>getBlockHolder(): 26.1's typeHolder();</li>
 * <li>getTags(): the block's built-in holder's tags (1.21.11's body);</li>
 * <li>post-processing: 1.21.11 asked a predicate whether a state needed it (the position then marked);
 *     26.1 asks for the position to mark, none when not -- the same position when the predicate holds;</li>
 * <li>StateHolder.getValues(): 1.21.11 answered the property-to-value map, 26.1 a stream of property values:
 *     the map, in the state's property order (unmodifiable).</li>
 * </ul>
 */
public final class CbWallBlockStates {

    private CbWallBlockStates() {
    }

    public static Holder<Block> getBlockHolder(BlockBehaviour.BlockStateBase state) {
        return state.typeHolder();
    }

    public static Stream<TagKey<Block>> getTags(BlockBehaviour.BlockStateBase state) {
        return state.typeHolder().tags();
    }

    public static boolean hasPostProcess(BlockBehaviour.BlockStateBase state, BlockGetter level, BlockPos pos) {
        return state.getPostProcessPos(level, pos) != null;
    }

    public static BlockBehaviour.Properties hasPostProcess(BlockBehaviour.Properties properties, BlockBehaviour.StatePredicate predicate) {
        return properties.postProcess((state, level, pos) -> predicate.test(state, level, pos) ? pos : null);
    }

    public static Map<Property<?>, Comparable<?>> getValues(StateHolder<?, ?> state) {
        Map<Property<?>, Comparable<?>> values = new LinkedHashMap<>();
        state.getValues().forEach(value -> values.put(value.property(), value.value()));
        return Collections.unmodifiableMap(values);
    }
}
