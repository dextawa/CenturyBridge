package top.dext.centurybridge.step.v1_20_5.facade;

/**
 * What every 1.20.4 enchantment subclass that 1.20.5 dropped inherited from 1.20.4's Enchantment and
 * 1.20.5's no longer has, for the facades standing in for those classes (and so for a mod class
 * extending one, whose calls name the facade): the public category field, getRarity(), and
 * getDamageBonus against a MobType -- which 1.20.5's getDamageBonus(level, entityType) reaches with the
 * entity type as 1.20.4's MobType. Not a 1.20.4 class itself; the facades extend it.
 */
public abstract class LegacyEnchantment extends net.minecraft.class_1887
        implements top.dext.centurybridge.step.v1_20_5.rt.CbEnchant.RarityOverride,
                   top.dext.centurybridge.step.v1_20_5.rt.CbEnchant.DamageBonus {

    /** category (1.20.4's public final field) */
    public final EnchantmentCategory field_9083;

    /** the facade's 1.20.4 class body (rt/CbEnchant) answers its level range and costs */
    protected LegacyEnchantment(Class<? extends net.minecraft.class_1887> facade, EnchantmentRarity rarity,
                                EnchantmentCategory category, net.minecraft.class_1304[] slots) {
        super(top.dext.centurybridge.step.v1_20_5.rt.CbEnchant.definition(facade, rarity, category, slots));
        this.field_9083 = category;
    }

    /** getRarity(): the rarity it was made with */
    @Override
    public EnchantmentRarity method_8186() {
        return top.dext.centurybridge.step.v1_20_5.rt.CbEnchant.superGetRarity(this);
    }

    /** getDamageBonus(level, mobType): 1.20.4's Enchantment answered 0 */
    @Override
    public float method_8196(int level, class_1310 mobType) {
        return 0.0F;
    }

    /** 1.20.5's getDamageBonus(level, entityType): the 1.20.4 method, against the entity type's MobType */
    @Override
    public float method_8196(int level, net.minecraft.class_1299<?> type) {
        return this.method_8196(level, top.dext.centurybridge.step.v1_20_5.rt.CbEnchant.mobType(type));
    }
}
