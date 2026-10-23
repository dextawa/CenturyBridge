package top.dext.centurybridge.rt.v1_20_2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Advancement members whose erased shape stayed while their type arguments moved from 1.20.1's
 * Advancement to 1.20.2's AdvancementNode / AdvancementHolder (generic drift: the JVM links them, the
 * first element a 1.20.1 mod takes out is a ClassCastException).
 *
 * Converters are adaptive both ways -- each element is tested and only the kind the other side does not
 * expect is converted -- because erasure cannot tell who built a collection: a 1.20.1 override's answer
 * arrives where 1.20.2 code reads, a 1.20.2 answer where 1.20.1 code reads, and a value may pass both
 * directions. Every conversion goes through CbAdvNodes (the record is 1.20.1's Advancement; the node it is
 * placed at, the holder naming it).
 */
public final class CbAdvGeneric {

    // ------------------------------------------------------------------ element conversions

    private static Object toAdvancement(Object o) {
        if (o instanceof net.minecraft.class_8781 node) {
            return node.method_53647();
        }
        if (o instanceof net.minecraft.class_8779 holder) {
            return holder.comp_1920();
        }
        return o;
    }

    private static Object toNode(Object o) {
        return o instanceof net.minecraft.class_161 a ? CbAdvNodes.nodeOf(a) : o;
    }

    private static Object toHolder(Object o) {
        return o instanceof net.minecraft.class_161 a ? CbAdvNodes.holderOf(a) : o;
    }

    private static List<Object> map(Iterable<?> in, java.util.function.UnaryOperator<Object> f) {
        List<Object> out = new ArrayList<>();
        for (Object o : in) {
            out.add(f.apply(o));
        }
        return out;
    }

    // ------------------------------------------------------------------ AdvancementTree.getRoots()

    /** getRoots() read by 1.20.1 code: the roots' advancements */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Iterable advancements(Iterable in) {
        return in == null ? null : map(in, CbAdvGeneric::toAdvancement);
    }

    /** getRoots() answered by a 1.20.1 override, read by 1.20.2: the advancements' nodes */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Iterable nodes(Iterable in) {
        return in == null ? null : map(in, CbAdvGeneric::toNode);
    }

    // ------------------------------------------------------------------ ServerAdvancementManager.getAllAdvancements()

    /** getAllAdvancements() read by 1.20.1 code: the advancements */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection advancementCollection(Collection in) {
        return in == null ? null : map(in, CbAdvGeneric::toAdvancement);
    }

    /** a collection 1.20.1 code built of advancements, read by 1.20.2 as holders */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection holderCollection(Collection in) {
        return in == null ? null : map(in, CbAdvGeneric::toHolder);
    }

    // ------------------------------------------------------------------ advancement consumers (datagen)

    /**
     * A Consumer&lt;Advancement&gt; 1.20.1 code passes where 1.20.2 hands Consumer&lt;AdvancementHolder&gt;
     * to (AdvancementSubProvider.generate's caller): it takes what 1.20.2 hands it -- holders -- as their
     * advancement. A consumer that already takes both (CbAdvancementConsumers.Dual) passes as it is.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Consumer holderConsumer(Consumer legacy) {
        return (Consumer) CbRecipeDatagen.entryConsumer(legacy);
    }

    /**
     * The consumer 1.20.2 hands a 1.20.1 generate(lookup, consumer) override: it takes holders; the 1.20.1
     * body hands it advancements, which go in as their holders (CbAdvancementConsumers.dual)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Consumer advancementConsumer(Consumer holders) {
        return (Consumer) CbAdvancementConsumers.dual(holders);
    }

    private CbAdvGeneric() {
    }
}
