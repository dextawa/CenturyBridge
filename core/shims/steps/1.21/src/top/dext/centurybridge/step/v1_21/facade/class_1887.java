package top.dext.centurybridge.step.v1_21.facade;

import java.util.Map;
import java.util.Optional;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;

/**
 * 1.20.6's Enchantment (net/minecraft/class_1887): every reference a 1.20.6 mod makes to the class is
 * renamed to this one (classRenames), because 1.21's class_1887 is a final record built from registry
 * data -- nothing can extend it, and its API is a different one.
 *
 * <p>The facade is the whole 1.20.6 class: the same constructor (an EnchantmentDefinition, the facade
 * {@link class_1887$class_9427}), the same static factories, the same final getters and the same
 * overridable behaviour, with 1.20.6's bodies. A mod subclasses it exactly as it subclassed
 * Enchantment and registers the object where it registered it -- BuiltInRegistries.ENCHANTMENT, which
 * reads as CenturyBridge's stand-in registry ({@link CbEnchantments#legacy()}, the same registry key).
 * When a world's enchantment registry loads, CenturyBridge adds a 1.21 enchantment record for every
 * mod enchantment, built from its definition and marked as standing for it; 1.21's enchantment code
 * then hands the record's questions and effects back to the mod's object (CbEnch* mixins, see
 * CbEnchantments).
 *
 * <p>The objects a 1.20.6 mod reads where 1.20.6 had its own enchantments -- Enchantments.SHARPNESS --
 * are facades too, of the very classes 1.20.6 made them of (CbEnchVanilla, generated from 1.20.6's
 * Enchantments), so instanceof DamageEnchantment and the subclasses' own 1.20.6 rules hold. They and
 * the facades of 1.21 enchantments no 1.20.6 class stands behind (a datapack's) are bound to the
 * world's record of the same key: their data-driven answers (levels, costs, weight, items, slots) are
 * the record's, so a datapack that changes a vanilla enchantment changes what a 1.20.6 mod reads too.
 *
 * <p>Member names are 1.20.6's intermediary names; 1.20.6's protected members are public here (an
 * access widener never reaches a CenturyBridge class, and a protected override still overrides a
 * public method).
 */
public class class_1887 implements net.minecraft.class_7695 {

    /** CB: a 1.20.6 mod's enchantment: CenturyBridge builds and registers its 1.21 record */
    public static final byte CB_MOD = 0;
    /** CB: one of 1.20.6's vanilla enchantments: bound to 1.21's record of the same key */
    public static final byte CB_VANILLA = 1;
    /** CB: a 1.21 enchantment no 1.20.6 class stands behind (a datapack's): its record is all there is */
    public static final byte CB_DATA = 2;

    /** 1.20.6 Enchantment.definition; null on a datapack enchantment's facade, whose definition is its 1.21 data */
    public final class_1887$class_9427 field_50013;
    /** 1.20.6 Enchantment.descriptionId (protected) */
    public String field_9084;
    /** 1.20.6 Enchantment.builtInRegistryHolder: the intrusive holder of CenturyBridge's stand-in registry */
    public final net.minecraft.class_6880.class_6883<class_1887> field_45769;
    /** CB: which of CB_MOD / CB_VANILLA / CB_DATA this object is (set by CbEnchantments) */
    public byte cb$kind = CB_MOD;

    // ---- 1.20.6's static factories ----

    /** constantCost(int) */
    public static net.minecraft.class_1887.class_9426 method_58440(int base) {
        return new net.minecraft.class_1887.class_9426(base, 0);
    }

    /** dynamicCost(int, int) */
    public static net.minecraft.class_1887.class_9426 method_58441(int base, int perLevel) {
        return new net.minecraft.class_1887.class_9426(base, perLevel);
    }

    /** definition(supportedItems, primaryItems, weight, maxLevel, minCost, maxCost, anvilCost, slots...) */
    public static class_1887$class_9427 method_58443(net.minecraft.class_6862<net.minecraft.class_1792> supportedItems,
            net.minecraft.class_6862<net.minecraft.class_1792> primaryItems, int weight, int maxLevel,
            net.minecraft.class_1887.class_9426 minCost, net.minecraft.class_1887.class_9426 maxCost, int anvilCost,
            net.minecraft.class_1304... slots) {
        return new class_1887$class_9427(supportedItems, Optional.of(primaryItems), weight, maxLevel, minCost, maxCost, anvilCost,
            net.minecraft.class_7701.field_40183, slots);
    }

    /** definition(supportedItems, weight, maxLevel, minCost, maxCost, anvilCost, slots...) */
    public static class_1887$class_9427 method_58442(net.minecraft.class_6862<net.minecraft.class_1792> supportedItems, int weight,
            int maxLevel, net.minecraft.class_1887.class_9426 minCost, net.minecraft.class_1887.class_9426 maxCost, int anvilCost,
            net.minecraft.class_1304... slots) {
        return new class_1887$class_9427(supportedItems, Optional.empty(), weight, maxLevel, minCost, maxCost, anvilCost,
            net.minecraft.class_7701.field_40183, slots);
    }

