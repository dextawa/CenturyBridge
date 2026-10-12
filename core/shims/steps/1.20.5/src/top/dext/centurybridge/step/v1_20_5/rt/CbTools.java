package top.dext.centurybridge.step.v1_20_5.rt;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 1.20.4's tool and attribute API on 1.20.5's components.
 * <p>
 * 1.20.4's swords, digging tools and tridents computed their attack modifiers in the constructor and
 * answered them from getDefaultAttributeModifiers(slot); 1.20.5 takes them as an item properties
 * component given at registration (SwordItem.createAttributes and the like) and asks the item's
 * getDefaultAttributeModifiers() only when a stack has none. A converted item is made the 1.20.4 way:
 * its constructor's arguments become the item's own modifiers ({@link Base}, kept by
 * mixin/CbItemMixin, which answers getDefaultAttributeModifiers() with them), so a mod class overriding
 * 1.20.4's per-slot method -- reconnected by an override hook through {@link #modifiers} -- is what the
 * game asks, and its super call reaches those modifiers. Mining moved to the TOOL component; 1.20.4's
 * getDestroySpeed / isCorrectToolForDrops overrides answer 1.20.5's item methods through override
 * hooks, and their super calls answer what 1.20.5's Item reads from that component -- which 1.20.5's
 * SwordItem and DiggerItem constructors set and its registration gives shears and tridents (so the
 * converted constructions of those get it too). A Tier's mining level became the tag of blocks it cannot
 * mine ({@link #incorrectBlocks}).
 */
public final class CbTools {

    /** mixin/CbItemMixin: the attack modifiers a converted tool was made with, null otherwise */
    public interface Base {
        net.minecraft.class_9285 cbS4$base();
    }

    /** a mod class declaring 1.20.4's Tier.getLevel() */
    public interface Level {
        int method_8024();
    }

    /** a mod class declaring 1.20.4's Item.getDefaultAttributeModifiers(EquipmentSlot) (the override hook's adapter) */
    public interface Modifiers {
        Multimap<net.minecraft.class_1320, net.minecraft.class_1322> method_7844(net.minecraft.class_1304 slot);
    }

    /** a mod class declaring 1.20.4's SwordItem.getDamage() */
    public interface SwordDamage {
        float method_8020();
    }

    /** a mod class declaring 1.20.4's DiggerItem.getAttackDamage() */
    public interface DiggerDamage {
        float method_26366();
    }

    /** a converted constructor's modifiers, until the Item constructor it runs takes them (same thread, at once) */
    private static final ThreadLocal<Map<net.minecraft.class_1792.class_1793, net.minecraft.class_9285>> PENDING =
        ThreadLocal.withInitial(IdentityHashMap::new);

    private static net.minecraft.class_1792.class_1793 base(net.minecraft.class_1792.class_1793 p, net.minecraft.class_9285 modifiers) {
        PENDING.get().put(p, modifiers);
        return p;
    }

    /** CbItemMixin, as the Item constructor returns: the modifiers its properties came with */
    public static net.minecraft.class_9285 claim(net.minecraft.class_1792.class_1793 p) {
        return PENDING.get().remove(p);
    }

    // ---------------------------------------------------------------- converted constructors

    /**
     * new SwordItem(tier, attackDamage, attackSpeed, properties): 1.20.4's sword modifiers (attack damage
     * attackDamage + the tier's bonus, attack speed, main hand -- SwordItem.createAttributes makes the same).
     */
    public static net.minecraft.class_1792.class_1793 sword(net.minecraft.class_1832 tier, int damage, float speed,
                                                         net.minecraft.class_1792.class_1793 p) {
        return base(p, net.minecraft.class_1829.method_57394(tier, damage, speed));
    }

    /** new DiggerItem(attackDamage, attackSpeed, tier, blocks, properties): 1.20.4's tool modifiers */
    public static net.minecraft.class_1792.class_1793 digger(float damage, float speed, net.minecraft.class_1832 tier,
                                                          net.minecraft.class_1792.class_1793 p) {
        return base(p, net.minecraft.class_1766.method_57346(tier, damage, speed));
    }

    /** new PickaxeItem / HoeItem(tier, attackDamage, attackSpeed, properties): 1.20.4's DiggerItem modifiers */
    public static net.minecraft.class_1792.class_1793 diggerInt(net.minecraft.class_1832 tier, int damage, float speed,
                                                             net.minecraft.class_1792.class_1793 p) {
        return base(p, net.minecraft.class_1766.method_57346(tier, (float) damage, speed));
    }

    /** new AxeItem / ShovelItem(tier, attackDamage, attackSpeed, properties) */
    public static net.minecraft.class_1792.class_1793 diggerFloat(net.minecraft.class_1832 tier, float damage, float speed,
                                                               net.minecraft.class_1792.class_1793 p) {
        return base(p, net.minecraft.class_1766.method_57346(tier, damage, speed));
    }

    /**
     * new TridentItem(properties): what 1.20.4's TridentItem gave itself -- its attack modifiers (8 damage,
     * -2.9 speed: TridentItem.createAttributes) and its two-durability-per-block mining (1.20.5's trident tool)
     */
    public static net.minecraft.class_1792.class_1793 trident(net.minecraft.class_1792.class_1793 p) {
        return base(p.method_57349(net.minecraft.class_9334.field_50077, net.minecraft.class_1835.method_58420()),
            net.minecraft.class_1835.method_57395());
    }

    /** new ShearsItem(properties): 1.20.4's ShearsItem mining (cobweb, leaves, wool, vines -- 1.20.5's shears tool) */
    public static net.minecraft.class_1792.class_1793 shears(net.minecraft.class_1792.class_1793 p) {
        return p.method_57349(net.minecraft.class_9334.field_50077, net.minecraft.class_1820.method_58416());
    }

    // ---------------------------------------------------------------- attribute modifiers

    /** the six slots 1.20.4 asked an item about (1.20.5's BODY slot did not exist) */
    private static final net.minecraft.class_1304[] SLOTS_1204 = {
        net.minecraft.class_1304.field_6173, net.minecraft.class_1304.field_6171, net.minecraft.class_1304.field_6166,
        net.minecraft.class_1304.field_6172, net.minecraft.class_1304.field_6174, net.minecraft.class_1304.field_6169};

    /**
     * getDefaultAttributeModifiers() of a mod class declaring 1.20.4's per-slot method (the override
     * hook's bridge): each modifier its method answers for each slot 1.20.4 asked about, for that slot.
     */
    public static net.minecraft.class_9285 modifiers(Modifiers m) {
        return fromSlots(m::method_7844);
    }

    /**
     * 1.20.4's per-slot modifiers as 1.20.5's list (shared: Fabric API's side converts FabricItem's with it):
     * each modifier the function answers for each slot 1.20.4 asked about, for that slot.
     */
    public static net.minecraft.class_9285 fromSlots(
            java.util.function.Function<net.minecraft.class_1304, Multimap<net.minecraft.class_1320, net.minecraft.class_1322>> perSlot) {
        net.minecraft.class_9285.class_9286 b = net.minecraft.class_9285.method_57480();
        for (net.minecraft.class_1304 slot : SLOTS_1204) {
            for (Map.Entry<net.minecraft.class_1320, net.minecraft.class_1322> e : perSlot.apply(slot).entries()) {
                b.method_57487(CbItemHolders.attributeHolder(e.getKey()), e.getValue(), net.minecraft.class_9274.method_59524(slot));
            }
        }
        return b.method_57486();
    }

    /** a 1.20.5 modifier list's modifiers for one slot, as 1.20.4's (immutable) multimap (shared, like fromSlots) */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> forSlot(net.minecraft.class_9285 mods,
                                                                                       net.minecraft.class_1304 slot) {
        ImmutableMultimap.Builder<net.minecraft.class_1320, net.minecraft.class_1322> out = ImmutableMultimap.builder();
        for (net.minecraft.class_9285.class_9287 e : mods.comp_2393()) {
            if (e.comp_2397().method_57286(slot)) {
                out.put(e.comp_2395().comp_349(), e.comp_2396());
            }
        }
        return out.build();
    }

    /** the item's own modifiers: a converted tool's, else those its properties gave it */
    private static net.minecraft.class_9285 own(net.minecraft.class_1792 item) {
        net.minecraft.class_9285 base = ((Base) item).cbS4$base();
        return base != null ? base : CbItemProps.built(item).method_57830(net.minecraft.class_9334.field_49636, net.minecraft.class_9285.field_49326);
    }

    /**
     * item.getDefaultAttributeModifiers(slot): the class's own 1.20.4 override, else the modifiers 1.20.5
     * gives the item for that slot -- its own, or (none given) what its getDefaultAttributeModifiers()
     * answers, as a stack of it does.
     */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> getDefaultAttributeModifiers(
            net.minecraft.class_1792 item, net.minecraft.class_1304 slot) {
        if (item instanceof Modifiers m) {
            return m.method_7844(slot);
        }
        net.minecraft.class_9285 own = own(item);
        return forSlot(own.comp_2393().isEmpty() ? item.method_7844() : own, slot);
    }

    /** super.getDefaultAttributeModifiers(slot) in a mod subclass of Item: 1.20.4's body answered none */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> superModifiers(net.minecraft.class_1792 item,
                                                                                             net.minecraft.class_1304 slot) {
        return ImmutableMultimap.of();
    }

    /** ... of SwordItem: its own modifiers (1.20.4: for the main hand) */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> superModifiers(net.minecraft.class_1829 item,
                                                                                             net.minecraft.class_1304 slot) {
        return forSlot(own(item), slot);
    }

    /** ... of DiggerItem */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> superModifiers(net.minecraft.class_1766 item,
                                                                                             net.minecraft.class_1304 slot) {
        return forSlot(own(item), slot);
    }

    /** ... of TridentItem */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> superModifiers(net.minecraft.class_1835 item,
                                                                                             net.minecraft.class_1304 slot) {
        return forSlot(own(item), slot);
    }

    /** ... of ArmorItem: ArmorItem's own (1.20.5 keeps them in a supplier: mixin/CbArmorItemAccessor) */
    public static Multimap<net.minecraft.class_1320, net.minecraft.class_1322> superModifiers(net.minecraft.class_1738 item,
                                                                                             net.minecraft.class_1304 slot) {
        return forSlot(((top.dext.centurybridge.step.v1_20_5.mixin.CbArmorItemAccessor) item).cbS4$defaultModifiers().get(), slot);
    }

    // ---------------------------------------------------------------- mining

    /** item.getDestroySpeed(stack, state): 1.20.5's method of the same shape, which a 1.20.4 override answers */
    public static float getDestroySpeed(net.minecraft.class_1792 item, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        return item.method_58404(stack, state);
    }

    /** super.getDestroySpeed(stack, state) in a mod subclass of Item: what 1.20.5's Item reads off the stack's tool */
    public static float superGetDestroySpeed(net.minecraft.class_1792 item, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        net.minecraft.class_9424 tool = stack.method_57824(net.minecraft.class_9334.field_50077);
        return tool != null ? tool.method_58425(state) : 1.0F;
    }

    /** ... of DiggerItem (1.20.5 keeps its blocks and speed in its tool) */
    public static float superGetDestroySpeed(net.minecraft.class_1766 item, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        return superGetDestroySpeed((net.minecraft.class_1792) item, stack, state);
    }

    /** ... of SwordItem */
    public static float superGetDestroySpeed(net.minecraft.class_1829 item, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        return superGetDestroySpeed((net.minecraft.class_1792) item, stack, state);
    }

    /** ... of ShearsItem */
    public static float superGetDestroySpeed(net.minecraft.class_1820 item, net.minecraft.class_1799 stack, net.minecraft.class_2680 state) {
        return superGetDestroySpeed((net.minecraft.class_1792) item, stack, state);
    }

    /**
     * item.isCorrectToolForDrops(state): 1.20.5's isCorrectToolForDrops(stack, state), which a 1.20.4
     * override answers, asked about a stack of the item as it is made (1.20.4's had no stack).
     */
    public static boolean isCorrectToolForDrops(net.minecraft.class_1792 item, net.minecraft.class_2680 state) {
        return item.method_58405(new net.minecraft.class_1799(item), state);
    }

    /** super.isCorrectToolForDrops(state) in a mod subclass of Item: what 1.20.5 reads off the item's tool (none: false) */
    public static boolean superIsCorrectToolForDrops(net.minecraft.class_1792 item, net.minecraft.class_2680 state) {
        net.minecraft.class_9424 tool = CbItemProps.built(item).method_57829(net.minecraft.class_9334.field_50077);
        return tool != null && tool.method_58426(state);
    }

    /** ... of DiggerItem */
    public static boolean superIsCorrectToolForDrops(net.minecraft.class_1766 item, net.minecraft.class_2680 state) {
        return superIsCorrectToolForDrops((net.minecraft.class_1792) item, state);
    }

    /** ... of SwordItem */
    public static boolean superIsCorrectToolForDrops(net.minecraft.class_1829 item, net.minecraft.class_2680 state) {
        return superIsCorrectToolForDrops((net.minecraft.class_1792) item, state);
    }

    /** ... of ShearsItem */
    public static boolean superIsCorrectToolForDrops(net.minecraft.class_1820 item, net.minecraft.class_2680 state) {
        return superIsCorrectToolForDrops((net.minecraft.class_1792) item, state);
    }

    // ---------------------------------------------------------------- damage and speed fields

    /** the attack damage a tool's own modifiers give (1.20.4 kept it in a field: the base plus the tier's bonus) */
    private static float attackDamage(net.minecraft.class_1792 item) {
        for (net.minecraft.class_9285.class_9287 e : own(item).comp_2393()) {
            if (e.comp_2395() == net.minecraft.class_5134.field_23721 && net.minecraft.class_1792.field_8006.equals(e.comp_2396().comp_2447())) {
                return (float) e.comp_2396().comp_2449();
            }
        }
        return 0.0F;
    }

    /** sword.getDamage(): the class's own override, else 1.20.4's (its attack damage) */
    public static float swordDamage(net.minecraft.class_1829 sword) {
        return sword instanceof SwordDamage d ? d.method_8020() : superSwordDamage(sword);
    }

    /** SwordItem.getDamage()'s 1.20.4 body */
    public static float superSwordDamage(net.minecraft.class_1829 sword) {
        return attackDamage(sword);
    }

    /** digger.getAttackDamage(): the class's own override, else 1.20.4's (its attack damage) */
    public static float diggerDamage(net.minecraft.class_1766 digger) {
        return digger instanceof DiggerDamage d ? d.method_26366() : superDiggerDamage(digger);
    }

    /** DiggerItem.getAttackDamage()'s 1.20.4 body */
    public static float superDiggerDamage(net.minecraft.class_1766 digger) {
        return attackDamage(digger);
    }

    /** digger.speed (1.20.4: the tier's speed, set by the constructor) */
    public static float diggerSpeed(net.minecraft.class_1766 digger) {
        return digger.method_8022().method_8027();
    }

    // ---------------------------------------------------------------- tiers

    /** 1.20.4's mining level, as the blocks 1.20.5 says a tier cannot mine: wood/gold 0, stone 1, iron 2, diamond 3, netherite 4 */
    public static net.minecraft.class_6862<net.minecraft.class_2248> byLevel(int level) {
        return switch (Math.max(0, Math.min(4, level))) {
            case 0 -> net.minecraft.class_3481.field_49930;
            case 1 -> net.minecraft.class_3481.field_49928;
            case 2 -> net.minecraft.class_3481.field_49927;
            case 3 -> net.minecraft.class_3481.field_49926;
            default -> net.minecraft.class_3481.field_49925;
        };
    }

    /**
     * Tier.getIncorrectBlocksForDrops() of a 1.20.4 tier (an abstract fill: 1.20.5 demands it): its mining
     * level's tag -- 1.20.4 kept the blocks needing a higher level in the needs_stone / needs_iron /
     * needs_diamond tags, which 1.20.5's incorrect_for_* tags are made of, level by level.
     */
    public static net.minecraft.class_6862<net.minecraft.class_2248> incorrectBlocks(net.minecraft.class_1832 tier) {
        if (tier instanceof Level l) {
            return byLevel(l.method_8024());
        }
        throw new AbstractMethodError(tier.getClass().getName() + " declares neither getLevel nor getIncorrectBlocksForDrops");
    }

    /**
     * tier.getLevel(): a 1.20.4 tier's own, else the level its tag of blocks it cannot mine stands for
     * (1.20.5's gold has wood's); a tag that stands for none answers 0.
     */
    public static int tierLevel(net.minecraft.class_1832 tier) {
        if (tier instanceof Level l) {
            return l.method_8024();
        }
        net.minecraft.class_6862<net.minecraft.class_2248> t = tier.method_58419();
        if (t == net.minecraft.class_3481.field_49928) {
            return 1;
        }
        if (t == net.minecraft.class_3481.field_49927) {
            return 2;
        }
        if (t == net.minecraft.class_3481.field_49926) {
            return 3;
        }
        return t == net.minecraft.class_3481.field_49925 ? 4 : 0;
    }

    // ---------------------------------------------------------------- projectile weapons

    /**
     * ProjectileWeaponItem.shootProjectile (abstract since 1.20.5) of a 1.20.4 subclass, which 1.20.4 never
     * asked for: the shot 1.20.5's own bows make -- along the shooter's look, turned by the angle.
     */
    public static void shootProjectile(net.minecraft.class_1811 weapon, net.minecraft.class_1309 shooter,
                                       net.minecraft.class_1676 projectile, int index, float velocity, float inaccuracy,
                                       float angle, net.minecraft.class_1309 target) {
        projectile.method_24919(shooter, shooter.method_36455(), shooter.method_36454() + angle, 0.0F, velocity, inaccuracy);
    }

    private CbTools() {
    }
}
