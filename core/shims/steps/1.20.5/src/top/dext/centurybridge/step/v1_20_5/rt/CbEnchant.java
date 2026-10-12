package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_5.facade.EnchantmentCategory;
import top.dext.centurybridge.step.v1_20_5.facade.EnchantmentRarity;
import top.dext.centurybridge.step.v1_20_5.facade.class_1310;

/**
 * 1.20.4's Enchantment API on 1.20.5's definition-based enchantments.
 * <p>
 * A 1.20.4 enchantment was a subclass constructed with a rarity, a category and its slots, whose
 * overridable methods answered its level range and enchanting costs; 1.20.5 builds one from an
 * EnchantmentDefinition (supported item tag, weight, max level, cost functions, anvil cost) and made
 * those methods final. A converted enchantment is constructed through {@link #definition} (or its
 * per-class siblings): the definition says what the 1.20.4 class said where a definition can say it,
 * and the instance remembers its 1.20.4 arguments ({@link Legacy}, kept by mixin/CbEnchantmentMixin).
 * For such an instance the final methods answer by 1.20.4's rules ({@link Body}: the class's own
 * formulas, which call the instance's own getMinCost where 1.20.4's did), the layer's access widener
 * re-opens them so a mod's override is what the game calls, and its items are decided by its 1.20.4
 * category. An enchantment 1.20.5 itself made keeps every 1.20.5 answer.
 */
public final class CbEnchant {

    // ---------------------------------------------------------------- what a converted one carries

    /**
     * A converted enchantment's 1.20.4 constructor arguments. damageType is a 1.20.4 DamageEnchantment's
     * type (0 all, 1 undead, 2 arthropods -- whatever int the mod passed), {@link #NO_TYPE} for every
     * other class.
     */
    public record Legacy(EnchantmentRarity rarity, EnchantmentCategory category, int damageType) {
    }

    /** {@link Legacy#damageType} of an enchantment that is no DamageEnchantment */
    public static final int NO_TYPE = Integer.MIN_VALUE;

    /** mixin/CbEnchantmentMixin: the instance's {@link Legacy}, null for an enchantment 1.20.5 made */
    public interface Carrier {
        Legacy cbS4$legacy();
    }

    /** a class declaring 1.20.4's Enchantment.getRarity() (the converter adds it to a mod's; the facades have it) */
    public interface RarityOverride {
        EnchantmentRarity method_8186();
    }

    /** a class declaring 1.20.4's Enchantment.getDamageBonus(int, MobType) */
    public interface DamageBonus {
        float method_8196(int level, class_1310 mobType);
    }

    /** mixin/CbEnchantDamageMixin: a DamageEnchantment's entity type tag (empty: sharpness) */
    public interface DamageTargets {
        Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> cbS4$targets();
    }

    /** definitions made for a 1.20.4 constructor, until that constructor's Enchantment takes its own */
    private static final Map<net.minecraft.class_1887.class_9427, Legacy> PENDING =
        Collections.synchronizedMap(new IdentityHashMap<>());

    /** CbEnchantmentMixin, as the Enchantment constructor returns: the 1.20.4 arguments its definition came from */
    public static Legacy claim(net.minecraft.class_1887.class_9427 definition) {
        return PENDING.remove(definition);
    }

    /** the 1.20.4 arguments an enchantment was made with, null for one 1.20.5 made */
    public static Legacy legacy(net.minecraft.class_1887 e) {
        return ((Carrier) e).cbS4$legacy();
    }

    // ---------------------------------------------------------------- 1.20.4's classes

    private static final int CONST = 0;
    private static final int THIS_MIN = 1;
    private static final int BASE_MIN = 2;

    /** 1.20.4's DamageEnchantment MIN_COST / LEVEL_COST / LEVEL_COST_SPAN, by type */
    private static final int[] DAMAGE_MIN = {1, 5, 5};
    private static final int[] DAMAGE_LEVEL = {11, 8, 8};
    private static final int[] DAMAGE_SPAN = {20, 20, 20};

    /** 1.20.4's ProtectionEnchantment.Type minCost / levelCost, by ordinal (ALL, FIRE, FALL, EXPLOSION, PROJECTILE) */
    private static final int[] PROTECTION_MIN = {1, 10, 5, 5, 3};
    private static final int[] PROTECTION_LEVEL = {11, 8, 6, 8, 6};

