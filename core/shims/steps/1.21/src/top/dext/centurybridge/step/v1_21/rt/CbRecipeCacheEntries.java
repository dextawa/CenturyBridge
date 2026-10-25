package top.dext.centurybridge.step.v1_21.rt;

/**
 * Runtime side of the RecipeCache.Entry route (CbRecipeCacheEntryMixin / CbRecipeCacheMixin): the restored 1.20.6
 * matches(List) as the layer calls it.
 */
public final class CbRecipeCacheEntries {
    private CbRecipeCacheEntries() {
    }

    /** the entry with 1.20.6's matches(List<ItemStack>) restored on it (CbRecipeCacheEntryMixin) */
    public interface Restored {
        boolean method_54472(java.util.List<net.minecraft.class_1799> inputs);

        int comp_2816();

        int comp_2817();
    }

    /** 1.21's entry.matches(input), as 1.21's size test plus 1.20.6's matches(List) over the input's items */
    public static boolean matches(Object entry, net.minecraft.class_9694 input) {
        Restored e = (Restored) entry;
        return e.comp_2816() == input.method_59991() && e.comp_2817() == input.method_59992() && e.method_54472(input.method_59989());
    }
}
