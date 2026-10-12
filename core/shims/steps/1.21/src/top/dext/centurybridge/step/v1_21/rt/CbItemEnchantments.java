package top.dext.centurybridge.step.v1_21.rt;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * ItemEnchantments, its Mutable, EnchantmentInstance and EnchantmentPredicate between 1.20.6's enchantment
 * objects and 1.21's holders. In 1.20.6 an item's enchantments were keyed by the holders of
 * BuiltInRegistries.ENCHANTMENT, whose value is the enchantment object; in 1.21 by the world registry's
 * holders, whose value is the record. A 1.20.6 mod passes objects (Enchantment parameters: converted to
 * the world's holder) and reads holders (keySet / entrySet / removeIf: the views here answer the
 * holders of the objects, CenturyBridge's stand-in registry's, so holder.value() is the 1.20.6 object
 * again and holder.is(tag) answers the world's tags, see CbEnchantments.withLegacyTags).
 */
public final class CbItemEnchantments {

    private CbItemEnchantments() {
    }

    /** 1.20.6 ItemEnchantments.MAX_LEVEL (fieldGetter: 1.21 dropped the constant) */
    public static int field_50014() {
        return 255;
    }

    /**
     * The holder a level lookup asks for: the world's holder of the enchantment's key; for an enchantment
     * the world does not have (or with no world), a holder of that key bound to nothing, which no
     * ItemEnchantments contains -- 1.20.6 answered 0 for an enchantment not on the item, and the lookup
     * answers 0 (the holder is only compared, never stored or read).
     */
    public static net.minecraft.class_6880<net.minecraft.class_1887> lookupHolder(class_1887 enchantment) {
        net.minecraft.class_2378<net.minecraft.class_1887> w = CbEnchantments.current();
        net.minecraft.class_5321<net.minecraft.class_1887> key = CbEnchantments.wKeyOf(enchantment);
        if (w != null) {
            Optional<net.minecraft.class_6880.class_6883<net.minecraft.class_1887>> h = w.method_40264(key);
            if (h.isPresent()) {
                return h.get();
            }
            return net.minecraft.class_6880.class_6883.method_40234(w.method_46770(), key);
        }
        @SuppressWarnings({"unchecked", "rawtypes"})
        net.minecraft.class_7876<net.minecraft.class_1887> owner = (net.minecraft.class_7876) CbEnchantments.legacy().method_46770();
        return net.minecraft.class_6880.class_6883.method_40234(owner, key);
    }

    // ---- holder views ----

    /** a set of world holders seen as the holders of the 1.20.6 objects they stand for (adaptive, undone by wHolderSet) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set vHolderSet(Set holders) {
        if (holders == null || holders instanceof VHolders) {
            return holders;
        }
        if (holders instanceof WHolders w) {
            return w.inner;
        }
        return new VHolders(holders);
    }

    /** the inverse of vHolderSet */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set wHolderSet(Set holders) {
        if (holders == null || holders instanceof WHolders) {
            return holders;
        }
        if (holders instanceof VHolders v) {
            return v.inner;
        }
        return new WHolders(holders);
    }

    /** (holder, level) entries of world holders as entries of the 1.20.6 objects' holders (adaptive, undone by wEntrySet) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set vEntrySet(Set entries) {
        if (entries == null || entries instanceof VEntries) {
            return entries;
        }
        if (entries instanceof WEntries w) {
            return w.inner;
        }
        return new VEntries(entries);
    }

    /** the inverse of vEntrySet */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set wEntrySet(Set entries) {
        if (entries == null || entries instanceof WEntries) {
            return entries;
        }
        if (entries instanceof VEntries v) {
            return v.inner;
        }
        return new WEntries(entries);
    }

    /** a 1.20.6 test of holders (removeIf) as a test of world holders: it is handed each world holder's 1.20.6 holder */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Predicate wPredicate(Predicate test) {
        if (test == null || test instanceof VPredicate) {
            return test == null ? null : ((VPredicate) test).inner;
        }
        return new WPredicate(test);
    }

    /** the inverse of wPredicate: a world-holder test as a test of 1.20.6 holders (a mod's override is handed vanilla's test) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Predicate vPredicate(Predicate test) {
        if (test == null || test instanceof WPredicate) {
            return test == null ? null : ((WPredicate) test).inner;
        }
        return new VPredicate(test);
    }

    @SuppressWarnings({"rawtypes"})
    private static final class VHolders extends AbstractSet {
        final Set inner;

        VHolders(Set inner) {
            this.inner = inner;
        }

        @Override
        public Iterator iterator() {
            Iterator it = inner.iterator();
            return new Iterator() {
                public boolean hasNext() {
                    return it.hasNext();
                }

                public Object next() {
                    return CbEnchantments.cbHolderV((net.minecraft.class_6880) it.next());
                }
            };
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean contains(Object o) {
            return o instanceof net.minecraft.class_6880 h && inner.contains(CbEnchantments.cbHolderW(h));
        }
    }

    @SuppressWarnings({"rawtypes"})
    private static final class WHolders extends AbstractSet {
        final Set inner;

        WHolders(Set inner) {
            this.inner = inner;
        }

        @Override
        public Iterator iterator() {
            Iterator it = inner.iterator();
            return new Iterator() {
                public boolean hasNext() {
                    return it.hasNext();
                }

                public Object next() {
                    return CbEnchantments.cbHolderW((net.minecraft.class_6880) it.next());
                }
            };
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean contains(Object o) {
            return o instanceof net.minecraft.class_6880 h && inner.contains(CbEnchantments.cbHolderV(h));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class VEntries extends AbstractSet {
        final Set inner;

        VEntries(Set inner) {
            this.inner = inner;
        }

        @Override
        public Iterator iterator() {
            Iterator it = inner.iterator();
            return new Iterator() {
                public boolean hasNext() {
                    return it.hasNext();
                }

                public Object next() {
                    it.unimi.dsi.fastutil.objects.Object2IntMap.Entry e = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry) it.next();
                    return new it.unimi.dsi.fastutil.objects.AbstractObject2IntMap.BasicEntry(
                        CbEnchantments.cbHolderV((net.minecraft.class_6880) e.getKey()), e.getIntValue());
                }
            };
        }

        @Override
        public int size() {
            return inner.size();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class WEntries extends AbstractSet {
        final Set inner;

        WEntries(Set inner) {
            this.inner = inner;
        }

        @Override
        public Iterator iterator() {
            Iterator it = inner.iterator();
            return new Iterator() {
                public boolean hasNext() {
                    return it.hasNext();
                }

                public Object next() {
                    it.unimi.dsi.fastutil.objects.Object2IntMap.Entry e = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry) it.next();
                    return new it.unimi.dsi.fastutil.objects.AbstractObject2IntMap.BasicEntry(
                        CbEnchantments.cbHolderW((net.minecraft.class_6880) e.getKey()), e.getIntValue());
                }
            };
        }

        @Override
        public int size() {
            return inner.size();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class WPredicate implements Predicate {
        final Predicate inner;

        WPredicate(Predicate inner) {
            this.inner = inner;
        }

        @Override
        public boolean test(Object holder) {
            return inner.test(CbEnchantments.cbHolderV((net.minecraft.class_6880) holder));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class VPredicate implements Predicate {
        final Predicate inner;

        VPredicate(Predicate inner) {
            this.inner = inner;
        }

        @Override
        public boolean test(Object holder) {
            return inner.test(CbEnchantments.cbHolderW((net.minecraft.class_6880) holder));
        }
    }

    // ---- EnchantmentInstance ----

    /** EnchantmentInstance.enchantment (instanceFieldGetter): the 1.20.6 object of the instance's holder */
    public static class_1887 field_9093(net.minecraft.class_1889 instance) {
        return CbEnchantments.cbEnchantment(instance.field_9093);
    }

    // ---- EnchantmentPredicate ----

    /**
     * EnchantmentPredicate's 1.20.6 component (an optional enchantment holder) as 1.21's (an optional
     * set of them): a holder becomes the set of that one world holder; a set passes (adaptive).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional predicateEnchantments(Optional enchantment) {
        if (enchantment == null || enchantment.isEmpty() || enchantment.get() instanceof net.minecraft.class_6885) {
            return enchantment;
        }
        return Optional.of(net.minecraft.class_6885.method_40246(CbEnchantments.cbHolderW((net.minecraft.class_6880) enchantment.get())));
    }

    /**
     * EnchantmentPredicate.enchantment() (1.20.6's component, instanceRedirect): the one enchantment of
     * 1.21's set, as the 1.20.6 object's holder. A 1.21 predicate may name several (a tag, a list); 1.20.6's
     * named at most one and has no answer for more.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional<net.minecraft.class_6880<class_1887>> comp_1748(net.minecraft.class_2035 predicate) {
        Optional<net.minecraft.class_6885<net.minecraft.class_1887>> set = predicate.comp_2665();
        if (set.isEmpty()) {
            return Optional.empty();
        }
        if (set.get().method_40247() != 1 || set.get().method_45925().isPresent()) {
            throw new IllegalStateException("this enchantment predicate names a set of 1.21 enchantments (" + set.get()
                + "); 1.20.6's EnchantmentPredicate named at most one");
        }
        return Optional.of((net.minecraft.class_6880) CbEnchantments.cbHolderV(set.get().method_40240(0)));
    }
}