    /**
     * Each 1.20.4 enchantment class's own answers, read off its bytecode: max level, minimum cost as
     * base + perLevel * (level - 1), maximum cost as a constant, as getMinCost(level) + c (1.20.4 called
     * the instance's own, virtually) or as Enchantment's 1 + 10 * level + c (super.getMinCost), and the
     * item rule its canEnchant added to its category's. getMinLevel was 1 in every one.
     */
    enum Body {
        BASE(1, 11, 10, THIS_MIN, 5),
        ARROW_FIRE(1, 20, 0, CONST, 50),
        ARROW_DAMAGE(5, 1, 10, THIS_MIN, 15),
        ARROW_KNOCKBACK(2, 12, 20, THIS_MIN, 25),
        ARROW_INFINITE(1, 20, 0, CONST, 50),
        ARROW_PIERCING(4, 1, 10, CONST, 50),
        DAMAGE(5, 0, 0, THIS_MIN, 0) {
            @Override
            int minCost(net.minecraft.class_1887 e, int level) {
                int t = damageType1204(e);
                return DAMAGE_MIN[t] + (level - 1) * DAMAGE_LEVEL[t];
            }

            @Override
            int maxCost(net.minecraft.class_1887 e, int level) {
                return e.method_8182(level) + DAMAGE_SPAN[damageType1204(e)];
            }

            @Override
            boolean canEnchant(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
                return stack.method_7909() instanceof net.minecraft.class_1743 || super.canEnchant(e, stack);
            }
        },
        BINDING_CURSE(1, 25, 0, CONST, 50) {
            @Override
            boolean canEnchant(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
                return !stack.method_31574(net.minecraft.class_1802.field_8255) && super.canEnchant(e, stack);
            }
        },
        DIGGING(5, 1, 10, BASE_MIN, 50) {
            @Override
            boolean canEnchant(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
                return stack.method_31574(net.minecraft.class_1802.field_8868) || super.canEnchant(e, stack);
            }
        },
        DIG_DURABILITY(3, 5, 8, BASE_MIN, 50) {
            @Override
            boolean canEnchant(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
                return stack.method_7963() || super.canEnchant(e, stack);
            }
        },
        FIRE_ASPECT(2, 10, 20, BASE_MIN, 50),
        FROST_WALKER(2, 10, 10, THIS_MIN, 15),
        FISHING_SPEED(3, 15, 9, BASE_MIN, 50),
        LOOT_BONUS(3, 15, 9, BASE_MIN, 50),
        KNOCKBACK(2, 5, 20, BASE_MIN, 50),
        MULTI_SHOT(1, 20, 0, CONST, 50),
        MENDING(1, 25, 25, THIS_MIN, 50),
        PROTECTION(4, 0, 0, THIS_MIN, 0) {
            @Override
            int minCost(net.minecraft.class_1887 e, int level) {
                int t = ((net.minecraft.class_1900) e).field_9133.ordinal();
                return PROTECTION_MIN[t] + (level - 1) * PROTECTION_LEVEL[t];
            }

            @Override
            int maxCost(net.minecraft.class_1887 e, int level) {
                return e.method_8182(level) + PROTECTION_LEVEL[((net.minecraft.class_1900) e).field_9133.ordinal()];
            }
        },
        OXYGEN(3, 10, 10, THIS_MIN, 30),
        SWEEPING_EDGE(3, 5, 9, THIS_MIN, 15),
        QUICK_CHARGE(3, 12, 20, CONST, 50),
        CHANNELING(1, 25, 0, CONST, 50),
        THORNS(3, 10, 20, BASE_MIN, 50) {
            @Override
            boolean canEnchant(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
                // an ArmorItem in 1.20.4's sense -- the ARMOR category's own rule
                return EnchantmentCategory.field_9068.method_8177(stack.method_7909()) || super.canEnchant(e, stack);
            }
        },
        LOYALTY(3, 12, 7, CONST, 50),
        IMPALER(5, 1, 8, THIS_MIN, 20),
        UNTOUCHING(1, 15, 0, BASE_MIN, 50),
        RIPTIDE(3, 17, 7, CONST, 50),
        VANISHING_CURSE(1, 25, 0, CONST, 50),
        WATER_WORKER(1, 1, 0, THIS_MIN, 40),
        WATER_WALKER(3, 10, 10, THIS_MIN, 15),
        SOUL_SPEED(3, 10, 10, THIS_MIN, 15),
        SWIFT_SNEAK(3, 25, 25, THIS_MIN, 50);

        final int maxLevel;
        final int minBase;
        final int minPer;
        final int maxKind;
        final int maxC;

        Body(int maxLevel, int minBase, int minPer, int maxKind, int maxC) {
            this.maxLevel = maxLevel;
            this.minBase = minBase;
            this.minPer = minPer;
            this.maxKind = maxKind;
            this.maxC = maxC;
        }

        int minCost(net.minecraft.class_1887 e, int level) {
            return minBase + (level - 1) * minPer;
        }

        int maxCost(net.minecraft.class_1887 e, int level) {
            return switch (maxKind) {
                case CONST -> maxC;
                case THIS_MIN -> e.method_8182(level) + maxC;
                default -> 1 + level * 10 + maxC;
            };
        }

        /** 1.20.4's canEnchant(stack): the category's rule (Enchantment's body), plus the class's own */
        boolean canEnchant(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
            return category(e).method_8177(stack.method_7909());
        }

