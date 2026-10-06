package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for 1.20.1 net/minecraft/class_1832 (ToolMaterial / Tier interface),
 * deleted at 1.21.2 (tool materials became a data component / ToolMaterial
 * record, no interface to implement). 43 corpus classes implement it to define
 * custom tool tiers.
 *
 * As a plain interface facade it keeps those implementor classes loadable: the
 * mod's own tier class implements this instead of the gone vanilla interface
 * and supplies the six accessor bodies as before. Whether 26.2's data-driven
 * tool system consumes the tier is a separate (ledgered) concern -- this only
 * removes the "implements <gone>" load failure.
 *
 * Method names are the intermediary tokens; the Ingredient return type is named
 * with its 26.2 real name (the mod-side class_1856 reference is rewritten to it
 * by the namespace-wall rename pass).
 */
public interface class_1832 {
    /** getUses() */
    int method_8025();

    /** getSpeed() */
    float method_8027();

    /** getAttackDamageBonus() */
    float method_8028();

    /** getLevel() / getMiningLevel() */
    int method_8024();

    /** getEnchantmentValue() */
    int method_8026();

    /** getRepairIngredient() */
    net.minecraft.world.item.crafting.Ingredient method_8023();
}
