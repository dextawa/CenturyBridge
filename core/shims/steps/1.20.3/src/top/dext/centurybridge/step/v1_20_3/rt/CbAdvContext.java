package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.function.Supplier;
import top.dext.centurybridge.step.v1_20_3.facade.DeserializationContext;

/**
 * The DeserializationContext a 1.20.2 trigger's createInstance is handed while 1.20.3 decodes
 * criteria through codecs, which carry no context.
 *
 * 1.20.2 built one per advancement in ServerAdvancementManager's load (that advancement's id, the
 * server's loot data) and passed it down to every criterion; CbAdvServerAdvancementManagerMixin
 * opens the same context around 1.20.3's per-advancement decode. A 1.20.2 entry point a mod calls
 * with a context of its own (Criterion.criterionFromJson(json, context), CriterionTrigger
 * .createInstance(json, context), ...) runs its decode inside that context ({@link #with}). A codec
 * decode outside both -- which no 1.20.2 code could make -- sees a context without id or loot data.
 */
public final class CbAdvContext {

    private static final ThreadLocal<DeserializationContext> CURRENT = new ThreadLocal<>();

    /** ServerAdvancementManager starts decoding advancement {@code id} */
    public static void begin(net.minecraft.class_2960 id, net.minecraft.class_60 lootData) {
        CURRENT.set(new DeserializationContext(id, lootData));
    }

    /** ... and is done with it */
    public static void end() {
        CURRENT.remove();
    }

    /** the context of the decode in progress */
    public static DeserializationContext current() {
        DeserializationContext c = CURRENT.get();
        return c != null ? c : new DeserializationContext(null, null);
    }

    /** run a decode a 1.20.2 entry point was asked for with {@code context} */
    public static <T> T with(DeserializationContext context, Supplier<T> decode) {
        DeserializationContext before = CURRENT.get();
        CURRENT.set(context);
        try {
            return decode.get();
        } finally {
            if (before == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(before);
            }
        }
    }

    private CbAdvContext() {
    }
}
