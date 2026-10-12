package top.dext.centurybridge.rt.v1_20_2;

import java.util.function.Consumer;

/**
 * AdvancementSubProvider.generate(lookup, consumer) kept its descriptor while the consumer's type
 * argument changed (Consumer&lt;Advancement&gt; -> Consumer&lt;AdvancementEntry&gt;): a 1.20.1
 * generator hands it advancement values, 1.20.2 code entries. The consumer 1.20.2's
 * AdvancementProvider passes in is wrapped (CbAdvancementProviderConsumer) to take both: an entry
 * goes through, an advancement value goes through as its entry (CbRecipeDatagen.entryOf: the
 * entry recorded when it was built or loaded).
 */
public final class CbAdvancementConsumers {

    private CbAdvancementConsumers() {
    }

    /** a 1.20.2 Consumer&lt;AdvancementEntry&gt; that also takes the 1.20.1 advancement value */
    public static final class Dual implements Consumer<Object> {
        private final Consumer<net.minecraft.class_8779> entries;

        Dual(Consumer<net.minecraft.class_8779> entries) {
            this.entries = entries;
        }

        @Override
        public void accept(Object value) {
            if (value instanceof net.minecraft.class_8779 entry) {
                entries.accept(CbRecipeDatagen.track(entry));
            } else if (value instanceof net.minecraft.class_161 advancement) {
                entries.accept(CbRecipeDatagen.entryOf(advancement));
            } else {
                throw new ClassCastException("CenturyBridge: not an advancement: " + value);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Consumer<net.minecraft.class_8779> dual(Consumer<net.minecraft.class_8779> entries) {
        return entries == null || ((Object) entries) instanceof Dual ? entries : (Consumer) new Dual(entries);
    }
}