    /** definition(supportedItems, weight, maxLevel, minCost, maxCost, anvilCost, requiredFeatures, slots...) */
    public static class_1887$class_9427 method_58674(net.minecraft.class_6862<net.minecraft.class_1792> supportedItems, int weight,
            int maxLevel, net.minecraft.class_1887.class_9426 minCost, net.minecraft.class_1887.class_9426 maxCost, int anvilCost,
            net.minecraft.class_7699 requiredFeatures, net.minecraft.class_1304... slots) {
        return new class_1887$class_9427(supportedItems, Optional.empty(), weight, maxLevel, minCost, maxCost, anvilCost,
            requiredFeatures, slots);
    }

    /** byId(int): BuiltInRegistries.ENCHANTMENT.byId -- the stand-in registry answers the world's ids */
    public static class_1887 method_8191(int id) {
        return CbEnchantments.legacy().method_10200(id);
    }

    // ---- construction ----

    /** 1.20.6's only constructor: Enchantment(EnchantmentDefinition) */
    public class_1887(class_1887$class_9427 definition) {
        this.field_45769 = CbEnchantments.intrusiveHolder(this);
        this.field_50013 = definition;
    }

    /** CB: the facade of a 1.21 enchantment no 1.20.6 class stands behind (CbEnchantments.dataFacade) */
    public class_1887(CbEnchantments.DataOnly marker) {
        this.field_45769 = CbEnchantments.intrusiveHolder(this);
        this.field_50013 = null;
        this.cb$kind = CB_DATA;
    }

    /** the world's record for this enchantment when this object is bound to one (vanilla, datapack), else null */
    private net.minecraft.class_1887 cb$bound() {
        return cb$kind == CB_MOD ? null : CbEnchantments.boundRecord(this);
    }

    private class_1887$class_9427 cb$definition() {
        if (field_50013 == null) {
            throw new IllegalStateException("enchantment " + CbEnchantments.keyOf(this) + " is a 1.21 datapack enchantment and no"
                + " world registries are loaded: 1.21's data is all of its definition");
        }
        return field_50013;
    }

    // ---- 1.20.6's instance methods ----

    /** getSlotItems(LivingEntity): the definition's slots holding a non-empty stack */
    public Map<net.minecraft.class_1304, net.minecraft.class_1799> method_8185(net.minecraft.class_1309 entity) {
        net.minecraft.class_1887 r = cb$bound();
        if (r != null) {
            return r.method_8185(entity);
        }
        Map<net.minecraft.class_1304, net.minecraft.class_1799> map = com.google.common.collect.Maps.newEnumMap(net.minecraft.class_1304.class);
        for (net.minecraft.class_1304 slot : cb$definition().comp_2513) {
            net.minecraft.class_1799 stack = entity.method_6118(slot);
            if (!stack.method_7960()) {
                map.put(slot, stack);
            }
        }
        return map;
    }

    /** getSupportedItems(): the supported items' tag (a bound enchantment's set must be a tag to answer one) */
    public final net.minecraft.class_6862<net.minecraft.class_1792> method_56109() {
        net.minecraft.class_1887 r = cb$bound();
        if (r != null) {
            return r.comp_2687().comp_2506().method_45925().orElseThrow(() -> new UnsupportedOperationException(
                "enchantment " + CbEnchantments.keyOf(this) + " supports a list of items, not a tag: 1.20.6's getSupportedItems answers a tag"));
        }
        return cb$definition().comp_2506;
    }

    /** isPrimaryItem(ItemStack): no primary items, or the stack is one of them (1.20.6's rule) */
    public final boolean method_58444(net.minecraft.class_1799 stack) {
        net.minecraft.class_1887 r = cb$bound();
        if (r != null) {
            Optional<net.minecraft.class_6885<net.minecraft.class_1792>> primary = r.comp_2687().comp_2507();
            return primary.isEmpty() || stack.method_53187(primary.get());
        }
        Optional<net.minecraft.class_6862<net.minecraft.class_1792>> primary = cb$definition().comp_2507;
        return primary.isEmpty() || stack.method_31573(primary.get());
    }

    /** getWeight() */
    public final int method_58445() {
        net.minecraft.class_1887 r = cb$bound();
        return r != null ? r.method_58445() : cb$definition().comp_2508;
    }

    /** getAnvilCost() */
    public final int method_58446() {
        net.minecraft.class_1887 r = cb$bound();
        return r != null ? r.method_58446() : cb$definition().comp_2512;
    }

    /** getMinLevel() */
    public final int method_8187() {
        return 1;
    }

    /** getMaxLevel() */
    public final int method_8183() {
        net.minecraft.class_1887 r = cb$bound();
        return r != null ? r.method_8183() : cb$definition().comp_2509;
    }

    /** getMinCost(int) */
    public final int method_8182(int level) {
        net.minecraft.class_1887 r = cb$bound();
        return r != null ? r.method_8182(level) : cb$definition().comp_2510.method_58447(level);
    }

