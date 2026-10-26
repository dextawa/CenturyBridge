package top.dext.centurybridge.step.v1_21_6.rt;

import net.minecraft.class_1799;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_7225;

/**
 * 1.21.5's ItemStack tag forms, which 1.21.6 dropped (every caller moved to ItemStack.CODEC through ValueInput /
 * ValueOutput; javap of both): parse(provider, tag) parsed ItemStack.CODEC under the provider's NbtOps serialization
 * context, logging "Tried to load invalid item: '{}'" and answering the partial result; save(provider, prefix) and
 * save(provider) encoded the same way and threw on failure, and refused an empty stack ("Cannot encode empty
 * ItemStack"). The same bodies on 1.21.6's ItemStack.CODEC (unchanged type) and the same logger name.
 */
public final class CbItemStacks {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(class_1799.class);

    private CbItemStacks() {
    }

    /** ItemStack.parse(HolderLookup.Provider, Tag), static */
    public static java.util.Optional<class_1799> method_57360(class_7225.class_7874 provider, class_2520 tag) {
        return class_1799.field_24671.parse(provider.method_57093(class_2509.field_11560), tag)
            .resultOrPartial(err -> LOGGER.error("Tried to load invalid item: '{}'", err));
    }

    /** ItemStack.save(HolderLookup.Provider, Tag) */
    public static class_2520 method_57376(class_1799 stack, class_7225.class_7874 provider, class_2520 prefix) {
        if (stack.method_7960()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        }
        return class_1799.field_24671.encode(stack, provider.method_57093(class_2509.field_11560), prefix).getOrThrow();
    }

    /** ItemStack.save(HolderLookup.Provider) */
    public static class_2520 method_57358(class_1799 stack, class_7225.class_7874 provider) {
        if (stack.method_7960()) {
            throw new IllegalStateException("Cannot encode empty ItemStack");
        }
        return class_1799.field_24671.encodeStart(provider.method_57093(class_2509.field_11560), stack).getOrThrow();
    }
}
