package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import top.dext.centurybridge.step.v1_20_5.rt.CbLoot;
import top.dext.centurybridge.step.v1_20_5.rt.CbRegistries;

/**
 * Fabric API 1.20.4's data generation API on 1.20.5's. 1.20.5's providers take the run's registries -- in
 * their constructors and in the methods a provider implements -- where 1.20.4's had none:
 * <ul>
 * <li>a 1.20.4 mod adds its providers with Pack.addProvider(Factory), the factory taking the output only;
 *     {@link #addProvider} adds them as 1.20.5's registry-dependent factories, the run's registries future
 *     at hand while the factory builds the provider, and the provider constructors' super calls take it
 *     ({@link #registries}, the superCtorReshapes' push) -- what 1.20.5's addProvider(RegistryDependentFactory)
 *     hands a 1.20.5 provider;</li>
 * <li>the methods a 1.20.4 provider implements without the registries (generateAdvancement(consumer),
 *     generateTranslations(builder), configure(provider)) answer 1.20.5's with them through override hooks;
 *     calls of the 1.20.4 shapes reach a mod's own (the adapters here), else 1.20.5's with the run's
 *     registries;</li>
 * <li>a block loot table provider's generate(consumer) is s4item's LootTableSubProvider bridge (CbLoot).</li>
 * </ul>
 * Outside a run (a provider made without addProvider), the registries are the running world's (CbLoot,
 * CbRegistries) -- the built-in ones in a data generation process.
 */
public final class CbDatagen {

    private CbDatagen() {
    }

    /** the registries future of the provider being built (addProvider) */
    private static final ThreadLocal<CompletableFuture<net.minecraft.class_7225.class_7874>> BUILDING = new ThreadLocal<>();
    /** the latest run's registries future: every pack of a run shares one */
    private static volatile CompletableFuture<net.minecraft.class_7225.class_7874> run;

    /** a 1.20.4 FabricAdvancementProvider's generateAdvancement(consumer) */
    public interface LegacyAdvancements {
        void generateAdvancement(Consumer<net.minecraft.class_8779> consumer);
    }

    /** a 1.20.4 FabricLanguageProvider's generateTranslations(builder) */
    public interface LegacyTranslations {
        void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder);
    }

    /** a 1.20.4 FabricCodecDataProvider's configure(provider) */
    public interface LegacyConfigure {
        void configure(BiConsumer<net.minecraft.class_2960, ?> provider);
    }

    /** a mod's own TranslationBuilder declaring 1.20.4's add(EntityAttribute, value) */
    public interface AttributeTranslation {
        void add(net.minecraft.class_1320 entityAttribute, String value);
    }

    // ---------------------------------------------------------------- the run's registries

    /** pack.addProvider(factory) of a 1.20.4 mod: as 1.20.5's registry-dependent factory (see the class comment) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2405 addProvider(FabricDataGenerator.Pack pack, FabricDataGenerator.Pack.Factory factory) {
        FabricDataGenerator.Pack.RegistryDependentFactory dependent = (output, registries) -> {
            run = registries;
            CompletableFuture<net.minecraft.class_7225.class_7874> before = BUILDING.get();
            BUILDING.set(registries);
            try {
                return factory.create(output);
            } finally {
                if (before == null) {
                    BUILDING.remove();
                } else {
                    BUILDING.set(before);
                }
            }
        };
        return pack.addProvider(dependent);
    }

    /** the registries a 1.20.4 provider construction passes 1.20.5's constructor (superCtorReshapes) */
    public static CompletableFuture<net.minecraft.class_7225.class_7874> registries() {
        CompletableFuture<net.minecraft.class_7225.class_7874> f = BUILDING.get();
        return f != null ? f : CbLoot.registriesFuture();
    }

    /** the run's registries, for a 1.20.4 call that hands a 1.20.5 provider none */
    private static net.minecraft.class_7225.class_7874 registriesNow() {
        CompletableFuture<net.minecraft.class_7225.class_7874> f = run;
        return f != null ? f.join() : CbRegistries.current();
    }

    // ---------------------------------------------------------------- 1.20.4 calls

    /** provider.generateAdvancement(consumer) */
    public static void generateAdvancement(FabricAdvancementProvider provider, Consumer<net.minecraft.class_8779> consumer) {
        if (provider instanceof LegacyAdvancements legacy) {
            legacy.generateAdvancement(consumer);
        } else {
            provider.generateAdvancement(registriesNow(), consumer);
        }
    }

    /** provider.generateTranslations(builder) */
    public static void generateTranslations(FabricLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        if (provider instanceof LegacyTranslations legacy) {
            legacy.generateTranslations(builder);
        } else {
            provider.generateTranslations(registriesNow(), builder);
        }
    }

    /** provider.configure(consumer): the class's own 1.20.4 one, else 1.20.4's base, which demanded an override */
    public static void configure(FabricCodecDataProvider<?> provider, BiConsumer<net.minecraft.class_2960, ?> consumer) {
        if (provider instanceof LegacyConfigure legacy) {
            legacy.configure(consumer);
        } else {
            superConfigure(provider, consumer);
        }
    }

    /** super.configure(consumer): 1.20.4's deprecated base */
    public static void superConfigure(FabricCodecDataProvider<?> provider, BiConsumer<net.minecraft.class_2960, ?> consumer) {
        throw new UnsupportedOperationException("Override this method.");
    }

    /** builder.add(attribute, value) */
    public static void add(FabricLanguageProvider.TranslationBuilder builder, net.minecraft.class_1320 attribute, String value) {
        if (builder instanceof AttributeTranslation own) {
            own.add(attribute, value);
        } else {
            superAdd(builder, attribute, value);
        }
    }

    /** TranslationBuilder's 1.20.4 default add(attribute, value) */
    public static void superAdd(FabricLanguageProvider.TranslationBuilder builder, net.minecraft.class_1320 attribute, String value) {
        builder.add(attribute.method_26830(), value);
    }

    /** provider.generate(consumer) of a block loot table provider: s4item's LootTableSubProvider call */
    @SuppressWarnings("rawtypes")
    public static void generate(FabricBlockLootTableProvider provider, BiConsumer consumer) {
        CbLoot.call(provider, consumer);
    }

    /** super.generate(consumer) in a mod subclass of FabricBlockLootTableProvider */
    @SuppressWarnings("rawtypes")
    public static void superGenerate(FabricBlockLootTableProvider provider, BiConsumer consumer) {
        CbLoot.superGenerate((net.minecraft.class_7788) provider, consumer);
    }
}
