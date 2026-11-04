package top.dext.centurybridge.step.v1_21.facade;

/**
 * 1.20.6 EnchantmentHelper.EnchantmentVisitor (class_1890$class_1891): accept(Enchantment, int). 1.21 keeps the interface
 * name but its accept takes Holder&lt;Enchantment&gt; (javap 1.21), so a converted mod's visitor (lambda or class) is bound
 * here instead (classRenames, converted mods only); CbEnchantmentHelper's walks call it with the layer's 1.20.6 enchantment.
 */
@FunctionalInterface
public interface EnchantmentVisitor {
    void accept(class_1887 enchantment, int level);
}
