package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;
import net.minecraft.class_1887;
import net.minecraft.class_1889;
import net.minecraft.class_5483;
import net.minecraft.class_5819;
import net.minecraft.class_6010;
import net.minecraft.class_6012;
import net.minecraft.class_6030;
import net.minecraft.class_6880;
import top.dext.centurybridge.step.v1_21_5.facade.Weight;
import top.dext.centurybridge.step.v1_21_5.facade.WeightedEntry;

/**
 * 1.21.4 weighted entries on 1.21.5's weighted lists.
 *
 * 1.21.4: WeightedRandomList (class_6012) holds WeightedEntry objects that each answer their own Weight
 * (intrusive: SpawnerData, EnchantmentInstance, LongJumpToRandomPos.PossibleJump extend IntrusiveBase; or Wrapper(value,
 * Weight) in a SimpleWeightedRandomList); WeightedRandom (class_6011) reads entry.getWeight().asInt().
 * 1.21.5: WeightedList (same name class_6012) holds Weighted(value, int) records; WeightedRandom takes a ToIntFunction;
 * EnchantmentInstance and PossibleJump are records (weight() = the enchantment's weight / the weight component).
 *
 * Erasure cannot tell which kind of entry a list holds, so every converter here is adaptive (instanceof-tested): what
 * already is 1.21.5's form passes, a 1.21.4 entry is read for its weight.
 */
public final class CbWeighted {
    private CbWeighted() {
    }

    /**
     * An entry's weight as 1.21.4's entry.getWeight().asInt() answered it. 1.21.5's records that were 1.21.4 entries
     * (Weighted, EnchantmentInstance, PossibleJump, SpawnerData) implement WeightedEntry again through the layer's
     * mixins (mixin/CbWeighted*Mixin); they are read directly here, without a Weight per call. Anything else was not
     * a 1.21.4 entry: 1.21.4 bound list elements to WeightedEntry, so its cast failed the same way.
     */
    public static int weightOf(Object entry) {
        if (entry instanceof class_6010<?> w) {
            return w.comp_2543();
        }
        if (entry instanceof class_5483.class_1964 data) {
            return CbSpawnWeights.weight(data);
        }
        if (entry instanceof class_1889 ench) {
            // 1.21.4 EnchantmentInstance(enchantment, level): super(enchantment.value().getWeight()); 1.21.5 weight()
            return ench.method_66344();
        }
        if (entry instanceof class_6030.class_6031 jump) {
            // 1.21.4 PossibleJump(pos, weight): super(weight); 1.21.5 record component weight
            return jump.comp_3479();
        }
        return ((WeightedEntry) entry).method_34979().method_34976();
    }

    /** Weight -> the int 1.21.5 takes */
    public static int asInt(Weight weight) {
        return weight.method_34976();
    }

    /** the int 1.21.5 answers -> Weight */
    public static Weight weight(int weight) {
        return Weight.method_34977(weight);
    }

    private static final ToIntFunction<Object> WEIGHER = CbWeighted::weightOf;

    /** the weigher 1.21.5's WeightedRandom takes, reading each entry as 1.21.4's WeightedRandom did */
    @SuppressWarnings("unchecked")
    public static <T> ToIntFunction<T> weigher() {
        return (ToIntFunction<T>) WEIGHER;
    }

    /** an entry as 1.21.5's lists hold it: a Weighted passes, any 1.21.4 entry is wrapped with its own weight */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_6010<Object> wrap(Object entry) {
        if (entry instanceof class_6010 w) {
            return w;
        }
        return new class_6010<>(entry, weightOf(entry));
    }

    /** WeightedRandomList.create(E...) */
    public static class_6010<?>[] wrapArray(Object[] entries) {
        class_6010<?>[] out = new class_6010<?>[entries.length];
        for (int i = 0; i < entries.length; i++) {
            out[i] = wrap(entries[i]);
        }
        return out;
    }

    /** WeightedRandomList.create(List) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List wrapList(List entries) {
        List<class_6010<Object>> out = new ArrayList<>(entries.size());
        for (Object e : entries) {
            out.add(wrap(e));
        }
        return out;
    }

    /**
     * Whether a 1.21.5 list entry's value is itself a 1.21.4 weighted entry -- the entry of an intrusive 1.21.4 list
     * (SpawnerData, EnchantmentInstance, PossibleJump, a mod's own IntrusiveBase or WeightedEntry), which 1.21.4's
     * list held and answered as it is. A Weighted value (1.21.4's Wrapper) is a SimpleWeightedRandomList's payload,
     * not an entry of its own. Erasure keeps no other trace of which kind of list a mod meant.
     */
    static boolean intrusive(Object value) {
        return value instanceof WeightedEntry && !(value instanceof class_6010);
    }

    /** a 1.21.5 entry as 1.21.4's list held it: the value of an intrusive entry (a SpawnerData remembers its weight), else the Weighted */
    static Object entryOf(class_6010<?> weighted) {
        Object value = weighted.comp_2542();
        if (intrusive(value)) {
            if (value instanceof class_5483.class_1964 data) {
                CbSpawnWeights.record(data, weighted.comp_2543());
            }
            return value;
        }
        return weighted;
    }

    /** WeightedRandomList.unwrap(): 1.21.5's Weighted entries as 1.21.4's list held them (an immutable list, as 1.21.4's ImmutableList) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List unwrapList(List entries) {
        Object[] out = new Object[entries.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = entryOf((class_6010<?>) entries.get(i));
        }
        return List.of(out);
    }

    /**
     * WeightedRandomList.getRandom(random): 1.21.5's own draw (WeightedList.getRandom, the same nextInt(totalWeight)
     * and walk as 1.21.4's), answered as 1.21.4's list did: the drawn entry. 1.21.5 answers the drawn value; its entry is
     * the Weighted holding that value, found by identity. A list holding one value object in several entries answers the
     * first of them (same value; its weight may be another entry's -- recorded as a loss).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional getRandom(class_6012 list, class_5819 random) {
        Optional<Object> drawn = list.method_34992(random);
        if (drawn.isEmpty()) {
            return drawn;
        }
        Object value = drawn.get();
        for (Object o : list.method_34994()) {
            class_6010<?> w = (class_6010<?>) o;
            if (w.comp_2542() == value) {
                return Optional.of(entryOf(w));
            }
        }
        throw new IllegalStateException("weighted list drew a value none of its entries holds: " + value);
    }

    /** WeightedRandomList.codec(entryCodec): entryCodec.listOf().xmap(create, unwrap) as 1.21.4 built it */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Codec intrusiveCodec(Codec entryCodec) {
        return entryCodec.listOf().xmap(l -> class_6012.method_34988(wrapList((List) l)), wl -> unwrapList(((class_6012) wl).method_34994()));
    }

    /** EnchantmentInstance.enchantment */
    public static class_6880<class_1887> field_9093(class_1889 inst) {
        return inst.comp_3486();
    }

    /** EnchantmentInstance.level */
    public static int field_9094(class_1889 inst) {
        return inst.comp_3487();
    }
}