    /** getMaxCost(int) */
    public final int method_20742(int level) {
        net.minecraft.class_1887 r = cb$bound();
        return r != null ? r.method_20742(level) : cb$definition().comp_2511.method_58447(level);
    }

    /** getDamageProtection(int, DamageSource): 1.20.6's base answer; subclasses override */
    public int method_8181(int level, net.minecraft.class_1282 source) {
        return 0;
    }

    /** getDamageBonus(int, EntityType): 1.20.6's base answer; subclasses override */
    public float method_8196(int level, net.minecraft.class_1299<?> type) {
        return 0.0F;
    }

    /** isCompatibleWith(Enchantment): both sides agree */
    public final boolean method_8188(class_1887 other) {
        return this.method_8180(other) && other.method_8180(this);
    }

    /**
     * checkCompatibility(Enchantment) (protected in 1.20.6): 1.20.6's base rule, not itself; a datapack
     * enchantment's facade answers its record's exclusive set, the only rule 1.21 gave it.
     */
    public boolean method_8180(class_1887 other) {
        if (cb$kind == CB_DATA && this != other) {
            net.minecraft.class_1887 r = cb$bound();
            if (r != null) {
                return !r.comp_2688().method_40241(CbEnchantments.cbHolder(other));
            }
        }
        return this != other;
    }

    /** getOrCreateDescriptionId() (protected in 1.20.6): "enchantment.&lt;namespace&gt;.&lt;path&gt;" of its registry name */
    public String method_8190() {
        if (field_9084 == null) {
            String key = cb$kind == CB_DATA ? CbEnchantments.translationKeyOf(this) : null;
            field_9084 = key != null ? key
                : net.minecraft.class_156.method_646("enchantment", CbEnchantments.legacy().method_10221(this));
        }
        return field_9084;
    }

    /** getDescriptionId() */
    public String method_8184() {
        return method_8190();
    }

    /** getFullname(int): the name, red for a curse and gray otherwise, and the level unless both it and the maximum are 1 */
    public net.minecraft.class_2561 method_8179(int level) {
        if (cb$kind != CB_MOD && cb$bound() != null) {
            return net.minecraft.class_1887.method_8179(CbEnchantments.cbHolder(this), level);
        }
        net.minecraft.class_5250 name = net.minecraft.class_2561.method_43471(this.method_8184());
        if (this.method_8195()) {
            name.method_27692(net.minecraft.class_124.field_1061);
        } else {
            name.method_27692(net.minecraft.class_124.field_1080);
        }
        if (level != 1 || this.method_8183() != 1) {
            name.method_10852(net.minecraft.class_5244.field_41874).method_10852(net.minecraft.class_2561.method_43471("enchantment.level." + level));
        }
        return name;
    }

    /** canEnchant(ItemStack): the stack's item is in the supported items */
    public boolean method_8192(net.minecraft.class_1799 stack) {
        net.minecraft.class_1887 r = cb$bound();
        if (r != null) {
            return r.method_8192(stack);
        }
        return stack.method_7909().method_40131().method_40220(cb$definition().comp_2506);
    }

    /** doPostAttack(LivingEntity attacker, Entity target, int level): 1.20.6's base does nothing; subclasses override */
    public void method_8189(net.minecraft.class_1309 attacker, net.minecraft.class_1297 target, int level) {
    }

    /** doPostHurt(LivingEntity user, Entity attacker, int level): 1.20.6's base does nothing; subclasses override */
    public void method_8178(net.minecraft.class_1309 user, net.minecraft.class_1297 attacker, int level) {
    }

    /** doPostItemStackHurt(LivingEntity attacker, Entity target, int level): 1.20.6's base does nothing; subclasses override */
    public void method_58675(net.minecraft.class_1309 attacker, net.minecraft.class_1297 target, int level) {
    }

    /** isTreasureOnly() (a datapack enchantment's facade answers its record's #minecraft:treasure membership) */
    public boolean method_8193() {
        return cb$kind == CB_DATA && CbEnchantments.boundIn(this, net.minecraft.class_9636.field_51557);
    }

    /** isCurse() (datapack: #minecraft:curse) */
    public boolean method_8195() {
        return cb$kind == CB_DATA && CbEnchantments.boundIn(this, net.minecraft.class_9636.field_51551);
    }

    /** isTradeable() (datapack: #minecraft:tradeable) */
    public boolean method_25949() {
        return cb$kind != CB_DATA || CbEnchantments.boundIn(this, net.minecraft.class_9636.field_51545);
    }

    /** isDiscoverable() (datapack: #minecraft:on_random_loot) */
    public boolean method_25950() {
        return cb$kind != CB_DATA || CbEnchantments.boundIn(this, net.minecraft.class_9636.field_51550);
    }

    /** builtInRegistryHolder() */
    public net.minecraft.class_6880.class_6883<class_1887> method_53216() {
        return field_45769;
    }

    /** requiredFeatures() (FeatureElement): the definition's; a datapack enchantment is part of the world it is in */
    @Override
    public net.minecraft.class_7699 method_45322() {
        return field_50013 != null ? field_50013.comp_2548 : net.minecraft.class_7701.field_40183;
    }
}
