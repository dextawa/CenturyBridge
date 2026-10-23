package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Optional;

/**
 * 1.20.4's potions on 1.20.5's.
 * <ul>
 * <li>The empty potion. 1.20.4 registered Potions.EMPTY as minecraft:empty -- the potion registry's
 *     default, so an unknown name was the empty potion -- and wrote no "Potion" for it; 1.20.5 has no
 *     empty potion (no potion is an absent one) and its registry's default is water. {@link #EMPTY} is
 *     1.20.4's empty potion again, unregistered: what 1.20.4 code reads for "no potion", compares
 *     against, and writes as none; {@link #byName} answers it where 1.20.4 did.</li>
 * <li>Potion.getName(prefix) became a static of the holder: a 1.20.4 potion class overriding getName is
 *     asked again where 1.20.5 names a potion (CbPotionNameMixin), its super call answers 1.20.4's body.</li>
 * <li>builtInRegistryHolder(): the registry's reference of the potion (CbItemHolders.builtIn).</li>
 * </ul>
 */
public final class CbPotions {

    /** a 1.20.4 potion class declaring getName(prefix) (adapter) */
    public interface Named {
        String method_8051(String prefix);
    }

    /** Potion's own name field (CbPotionNameMixin) */
    public interface NameField {
        String cbS4$name();
    }

    /** a 1.20.4 potion class declaring builtInRegistryHolder() (adapter) */
    public interface BuiltIn {
        @SuppressWarnings("rawtypes")
        net.minecraft.class_6880.class_6883 method_53211();
    }

    /** 1.20.4's Potions.EMPTY: no effects, no name, unregistered (1.20.5 registers no empty potion) */
    public static final net.minecraft.class_1842 EMPTY = new net.minecraft.class_1842();

    /** Potions.EMPTY_ID (a static field 1.20.4 left writable) */
    private static volatile net.minecraft.class_5321<net.minecraft.class_1842> emptyId =
        net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41215, new net.minecraft.class_2960("empty"));

    /** Potions.EMPTY */
    public static net.minecraft.class_1842 empty() {
        return EMPTY;
    }

    /** Potions.EMPTY_ID */
    public static net.minecraft.class_5321<net.minecraft.class_1842> emptyId() {
        return emptyId;
    }

    /** Potions.EMPTY_ID = key */
    public static void emptyId(net.minecraft.class_5321<net.minecraft.class_1842> key) {
        emptyId = key;
    }

    /** Potion.byName(name): the registered potion of that id, else -- unknown, or minecraft:empty -- the empty potion */
    public static net.minecraft.class_1842 byName(String name) {
        net.minecraft.class_2960 id = net.minecraft.class_2960.method_12829(name);
        return id == null ? EMPTY : net.minecraft.class_7923.field_41179.method_17966(id).orElse(EMPTY);
    }

    /** the id 1.20.4's potion registry gave a potion: its own, else (unregistered) the default, minecraft:empty */
    public static net.minecraft.class_2960 idOf(net.minecraft.class_1842 potion) {
        return net.minecraft.class_7923.field_41179.method_29113(potion)
            .map(k -> ((net.minecraft.class_5321<?>) k).method_29177())
            .orElseGet(() -> new net.minecraft.class_2960("empty"));
    }

    /** a 1.20.4 potion as 1.20.5's optional holder: the empty potion is none */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional holder(net.minecraft.class_1842 potion) {
        return potion == null || potion == EMPTY ? Optional.empty() : Optional.of(CbHolders.potion(potion));
    }

    /** 1.20.5's optional potion holder as 1.20.4's potion: none is the empty potion */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_1842 potion(Optional holder) {
        return holder == null || holder.isEmpty() ? EMPTY : (net.minecraft.class_1842) ((net.minecraft.class_6880) holder.get()).comp_349();
    }

    // ---------------------------------------------------------------- getName

    /** potion.getName(prefix): the class's own override, else 1.20.4's body */
    public static String getName(net.minecraft.class_1842 potion, String prefix) {
        return potion instanceof Named n ? n.method_8051(prefix) : superGetName(potion, prefix);
    }

    /** Potion.getName(prefix)'s 1.20.4 body: the prefix and the potion's name, else its registry path */
    public static String superGetName(net.minecraft.class_1842 potion, String prefix) {
        String name = ((NameField) (Object) potion).cbS4$name();
        return prefix + (name != null ? name : idOf(potion).method_12832());
    }

    /**
     * CbPotionNameMixin, Potion.getName(potion, prefix) (static in 1.20.5): a 1.20.4 class's own
     * getName(prefix) answers for its potion, as 1.20.4's virtual call did; null for any other potion.
     */
    @SuppressWarnings({"rawtypes"})
    public static String overriddenName(Optional potion, String prefix) {
        if (potion != null && potion.isPresent() && ((net.minecraft.class_6880) potion.get()).comp_349() instanceof Named n) {
            return n.method_8051(prefix);
        }
        return null;
    }

    // ---------------------------------------------------------------- builtInRegistryHolder

    /** potion.builtInRegistryHolder(): the class's own override, else the registry's reference (CbItemHolders.builtIn) */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_6880.class_6883 builtInRegistryHolder(net.minecraft.class_1842 potion) {
        return potion instanceof BuiltIn b ? b.method_53211() : superBuiltInRegistryHolder(potion);
    }

    /** Potion.builtInRegistryHolder()'s answer, past a class's own override */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880.class_6883 superBuiltInRegistryHolder(net.minecraft.class_1842 potion) {
        return CbItemHolders.builtIn(net.minecraft.class_7923.field_41179, potion);
    }

    private CbPotions() {
    }
}
