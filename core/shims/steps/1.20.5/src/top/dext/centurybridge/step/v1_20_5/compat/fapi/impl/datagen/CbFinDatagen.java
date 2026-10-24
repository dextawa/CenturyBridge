package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.datagen;

import java.util.ArrayDeque;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.class_7225;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.resource.conditions.CbLegacyConditions;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.resource.conditions.v1.ConditionJsonProvider;
import top.dext.centurybridge.step.v1_20_5.rt.CbItemGenerics;
import top.dext.centurybridge.step.v1_20_5.rt.CbLoot;

/**
 * fabric-data-generation-api-v1 1.20.4 on 1.20.5's. 1.20.5's providers take the data generator's registries
 * (a CompletableFuture of HolderLookup.Provider) in their constructors and hand the lookup to their generate
 * methods; 1.20.4's took neither.
 * <ul>
 * <li>Registries: a 1.20.4 mod adds its providers through Pack.addProvider(Factory) (output -> provider); that
 * call becomes 1.20.5's addProvider(RegistryDependentFactory), which hands the pack's own registries, and the
 * provider's converted super constructor takes them ({@link #registries()}). A provider built outside such a
 * factory gets the vanilla registries (VanillaRegistries.createLookup), the only ones there are without a
 * data generator.</li>
 * <li>The 1.20.4 generate methods (generateAdvancement(consumer), generateTranslations(builder),
 * configure(consumer)) are reached through bridge hooks from 1.20.5's (which ignore the lookup: 1.20.4's had
 * none), and a 1.20.4 call of one reaches the class's own method.</li>
 * <li>Resource conditions: 1.20.4's ConditionJsonProvider[] are 1.20.5's ResourceCondition[] (CbLegacyConditions).</li>
 * </ul>
 */
public final class CbFinDatagen {

    private CbFinDatagen() {
    }

    private static final ThreadLocal<ArrayDeque<CompletableFuture<class_7225.class_7874>>> REGISTRIES =
        ThreadLocal.withInitial(ArrayDeque::new);

    /** Pack.addProvider(Factory) -> addProvider(RegistryDependentFactory): the factory runs with the pack's registries at hand */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static FabricDataGenerator.Pack.RegistryDependentFactory withRegistries(FabricDataGenerator.Pack.Factory factory) {
        return (output, registries) -> {
            ArrayDeque<CompletableFuture<class_7225.class_7874>> stack = REGISTRIES.get();
            stack.push(registries);
            try {
                return factory.create(output);
            } finally {
                stack.pop();
            }
        };
    }

    /** the registries a 1.20.4 provider's converted super constructor passes: the pack's (see the class comment) */
    public static CompletableFuture<class_7225.class_7874> registries() {
        CompletableFuture<class_7225.class_7874> registries = REGISTRIES.get().peek();
        return registries != null ? registries : CompletableFuture.supplyAsync(net.minecraft.class_7887::method_46817);
    }

    // ---- FabricAdvancementProvider

    /** a 1.20.4 advancement provider's generateAdvancement(consumer) */
    public interface LegacyAdvancements {
        void generateAdvancement(Consumer<net.minecraft.class_8779> consumer);
    }

    /** 1.20.5's generateAdvancement(lookup, consumer) of a 1.20.4 provider: its own (1.20.4 had no lookup) */
    public static void generateAdvancement(LegacyAdvancements provider, class_7225.class_7874 lookup, Consumer<net.minecraft.class_8779> consumer) {
        provider.generateAdvancement(consumer);
    }

    /** provider.generateAdvancement(consumer) called by 1.20.4 code: the class's own */
    public static void generateAdvancement(FabricAdvancementProvider provider, Consumer<net.minecraft.class_8779> consumer) {
        legacy(provider, LegacyAdvancements.class, "generateAdvancement").generateAdvancement(consumer);
    }

    // ---- FabricLanguageProvider

