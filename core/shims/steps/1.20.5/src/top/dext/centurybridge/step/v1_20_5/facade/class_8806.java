package top.dext.centurybridge.step.v1_20_5.facade;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * 1.20.4 net.minecraft.util.JavaOps (net/minecraft/class_8806): Mojang's DynamicOps over plain Java
 * values, which 1.20.5 moved into DataFixerUpper as com.mojang.serialization.JavaOps. The bodies moved
 * unchanged (compared instruction by instruction against DataFixerUpper 7.0.14) except remove(): 1.20.4
 * answered a DataResult -- of a copy of the map without the key, an error "Not a map: " for anything
 * else -- where DataFixerUpper answers the map copy itself, or the input. Every member here answers what
 * 1.20.4's did: DataFixerUpper's JavaOps for the unchanged ones (1.20.4's constructor was private, so no
 * class extends this one and there is no override to keep), 1.20.4's body for remove().
 */
public class class_8806 implements DynamicOps<Object> {

    /** JavaOps.INSTANCE */
    public static final class_8806 field_46239 = new class_8806();

    private static final com.mojang.serialization.JavaOps OPS = com.mojang.serialization.JavaOps.INSTANCE;

    /** private in 1.20.4 (a facade is never access-widened, so its members are public) */
    public class_8806() {
    }

    @Override
    public Object empty() {
        return OPS.empty();
    }

    @Override
    public Object emptyMap() {
        return OPS.emptyMap();
    }

    @Override
    public Object emptyList() {
        return OPS.emptyList();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Object input) {
        return OPS.convertTo(outOps, input);
    }

    @Override
    public DataResult<Number> getNumberValue(Object input) {
        return OPS.getNumberValue(input);
    }

    @Override
    public Object createNumeric(Number value) {
        return OPS.createNumeric(value);
    }

    @Override
    public Object createByte(byte value) {
        return OPS.createByte(value);
    }

    @Override
    public Object createShort(short value) {
        return OPS.createShort(value);
    }

    @Override
    public Object createInt(int value) {
        return OPS.createInt(value);
    }

    @Override
    public Object createLong(long value) {
        return OPS.createLong(value);
    }

    @Override
    public Object createFloat(float value) {
        return OPS.createFloat(value);
    }

    @Override
    public Object createDouble(double value) {
        return OPS.createDouble(value);
    }

    @Override
    public DataResult<Boolean> getBooleanValue(Object input) {
        return OPS.getBooleanValue(input);
    }

    @Override
    public Object createBoolean(boolean value) {
        return OPS.createBoolean(value);
    }

    @Override
    public DataResult<String> getStringValue(Object input) {
        return OPS.getStringValue(input);
    }

    @Override
    public Object createString(String value) {
        return OPS.createString(value);
    }

    @Override
    public DataResult<Object> mergeToList(Object list, Object value) {
        return OPS.mergeToList(list, value);
    }

    @Override
    public DataResult<Object> mergeToList(Object list, List<Object> values) {
        return OPS.mergeToList(list, values);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
        return OPS.mergeToMap(map, key, value);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Map<Object, Object> values) {
        return OPS.mergeToMap(map, values);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, MapLike<Object> values) {
        return OPS.mergeToMap(map, values);
    }

    /** 1.20.4's private mapLikeToMap: the map-like's entries as an immutable map */
    public static Map<Object, Object> method_53923(MapLike<Object> values) {
        return values.entries().collect(com.google.common.collect.ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
    }

    /** 1.20.4's package-private getMapEntries(map): the map's entries as pairs */
    public static Stream<Pair<Object, Object>> method_53926(Map<?, ?> map) {
        return map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()));
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        return OPS.getMapValues(input);
    }

    @Override
    public DataResult<Consumer<BiConsumer<Object, Object>>> getMapEntries(Object input) {
        return OPS.getMapEntries(input);
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map) {
        return OPS.createMap(map);
    }

    @Override
    public DataResult<MapLike<Object>> getMap(Object input) {
        return OPS.getMap(input);
    }

    @Override
    public Object createMap(Map<Object, Object> map) {
        return OPS.createMap(map);
    }

    @Override
    public DataResult<Stream<Object>> getStream(Object input) {
        return OPS.getStream(input);
    }

    @Override
    public DataResult<Consumer<Consumer<Object>>> getList(Object input) {
        return OPS.getList(input);
    }

    @Override
    public Object createList(Stream<Object> input) {
        return OPS.createList(input);
    }

    @Override
    public DataResult<ByteBuffer> getByteBuffer(Object input) {
        return OPS.getByteBuffer(input);
    }

    @Override
    public Object createByteList(ByteBuffer input) {
        return OPS.createByteList(input);
    }

    @Override
    public DataResult<IntStream> getIntStream(Object input) {
        return OPS.getIntStream(input);
    }

    @Override
    public Object createIntList(IntStream input) {
        return OPS.createIntList(input);
    }

    @Override
    public DataResult<LongStream> getLongStream(Object input) {
        return OPS.getLongStream(input);
    }

    @Override
    public Object createLongList(LongStream input) {
        return OPS.createLongList(input);
    }

    /**
     * 1.20.4's remove(input, key): a DataResult of a copy of the map without the key (Map.copyOf of a
     * LinkedHashMap copy), an error result "Not a map: input" for anything but a map
     */
    @Override
    public Object remove(Object input, String key) {
        if (input instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new java.util.LinkedHashMap<>(map);
            copy.remove(key);
            return DataResult.success(Map.copyOf(copy));
        }
        return DataResult.error(() -> "Not a map: " + input);
    }

    /** 1.20.4's FixedMapBuilder over these ops; DataFixerUpper's is the same builder (its ops() DataFixerUpper's JavaOps) */
    @Override
    public RecordBuilder<Object> mapBuilder() {
        return OPS.mapBuilder();
    }

    @Override
    public String toString() {
        return "Java";
    }
}
