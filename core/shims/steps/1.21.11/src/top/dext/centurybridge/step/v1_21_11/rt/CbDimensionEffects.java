package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.collect.MapMaker;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.class_2874;
import net.minecraft.class_2960;

/**
 * 1.21.10's DimensionType.effectsLocation (comp_655): the id of the DimensionSpecialEffects a dimension type is drawn
 * with, the dimension_type file's "effects" field (default minecraft:overworld). 1.21.11 dropped the field and the
 * class: its own dimension types say the same with skybox and cardinal lighting. The layer reads "effects" where
 * 1.21.11's codec (DIRECT_CODEC for the data files, NETWORK_CODEC for registry sync, both made by
 * DimensionType.createDirectCodec) drops it, keeps it beside the decoded type, and writes it back on encode, so a
 * 1.21.10 data pack's effects id reaches the client as on 1.21.10 (CbDimensionTypeCodecMixin). A type with no
 * "effects" (1.21.11's own data, which no longer writes it) has the id its skybox and lighting stand for: END skybox
 * -> the_end, NETHER cardinal lighting -> the_nether, otherwise overworld (1.21.10's vanilla files named exactly these).
 */
public final class CbDimensionEffects {
    public static final class_2960 OVERWORLD_EFFECTS = class_2960.method_60656("overworld");
    public static final class_2960 NETHER_EFFECTS = class_2960.method_60656("the_nether");
    public static final class_2960 END_EFFECTS = class_2960.method_60656("the_end");
    private static final Map<class_2874, class_2960> LOCATIONS = new MapMaker().weakKeys().makeMap();

    private CbDimensionEffects() {
    }

    /** BuiltinDimensionTypes.OVERWORLD_EFFECTS */
    public static class_2960 field_37670() {
        return OVERWORLD_EFFECTS;
    }

    /** BuiltinDimensionTypes.NETHER_EFFECTS */
    public static class_2960 field_37671() {
        return NETHER_EFFECTS;
    }

    /** BuiltinDimensionTypes.END_EFFECTS */
    public static class_2960 field_37672() {
        return END_EFFECTS;
    }

    /** DimensionType.effectsLocation() */
    public static class_2960 comp_655(class_2874 type) {
        class_2960 id = LOCATIONS.get(type);
        if (id != null) {
            return id;
        }
        if (type.comp_5220() == class_2874.class_12326.field_64387) {
            return END_EFFECTS;
        }
        if (type.comp_5221() == class_2874.class_12325.field_64381) {
            return NETHER_EFFECTS;
        }
        return OVERWORLD_EFFECTS;
    }

    /** 1.21.11's dimension type codec, reading and writing 1.21.10's "effects" beside it */
    public static Codec<class_2874> wrap(Codec<class_2874> codec) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<class_2874, T>> decode(DynamicOps<T> ops, T input) {
                DataResult<Pair<class_2874, T>> result = codec.decode(ops, input);
                DataResult<class_2960> effects = ops.get(input, "effects").flatMap(v -> class_2960.field_25139.parse(ops, v));
                effects.result().ifPresent(id -> result.result().ifPresent(p -> LOCATIONS.put(p.getFirst(), id)));
                return result;
            }

            @Override
            public <T> DataResult<T> encode(class_2874 type, DynamicOps<T> ops, T prefix) {
                DataResult<T> result = codec.encode(type, ops, prefix);
                class_2960 id = LOCATIONS.get(type);
                if (id == null) {
                    return result;
                }
                return result.flatMap(map -> ops.mergeToMap(map, ops.createString("effects"), ops.createString(id.toString())));
            }

            @Override
            public String toString() {
                return codec + "+effects";
            }
        };
    }
}
