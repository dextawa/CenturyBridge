package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;

/**
 * HoverEvent.Action's 1.20.4 codecs, given back for the code that still reads them -- mods' mixins and the climbing
 * 1.20.3 layer's (CbHoverEventActionMixin; instanceFieldGetters, MixinBridge carries its @Shadow reads through these).
 * 1.20.5 keeps both as MapCodecs (javap -c of the constructors).
 */
public final class CbTextHoverGiveBack {

    /**
     * Action.codec (class_2568$class_5247.field_46605): 1.20.4 built it as {@code codec.xmap(..).fieldOf("contents").codec()},
     * 1.20.5 as the same chain without the final {@code codec()}; so it is 1.20.5's MapCodec as a Codec.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Codec field_46605(net.minecraft.class_2568.class_5247 action) {
        return action.field_46605.codec();
    }

    /**
     * Action.legacyCodec (class_2568$class_5247.field_46606): 1.20.4 decoded a hover event from the legacy "value" text
     * itself (a Codec over the Component, encoding refused); 1.20.5 reads the same text as the field "value" of a map
     * (a MapCodec over its own Component decoder, which hands the legacy converter the registries when the ops carry
     * them). So: the text is put under "value" and read with 1.20.5's; ops without registries get the running world's
     * (1.20.4's converters needed none; 1.20.5's read items and entities through them). Encoding is refused, as in both.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Codec field_46606(net.minecraft.class_2568.class_5247 action) {
        com.mojang.serialization.MapCodec map = action.field_46606;
        return Codec.of(Encoder.error("Can't encode in legacy format"), new com.mojang.serialization.Decoder() {
            @Override
            public DataResult decode(DynamicOps ops, Object input) {
                DynamicOps withRegistries = ops instanceof net.minecraft.class_6903 ? ops
                    : net.minecraft.class_6903.method_46632(ops, CbRegistries.current());
                Object wrapped = withRegistries.createMap(java.util.Map.of(withRegistries.createString("value"), input));
                DataResult<Pair> read = map.codec().decode(withRegistries, wrapped);
                return read.map(p -> Pair.of(p.getFirst(), input));
            }
        });
    }

    private CbTextHoverGiveBack() {
    }
}
