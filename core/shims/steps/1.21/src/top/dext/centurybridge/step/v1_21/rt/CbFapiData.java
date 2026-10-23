package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.class_5321;
import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * Fabric API 1.20.6's data generation members for enchantments, which 1.21's module dropped with the enchantment
 * class (1.21's enchantments are data). Wired from s6fin-fapi.json.
 */
public final class CbFapiData {

    private CbFapiData() {
    }

    /** 1.20.6 TranslationBuilder.add(enchantment, value): the enchantment's description id, translated */
    public static void add(FabricLanguageProvider.TranslationBuilder builder, class_1887 enchantment, String value) {
        builder.add(enchantment.method_8184(), value);
    }

    /**
     * 1.20.6 EnchantmentTagProvider.reverseLookup(enchantment): the enchantment's registry key, an
     * IllegalArgumentException("Enchantment ... is not registered") when it has none -- as a 1.21 enchantment key.
     */
    public static class_5321<net.minecraft.class_1887> reverseLookup(FabricTagProvider.EnchantmentTagProvider provider, class_1887 enchantment) {
        try {
            return CbEnchantments.wKeyOf(enchantment);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Enchantment " + enchantment + " is not registered");
        }
    }
}
