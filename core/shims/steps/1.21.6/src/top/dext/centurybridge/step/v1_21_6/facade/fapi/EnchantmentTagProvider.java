package top.dext.centurybridge.step.v1_21_6.facade.fapi;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.class_1887;
import net.minecraft.class_7225;
import net.minecraft.class_7924;

/**
 * Fabric API 0.128.2+1.21.5's FabricTagProvider.EnchantmentTagProvider, which 0.128.2+1.21.6 removed. A 1.21.5 mod's
 * references are renamed here (classRenames, s6h-fapitags.json). Its body is 1.21.5's (javap): a FabricTagProvider of
 * the enchantment registry (Registries.ENCHANTMENT). Its override of TagsProvider.tag called
 * FabricTagProvider.getOrCreateTagBuilder, which is what it inherits from the restored FabricTagProvider.tag
 * (CbFapiTagProviderRestoredMixin), so the override is not repeated. Enchantments are data-driven, so add(T) has no key for
 * one (FabricTagProvider.reverseLookup throws, as in 1.21.5); mods add their keys.
 */
public abstract class EnchantmentTagProvider extends FabricTagProvider<class_1887> {

    public EnchantmentTagProvider(FabricDataOutput output, CompletableFuture<class_7225.class_7874> registriesFuture) {
        super(output, class_7924.field_41265, registriesFuture);
    }
}