    /** a 1.20.4 language provider's generateTranslations(builder) */
    public interface LegacyTranslations {
        void generateTranslations(FabricLanguageProvider.TranslationBuilder builder);
    }

    /** 1.20.5's generateTranslations(lookup, builder) of a 1.20.4 provider: its own */
    public static void generateTranslations(LegacyTranslations provider, class_7225.class_7874 lookup, FabricLanguageProvider.TranslationBuilder builder) {
        provider.generateTranslations(builder);
    }

    /** provider.generateTranslations(builder) called by 1.20.4 code: the class's own */
    public static void generateTranslations(FabricLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        legacy(provider, LegacyTranslations.class, "generateTranslations").generateTranslations(builder);
    }

    // ---- FabricCodecDataProvider

    /** a 1.20.4 codec data provider's configure(consumer) (the one its registry-less constructor ran) */
    public interface LegacyConfigure {
        void configure(BiConsumer consumer);
    }

    /** 1.20.5's configure(consumer, lookup) of a 1.20.4 provider declaring configure(consumer): that one */
    @SuppressWarnings("rawtypes")
    public static void configure(LegacyConfigure provider, BiConsumer consumer, class_7225.class_7874 lookup) {
        provider.configure(consumer);
    }

    /** this.configure(consumer) called by 1.20.4 code: the class's own, else 1.20.4's body (UnsupportedOperationException) */
    @SuppressWarnings("rawtypes")
    public static void configure(FabricCodecDataProvider provider, BiConsumer consumer) {
        if (provider instanceof LegacyConfigure legacy) {
            legacy.configure(consumer);
            return;
        }
        superConfigure(provider, consumer);
    }

    /** super.configure(consumer): 1.20.4's FabricCodecDataProvider.configure(consumer) body */
    @SuppressWarnings("rawtypes")
    public static void superConfigure(FabricCodecDataProvider provider, BiConsumer consumer) {
        throw new UnsupportedOperationException("Override this method.");
    }

    // ---- FabricBlockLootTableProvider

    /** provider.generate(consumer of locations) called by 1.20.4 code: CbLoot's (the class's own 1.20.4 method, else 1.20.5's) */
    @SuppressWarnings("rawtypes")
    public static void generate(FabricBlockLootTableProvider provider, BiConsumer consumer) {
        CbLoot.call(provider, consumer);
    }

    /** super.generate(consumer of locations): 1.20.5's FabricBlockLootTableProvider.generate with the provider's registries */
    @SuppressWarnings("rawtypes")
    public static void superGenerate(FabricBlockLootTableProvider provider, BiConsumer consumer) {
        CbLoot.superGenerate((net.minecraft.class_7788) provider, consumer);
    }

    // ---- resource conditions on providers

    /**
     * a 1.20.4 FabricLootTableProvider's withConditions(consumer of locations, providers): the consumer of keys
     * 1.20.5 hands is given to it as one of locations, its answer handed back as one of keys
     */
    public interface LegacyLootConditions {
        BiConsumer withConditions(BiConsumer consumer, ConditionJsonProvider[] conditions);
    }

    /** 1.20.5's withConditions(consumer of keys, conditions) of a class overriding 1.20.4's */
    @SuppressWarnings("rawtypes")
    public static BiConsumer lootWithConditions(LegacyLootConditions provider, BiConsumer consumer, ResourceCondition[] conditions) {
        return CbItemGenerics.lootKeyConsumer(provider.withConditions(CbItemGenerics.lootLocationConsumer(consumer), CbLegacyConditions.providers(conditions)));
    }

    private static <T> T legacy(Object provider, Class<T> type, String method) {
        if (type.isInstance(provider)) {
            return type.cast(provider);
        }
        // 1.20.4's method was abstract: a class that does not declare it could not exist
        throw new AbstractMethodError(provider.getClass().getName() + "." + method + " (1.20.4 shape)");
    }
}