        /** the definition's cost functions, where a definition can say what the class says */
        net.minecraft.class_1887.class_9426 minCostFunction() {
            return net.minecraft.class_1887.method_58441(minBase, minPer);
        }

        net.minecraft.class_1887.class_9426 maxCostFunction() {
            return switch (maxKind) {
                case CONST -> net.minecraft.class_1887.method_58440(maxC);
                case THIS_MIN -> net.minecraft.class_1887.method_58441(minBase + maxC, minPer);
                default -> net.minecraft.class_1887.method_58441(11 + maxC, 10);
            };
        }
    }

    /** the nearest 1.20.4 class an enchantment's class is: its own answers stand in for the ones 1.20.5 made final */
    private static final ClassValue<Body> BODY = new ClassValue<>() {
        @Override
        protected Body computeValue(Class<?> c) {
            Body b = BODIES.get(c);
            return b != null ? b : computeValueOf(c.getSuperclass());
        }

        private Body computeValueOf(Class<?> c) {
            return c == null ? Body.BASE : get(c);
        }
    };

    private static final Map<Class<?>, Body> BODIES = Map.ofEntries(
        Map.entry(net.minecraft.class_1887.class, Body.BASE),
        Map.entry(net.minecraft.class_1880.class, Body.ARROW_INFINITE),
        Map.entry(net.minecraft.class_1881.class, Body.ARROW_PIERCING),
        Map.entry(net.minecraft.class_1882.class, Body.DAMAGE),
        Map.entry(net.minecraft.class_1883.class, Body.BINDING_CURSE),
        Map.entry(net.minecraft.class_1885.class, Body.DIG_DURABILITY),
        Map.entry(net.minecraft.class_1894.class, Body.FROST_WALKER),
        Map.entry(net.minecraft.class_1896.class, Body.LOOT_BONUS),
        Map.entry(net.minecraft.class_1898.class, Body.MULTI_SHOT),
        Map.entry(net.minecraft.class_1899.class, Body.MENDING),
        Map.entry(net.minecraft.class_1900.class, Body.PROTECTION),
        Map.entry(net.minecraft.class_1906.class, Body.THORNS),
        Map.entry(net.minecraft.class_1909.class, Body.UNTOUCHING),
        Map.entry(net.minecraft.class_1910.class, Body.RIPTIDE),
        Map.entry(net.minecraft.class_1911.class, Body.VANISHING_CURSE),
        Map.entry(net.minecraft.class_1913.class, Body.WATER_WALKER),
        Map.entry(net.minecraft.class_4948.class, Body.SOUL_SPEED),
        Map.entry(net.minecraft.class_7267.class, Body.SWIFT_SNEAK),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.ArrowFireEnchantment.class, Body.ARROW_FIRE),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.ArrowDamageEnchantment.class, Body.ARROW_DAMAGE),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.ArrowKnockbackEnchantment.class, Body.ARROW_KNOCKBACK),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.DiggingEnchantment.class, Body.DIGGING),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.FireAspectEnchantment.class, Body.FIRE_ASPECT),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.FishingSpeedEnchantment.class, Body.FISHING_SPEED),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.KnockbackEnchantment.class, Body.KNOCKBACK),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.OxygenEnchantment.class, Body.OXYGEN),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.SweepingEdgeEnchantment.class, Body.SWEEPING_EDGE),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.QuickChargeEnchantment.class, Body.QUICK_CHARGE),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.TridentChannelingEnchantment.class, Body.CHANNELING),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.TridentLoyaltyEnchantment.class, Body.LOYALTY),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.TridentImpalerEnchantment.class, Body.IMPALER),
        Map.entry(top.dext.centurybridge.step.v1_20_5.facade.WaterWorkerEnchantment.class, Body.WATER_WORKER));

    private static Body body(net.minecraft.class_1887 e) {
        return BODY.get(e.getClass());
    }

    // ---------------------------------------------------------------- construction (super-constructor reshapes)

    /**
     * new Enchantment(rarity, category, slots): a definition saying what 1.20.4's Enchantment said -- the
     * rarity's weight and anvil cost, max level 1, costs 1 + 10 * level and that + 5, the slots -- with
     * the item tag 1.20.5 uses for the category ({@link #tagOf}), no primary items.
     */
    public static net.minecraft.class_1887.class_9427 definition(EnchantmentRarity rarity, EnchantmentCategory category,
                                                                 net.minecraft.class_1304[] slots) {
        return define(Body.BASE, rarity, category, slots, NO_TYPE);
    }

    /** a facade's constructor (a 1.20.4 class 1.20.5 dropped): its own answers, from the table */
    public static net.minecraft.class_1887.class_9427 definition(Class<? extends net.minecraft.class_1887> facade,
                                                                 EnchantmentRarity rarity, EnchantmentCategory category,
                                                                 net.minecraft.class_1304[] slots) {
        return define(BODIES.get(facade), rarity, category, slots, NO_TYPE);
    }

    private static net.minecraft.class_1887.class_9427 define(Body body, EnchantmentRarity rarity, EnchantmentCategory category,
                                                              net.minecraft.class_1304[] slots, int damageType) {
        if (rarity == null) {
            throw new NullPointerException("a 1.20.4 enchantment needs a rarity (its weight and anvil cost)");
        }
        net.minecraft.class_1887.class_9426 min = body.minCostFunction();
        net.minecraft.class_1887.class_9426 max = body.maxCostFunction();
        if (body == Body.DAMAGE) {
            int t = Math.max(0, Math.min(2, damageType));
            min = net.minecraft.class_1887.method_58441(DAMAGE_MIN[t], DAMAGE_LEVEL[t]);
            max = net.minecraft.class_1887.method_58441(DAMAGE_MIN[t] + DAMAGE_SPAN[t], DAMAGE_LEVEL[t]);
        }
        net.minecraft.class_1887.class_9427 def = net.minecraft.class_1887.method_58442(tagOf(category), rarity.method_8197(),
            body.maxLevel, min, max, anvilCost(rarity), slots);
        PENDING.put(def, new Legacy(rarity, category, damageType));
        return def;
    }

    /** new ArrowInfiniteEnchantment(rarity, slots) (1.20.4: category BOW) */
    public static net.minecraft.class_1887.class_9427 infinity(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.ARROW_INFINITE, rarity, EnchantmentCategory.field_9070, slots, NO_TYPE);
    }

    /** new ArrowPiercingEnchantment(rarity, slots) (CROSSBOW) */
    public static net.minecraft.class_1887.class_9427 piercing(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.ARROW_PIERCING, rarity, EnchantmentCategory.field_9081, slots, NO_TYPE);
    }

    /** new DamageEnchantment(rarity, type, slots) (WEAPON) */
    public static net.minecraft.class_1887.class_9427 damage(EnchantmentRarity rarity, int type, net.minecraft.class_1304[] slots) {
        return define(Body.DAMAGE, rarity, EnchantmentCategory.field_9074, slots, type);
    }

    /**
     * The entity type tag 1.20.5's DamageEnchantment takes for a 1.20.4 type: none for 0 (all), the smite
     * and bane tags for 1 and 2. 1.20.4 accepted any int and only failed when a cost was asked; such a
     * type gets none here, and the instance keeps the type it was given for every 1.20.4 answer.
     */
    public static Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> damageTargets(int type) {
        return switch (type) {
            case 1 -> Optional.of(net.minecraft.class_3483.field_49931);
            case 2 -> Optional.of(net.minecraft.class_3483.field_48285);
            default -> Optional.empty();
        };
    }

    /** new BindingCurseEnchantment(rarity, slots) (WEARABLE) */
    public static net.minecraft.class_1887.class_9427 bindingCurse(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.BINDING_CURSE, rarity, EnchantmentCategory.field_9078, slots, NO_TYPE);
    }

    /** new DigDurabilityEnchantment(rarity, slots) (BREAKABLE) */
    public static net.minecraft.class_1887.class_9427 unbreaking(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.DIG_DURABILITY, rarity, EnchantmentCategory.field_9082, slots, NO_TYPE);
    }

    /** new FrostWalkerEnchantment(rarity, slots) (ARMOR_FEET) */
    public static net.minecraft.class_1887.class_9427 frostWalker(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.FROST_WALKER, rarity, EnchantmentCategory.field_9079, slots, NO_TYPE);
    }

    /** new LootBonusEnchantment(rarity, category, slots) */
    public static net.minecraft.class_1887.class_9427 lootBonus(EnchantmentRarity rarity, EnchantmentCategory category,
                                                                net.minecraft.class_1304[] slots) {
        return define(Body.LOOT_BONUS, rarity, category, slots, NO_TYPE);
    }

    /** new MultiShotEnchantment(rarity, slots) (CROSSBOW) */
    public static net.minecraft.class_1887.class_9427 multishot(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.MULTI_SHOT, rarity, EnchantmentCategory.field_9081, slots, NO_TYPE);
    }

    /** new MendingEnchantment(rarity, slots) (BREAKABLE) */
    public static net.minecraft.class_1887.class_9427 mending(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.MENDING, rarity, EnchantmentCategory.field_9082, slots, NO_TYPE);
    }

    /** new ProtectionEnchantment(rarity, type, slots) (ARMOR_FEET for FALL, else ARMOR) */
    public static net.minecraft.class_1887.class_9427 protection(EnchantmentRarity rarity, net.minecraft.class_1900.class_1901 type,
                                                                 net.minecraft.class_1304[] slots) {
        EnchantmentCategory c = type == net.minecraft.class_1900.class_1901.field_9140
            ? EnchantmentCategory.field_9079 : EnchantmentCategory.field_9068;
        int t = type.ordinal();
        net.minecraft.class_1887.class_9427 def = net.minecraft.class_1887.method_58442(tagOf(c), rarity.method_8197(),
            Body.PROTECTION.maxLevel, net.minecraft.class_1887.method_58441(PROTECTION_MIN[t], PROTECTION_LEVEL[t]),
            net.minecraft.class_1887.method_58441(PROTECTION_MIN[t] + PROTECTION_LEVEL[t], PROTECTION_LEVEL[t]),
            anvilCost(rarity), slots);
        PENDING.put(def, new Legacy(rarity, c, NO_TYPE));
        return def;
    }

    /** new ThornsEnchantment(rarity, slots) (ARMOR_CHEST) */
    public static net.minecraft.class_1887.class_9427 thorns(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.THORNS, rarity, EnchantmentCategory.field_9071, slots, NO_TYPE);
    }

    /** new UntouchingEnchantment(rarity, slots) (DIGGER) */
    public static net.minecraft.class_1887.class_9427 silkTouch(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.UNTOUCHING, rarity, EnchantmentCategory.field_9069, slots, NO_TYPE);
    }

    /** new TridentRiptideEnchantment(rarity, slots) (TRIDENT) */
    public static net.minecraft.class_1887.class_9427 riptide(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.RIPTIDE, rarity, EnchantmentCategory.field_9073, slots, NO_TYPE);
    }

    /** new VanishingCurseEnchantment(rarity, slots) (VANISHABLE) */
    public static net.minecraft.class_1887.class_9427 vanishingCurse(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.VANISHING_CURSE, rarity, EnchantmentCategory.field_23747, slots, NO_TYPE);
    }

    /** new WaterWalkerEnchantment(rarity, slots) (ARMOR_FEET) */
    public static net.minecraft.class_1887.class_9427 depthStrider(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.WATER_WALKER, rarity, EnchantmentCategory.field_9079, slots, NO_TYPE);
    }

    /** new SoulSpeedEnchantment(rarity, slots) (ARMOR_FEET) */
    public static net.minecraft.class_1887.class_9427 soulSpeed(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.SOUL_SPEED, rarity, EnchantmentCategory.field_9079, slots, NO_TYPE);
    }

    /** new SwiftSneakEnchantment(rarity, slots) (ARMOR_LEGS) */
    public static net.minecraft.class_1887.class_9427 swiftSneak(EnchantmentRarity rarity, net.minecraft.class_1304[] slots) {
        return define(Body.SWIFT_SNEAK, rarity, EnchantmentCategory.field_9076, slots, NO_TYPE);
    }

    /** a placeholder item tag no item is in: what a 1.20.4 enchantment made without a category is "for" */
    private static final net.minecraft.class_6862<net.minecraft.class_1792> NO_CATEGORY =
        net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41197, new net.minecraft.class_2960("centurybridge", "legacy/no_category"));

    /**
     * The item tag 1.20.5 gave its own enchantments of each 1.20.4 category (1.20.5's Enchantments:
     * protection is #enchantable/armor, feather falling #enchantable/foot_armor, sharpness's primary
     * items #enchantable/sword, efficiency #enchantable/mining, ...) -- what the creative tabs and other
     * readers of a definition's items look for. The items themselves are decided by the category.
     */
    public static net.minecraft.class_6862<net.minecraft.class_1792> tagOf(EnchantmentCategory c) {
        if (c == null) {
            return NO_CATEGORY;
        }
        return switch (c) {
            case field_9068 -> net.minecraft.class_3489.field_48303;
            case field_9079 -> net.minecraft.class_3489.field_48299;
            case field_9076 -> net.minecraft.class_3489.field_48300;
            case field_9071 -> net.minecraft.class_3489.field_48301;
            case field_9080 -> net.minecraft.class_3489.field_48302;
            case field_9074 -> net.minecraft.class_3489.field_48304;
            case field_9069 -> net.minecraft.class_3489.field_48306;
            case field_9072 -> net.minecraft.class_3489.field_48308;
            case field_9073 -> net.minecraft.class_3489.field_48309;
            case field_9082 -> net.minecraft.class_3489.field_48310;
            case field_9070 -> net.minecraft.class_3489.field_48311;
            case field_9078 -> net.minecraft.class_3489.field_48312;
            case field_9081 -> net.minecraft.class_3489.field_48313;
            case field_23747 -> net.minecraft.class_3489.field_48314;
        };
    }

    /** 1.20.4's anvil cost multiplier by rarity (AnvilMenu.createResult): 1, 2, 4, 8 */
    public static int anvilCost(EnchantmentRarity r) {
        return switch (r) {
            case field_9087 -> 1;
            case field_9090 -> 2;
            case field_9088 -> 4;
            case field_9091 -> 8;
        };
    }

    // ---------------------------------------------------------------- the final methods (CbEnchantmentMixin)

    /** getMaxLevel() of a converted enchantment: its 1.20.4 class's */
    public static int maxLevel(net.minecraft.class_1887 e) {
        return body(e).maxLevel;
    }

    /** getMinCost(level) of a converted enchantment */
    public static int minCost(net.minecraft.class_1887 e, int level) {
        return body(e).minCost(e, level);
    }

    /** getMaxCost(level) of a converted enchantment (calls its own getMinCost where 1.20.4's did) */
    public static int maxCost(net.minecraft.class_1887 e, int level) {
        return body(e).maxCost(e, level);
    }

    /** getWeight() of a converted enchantment: its rarity's (its own getRarity where it overrides it) */
    public static int weight(net.minecraft.class_1887 e) {
        return getRarity(e).method_8197();
    }

    /** getAnvilCost() of a converted enchantment: its rarity's 1.20.4 anvil multiplier */
    public static int anvilCost(net.minecraft.class_1887 e) {
        return anvilCost(getRarity(e));
    }

    /** canEnchant(stack) of a converted enchantment, its class leaving it alone: its 1.20.4 class's */
    public static boolean canEnchant(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
        return body(e).canEnchant(e, stack);
    }

    /**
     * isPrimaryItem(stack) of a converted enchantment: the items the enchanting table offered it for --
     * 1.20.4's table asked the category alone (EnchantmentHelper.getAvailableEnchantmentResults).
     */
    public static boolean isPrimaryItem(net.minecraft.class_1887 e, net.minecraft.class_1799 stack) {
        return category(e).method_8177(stack.method_7909());
    }

    // ---------------------------------------------------------------- rarity, category, type fields

    /** enchantment.getRarity(): the class's own 1.20.4 override, else what 1.20.4's Enchantment answered */
    public static EnchantmentRarity getRarity(net.minecraft.class_1887 e) {
        return e instanceof RarityOverride r ? r.method_8186() : superGetRarity(e);
    }

    /**
     * Enchantment.getRarity()'s 1.20.4 body (super.getRarity() too): the rarity the enchantment was made
     * with. One 1.20.5 made has a weight instead; 1.20.5's own enchantments took theirs from their
     * 1.20.4 rarity (10, 5, 2, 1), and any other weight answers the rarity of the nearest one not above it.
     */
    public static EnchantmentRarity superGetRarity(net.minecraft.class_1887 e) {
        Legacy l = legacy(e);
        if (l != null) {
            return l.rarity();
        }
        int w = e.method_58445();
        return w >= 10 ? EnchantmentRarity.field_9087 : w >= 5 ? EnchantmentRarity.field_9090
            : w >= 2 ? EnchantmentRarity.field_9088 : EnchantmentRarity.field_9091;
    }

    /** 1.20.4's categories of the vanilla enchantments, by their 1.20.5 ids (sweeping became sweeping_edge) */
    private static final Map<String, EnchantmentCategory> VANILLA = vanillaCategories();

    private static Map<String, EnchantmentCategory> vanillaCategories() {
        Map<String, EnchantmentCategory> m = new LinkedHashMap<>();
        for (String id : new String[] {"protection", "fire_protection", "blast_protection", "projectile_protection"}) {
            m.put(id, EnchantmentCategory.field_9068);
        }
        for (String id : new String[] {"feather_falling", "depth_strider", "frost_walker", "soul_speed"}) {
            m.put(id, EnchantmentCategory.field_9079);
        }
        m.put("swift_sneak", EnchantmentCategory.field_9076);
        m.put("thorns", EnchantmentCategory.field_9071);
        m.put("respiration", EnchantmentCategory.field_9080);
        m.put("aqua_affinity", EnchantmentCategory.field_9080);
        m.put("binding_curse", EnchantmentCategory.field_9078);
        for (String id : new String[] {"sharpness", "smite", "bane_of_arthropods", "knockback", "fire_aspect", "looting", "sweeping_edge"}) {
            m.put(id, EnchantmentCategory.field_9074);
        }
        for (String id : new String[] {"efficiency", "silk_touch", "fortune"}) {
            m.put(id, EnchantmentCategory.field_9069);
        }
        m.put("unbreaking", EnchantmentCategory.field_9082);
        m.put("mending", EnchantmentCategory.field_9082);
        for (String id : new String[] {"power", "punch", "flame", "infinity"}) {
            m.put(id, EnchantmentCategory.field_9070);
        }
        m.put("luck_of_the_sea", EnchantmentCategory.field_9072);
        m.put("lure", EnchantmentCategory.field_9072);
        for (String id : new String[] {"loyalty", "impaling", "riptide", "channeling"}) {
            m.put(id, EnchantmentCategory.field_9073);
        }
        for (String id : new String[] {"multishot", "quick_charge", "piercing"}) {
            m.put(id, EnchantmentCategory.field_9081);
        }
        m.put("vanishing_curse", EnchantmentCategory.field_23747);
        return Map.copyOf(m);
    }

    /**
     * enchantment.category: what a converted one was made with; for a vanilla one, its 1.20.4 category;
     * for any other (1.20.5's mace enchantments, a 1.20.5 mod's) the category whose 1.20.5 tag its
     * definition names, and null when it names none -- such an enchantment has no 1.20.4 category.
     */
    public static EnchantmentCategory category(net.minecraft.class_1887 e) {
        Legacy l = legacy(e);
        if (l != null) {
            return l.category();
        }
        net.minecraft.class_2960 id = net.minecraft.class_7923.field_41176.method_10221(e);
        if (id != null && id.method_12836().equals("minecraft")) {
            EnchantmentCategory c = VANILLA.get(id.method_12832());
            if (c != null) {
                return c;
            }
        }
        net.minecraft.class_6862<net.minecraft.class_1792> tag = e.method_56109();
        for (EnchantmentCategory c : EnchantmentCategory.values()) {
            if (tagOf(c) == tag) {
                return c;
            }
        }
        return null;
    }

    /**
     * damageEnchantment.type: the type a converted one was made with; for one 1.20.5 made, 0 (all) without
     * an entity tag, 1 for the smite tag, 2 for the bane tag, -1 for any other tag (1.20.5's impaling is a
     * DamageEnchantment; 1.20.4's was not one).
     */
    public static int damageType(net.minecraft.class_1882 e) {
        Legacy l = legacy(e);
        if (l != null && l.damageType() != NO_TYPE) {
            return l.damageType();
        }
        Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> t = ((DamageTargets) e).cbS4$targets();
        if (t.isEmpty()) {
            return 0;
        }
        return t.get() == net.minecraft.class_3483.field_49931 ? 1 : t.get() == net.minecraft.class_3483.field_48285 ? 2 : -1;
    }

    private static int damageType1204(net.minecraft.class_1887 e) {
        return damageType((net.minecraft.class_1882) e);
    }

    /** ProtectionEnchantment.Type.getMinCost() (1.20.5 dropped it): 1.20.4's per type */
    public static int protectionMinCost(net.minecraft.class_1900.class_1901 type) {
        return PROTECTION_MIN[type.ordinal()];
    }

    /** ProtectionEnchantment.Type.getLevelCost() */
    public static int protectionLevelCost(net.minecraft.class_1900.class_1901 type) {
        return PROTECTION_LEVEL[type.ordinal()];
    }

    // ---------------------------------------------------------------- damage bonus against a MobType

    /**
     * 1.20.4's MobType of the entity type 1.20.5 asks a bonus against (the override hook's argument
     * wrap): what the attacked entity's class declares (CbRestMobType.targetDeclared, set around the
     * attack), else the type's tags; no type at all (1.20.5's item tooltips) is 1.20.4's UNDEFINED.
     */
    public static class_1310 mobType(net.minecraft.class_1299<?> type) {
        if (type == null) {
            return class_1310.field_6290;
        }
        class_1310 d = CbRestMobType.targetDeclared(type);
        return d != null ? d : CbRestMobType.of(type);
    }

    /**
     * enchantment.getDamageBonus(level, mobType): the class's own 1.20.4 method where it has one (a
     * converted override, a facade); a DamageEnchantment by its type, as 1.20.4's; any other 1.20.5
     * enchantment, which only knows entity types, asked about an entity type of that MobType (UNDEFINED
     * is no type, as 1.20.5's own tooltips ask).
     */
    public static float getDamageBonus(net.minecraft.class_1887 e, int level, class_1310 mobType) {
        if (e instanceof DamageBonus d) {
            return d.method_8196(level, mobType);
        }
        if (e instanceof net.minecraft.class_1882 d) {
            int t = damageType(d);
            if (t != -1) {
                return damageBonus1204(t, level, mobType);
            }
            if (((DamageTargets) d).cbS4$targets().filter(x -> x == net.minecraft.class_3483.field_48284).isPresent()) {
                return mobType == class_1310.field_6292 ? level * 2.5F : 0.0F; // 1.20.4's TridentImpalerEnchantment
            }
        }
        return e.method_8196(level, representative(mobType));
    }

    /** super.getDamageBonus(level, mobType) in a mod subclass of Enchantment: 1.20.4's body */
    public static float superDamageBonus(net.minecraft.class_1887 e, int level, class_1310 mobType) {
        return 0.0F;
    }

    /** super.getDamageBonus(level, mobType) in a mod subclass of DamageEnchantment: 1.20.4's body by type */
    public static float superDamageBonusDamage(net.minecraft.class_1882 e, int level, class_1310 mobType) {
        return damageBonus1204(damageType(e), level, mobType);
    }

    /** 1.20.4's DamageEnchantment.getDamageBonus */
    public static float damageBonus1204(int type, int level, class_1310 mobType) {
        if (type == 0) {
            return 1.0F + Math.max(0, level - 1) * 0.5F;
        }
        if (type == 1 && mobType == class_1310.field_6289) {
            return level * 2.5F;
        }
        if (type == 2 && mobType == class_1310.field_6293) {
            return level * 2.5F;
        }
        return 0.0F;
    }

    /** an entity type of each MobType (zombie, spider, pillager, guardian), none for UNDEFINED */
    private static net.minecraft.class_1299<?> representative(class_1310 t) {
        if (t == class_1310.field_6289) {
            return net.minecraft.class_1299.field_6051;
        }
        if (t == class_1310.field_6293) {
            return net.minecraft.class_1299.field_6079;
        }
        if (t == class_1310.field_6291) {
            return net.minecraft.class_1299.field_6105;
        }
        if (t == class_1310.field_6292) {
            return net.minecraft.class_1299.field_6118;
        }
        return null;
    }

    /** EnchantmentHelper.getDamageBonus(stack, mobType): 1.20.4's sum over the stack's enchantments */
    public static float damageBonus(net.minecraft.class_1799 stack, class_1310 mobType) {
        float sum = 0.0F;
        if (stack.method_7960()) {
            return sum;
        }
        net.minecraft.class_9304 enchantments = stack.method_57825(net.minecraft.class_9334.field_49633, net.minecraft.class_9304.field_49385);
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.class_6880<net.minecraft.class_1887>> en
                : enchantments.method_57539()) {
            sum += getDamageBonus(en.getKey().comp_349(), en.getIntValue(), mobType);
        }
        return sum;
    }

    /**
     * 1.20.5's DamageEnchantment.getDamageBonus against an entity whose class declares its 1.20.4 MobType
     * (CbRestMobType.targetDeclared): what 1.20.4 decided by that MobType for the enchantment's tag --
     * smite against UNDEAD, bane of arthropods against ARTHROPOD, impaling against WATER, 2.5 per level;
     * null (1.20.5 decides) for sharpness, which 1.20.4 gave every MobType alike, and for a tag 1.20.4
     * had no counterpart of.
     */
    public static Float declaredBonus(net.minecraft.class_1882 e, int level, class_1310 declared) {
        Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> t = ((DamageTargets) e).cbS4$targets();
        if (t.isEmpty()) {
            return null;
        }
        class_1310 wants = t.get() == net.minecraft.class_3483.field_49931 ? class_1310.field_6289
            : t.get() == net.minecraft.class_3483.field_48285 ? class_1310.field_6293
            : t.get() == net.minecraft.class_3483.field_48284 ? class_1310.field_6292 : null;
        if (wants == null) {
            return null;
        }
        return declared == wants ? level * 2.5F : 0.0F;
    }

    /**
     * DamageEnchantment.doPostAttack by 1.20.4's rule, where 1.20.4's answer can differ from 1.20.5's:
     * a converted DamageEnchantment (its own type), or 1.20.5's bane of arthropods hitting an entity whose
     * class declares its MobType. 1.20.4: a bane hit on a living ARTHROPOD slows it for 20 + random(10 *
     * level) ticks at amplifier 3. Whether it decided (false: 1.20.5's own rule applies).
     */
    public static boolean postAttack1204(net.minecraft.class_1882 e, net.minecraft.class_1309 attacker,
                                         net.minecraft.class_1297 target, int level) {
        boolean converted = legacy(e) != null;
        int type = damageType(e);
        class_1310 declared = CbRestMobType.declared(target);
        if (!converted && (type != 2 || declared == null)) {
            return false;
        }
        class_1310 mob = declared != null ? declared : CbRestMobType.mobType(target);
        if (target instanceof net.minecraft.class_1309 living && type == 2 && level > 0 && mob == class_1310.field_6293) {
            int ticks = 20 + attacker.method_6051().method_43048(10 * level);
            living.method_6092(new net.minecraft.class_1293(net.minecraft.class_1294.field_5909, ticks, 3));
        }
        return true;
    }

    /**
     * The feature flags enchanting draws from (EnchantmentHelper's enchanting-table and random-enchant
     * methods take them since 1.20.5): 1.20.4's offered every registered enchantment, and the ones a
     * world's flags disable (1.20.5's experimental mace enchantments) do not exist for its caller -- so
     * the running server's world flags, and without a server in this process the default (vanilla) set.
     */
    public static net.minecraft.class_7699 enabledFeatures() {
        net.minecraft.server.MinecraftServer s = CbRegistries.server();
        return s != null ? s.method_27728().method_45560() : net.minecraft.class_7701.field_40183;
    }

    private CbEnchant() {
    }
}
