package top.dext.centurybridge.step.v1_21.facade;

import java.util.Objects;
import java.util.Optional;

/**
 * 1.20.6's Enchantment.EnchantmentDefinition (net/minecraft/class_1887$class_9427), renamed to this class in a
 * 1.20.6 mod. 1.21 kept the record's name for another shape: the items became HolderSets, the slots a
 * list of EquipmentSlotGroups, and the required feature flags are gone. A 1.20.6 mod only makes
 * definitions (Enchantment.definition(...), new EnchantmentDefinition(...)) and hands them to an
 * Enchantment constructor, which in a 1.20.6 mod is the facade {@link class_1887}; nothing in 1.21 hands a
 * mod the 1.21 record. So the definition stays 1.20.6's value here, and CbEnchantments turns it into the
 * 1.21 record when it registers the mod's enchantment.
 *
 * <p>A record in 1.20.6: the same components, accessors and value semantics (equals / hashCode over the
 * components, the slots array by identity as a record compares an array component). Written as a
 * final class because primaryItems ({@code comp_2507}) was a package-visible field, which a facade must
 * offer as a public one.
 */
public final class class_1887$class_9427 {

    public final net.minecraft.class_6862<net.minecraft.class_1792> comp_2506;
    public final Optional<net.minecraft.class_6862<net.minecraft.class_1792>> comp_2507;
    public final int comp_2508;
    public final int comp_2509;
    public final net.minecraft.class_1887.class_9426 comp_2510;
    public final net.minecraft.class_1887.class_9426 comp_2511;
    public final int comp_2512;
    public final net.minecraft.class_7699 comp_2548;
    public final net.minecraft.class_1304[] comp_2513;

    /** EnchantmentDefinition(supportedItems, primaryItems, weight, maxLevel, minCost, maxCost, anvilCost, requiredFeatures, slots) */
    public class_1887$class_9427(net.minecraft.class_6862<net.minecraft.class_1792> supportedItems,
            Optional<net.minecraft.class_6862<net.minecraft.class_1792>> primaryItems, int weight, int maxLevel,
            net.minecraft.class_1887.class_9426 minCost, net.minecraft.class_1887.class_9426 maxCost, int anvilCost,
            net.minecraft.class_7699 requiredFeatures, net.minecraft.class_1304[] slots) {
        this.comp_2506 = supportedItems;
        this.comp_2507 = primaryItems;
        this.comp_2508 = weight;
        this.comp_2509 = maxLevel;
        this.comp_2510 = minCost;
        this.comp_2511 = maxCost;
        this.comp_2512 = anvilCost;
        this.comp_2548 = requiredFeatures;
        this.comp_2513 = slots;
    }

    /** supportedItems() */
    public net.minecraft.class_6862<net.minecraft.class_1792> comp_2506() {
        return comp_2506;
    }

    /** primaryItems() */
    public Optional<net.minecraft.class_6862<net.minecraft.class_1792>> comp_2507() {
        return comp_2507;
    }

    /** weight() */
    public int comp_2508() {
        return comp_2508;
    }

    /** maxLevel() */
    public int comp_2509() {
        return comp_2509;
    }

    /** minCost() */
    public net.minecraft.class_1887.class_9426 comp_2510() {
        return comp_2510;
    }

    /** maxCost() */
    public net.minecraft.class_1887.class_9426 comp_2511() {
        return comp_2511;
    }

    /** anvilCost() */
    public int comp_2512() {
        return comp_2512;
    }

    /** requiredFeatures() */
    public net.minecraft.class_7699 comp_2548() {
        return comp_2548;
    }

    /** slots() */
    public net.minecraft.class_1304[] comp_2513() {
        return comp_2513;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof class_1887$class_9427 d)) {
            return false;
        }
        return comp_2508 == d.comp_2508 && comp_2509 == d.comp_2509 && comp_2512 == d.comp_2512
            && Objects.equals(comp_2506, d.comp_2506) && Objects.equals(comp_2507, d.comp_2507)
            && Objects.equals(comp_2510, d.comp_2510) && Objects.equals(comp_2511, d.comp_2511)
            && Objects.equals(comp_2548, d.comp_2548) && comp_2513 == d.comp_2513;
    }

    @Override
    public int hashCode() {
        int h = Objects.hashCode(comp_2506);
        h = 31 * h + Objects.hashCode(comp_2507);
        h = 31 * h + comp_2508;
        h = 31 * h + comp_2509;
        h = 31 * h + Objects.hashCode(comp_2510);
        h = 31 * h + Objects.hashCode(comp_2511);
        h = 31 * h + comp_2512;
        h = 31 * h + Objects.hashCode(comp_2548);
        h = 31 * h + System.identityHashCode(comp_2513);
        return h;
    }

    /** a record's toString as 1.20.6's runtime printed it: simple class name, intermediary component names */
    @Override
    public String toString() {
        return "class_9427[comp_2506=" + comp_2506 + ", comp_2507=" + comp_2507 + ", comp_2508=" + comp_2508
            + ", comp_2509=" + comp_2509 + ", comp_2510=" + comp_2510 + ", comp_2511=" + comp_2511 + ", comp_2512=" + comp_2512
            + ", comp_2548=" + comp_2548 + ", comp_2513=" + String.valueOf((Object) comp_2513) + "]";
    }
}
