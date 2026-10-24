package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import top.dext.centurybridge.step.v1_21.mixin.CbLootBlockInvoker;
import top.dext.centurybridge.step.v1_21.mixin.CbLootEntityAccessor;
import top.dext.centurybridge.step.v1_21.mixin.CbLootModifierBuilderAccessor;

/**
 * 1.20.6's loot table data providers on 1.21's. 1.21 moved the registry lookup from the generate call into
 * the provider: LootTableSubProvider.generate(registries, consumer) became generate(consumer); the vanilla
 * providers are built with the lookup (new VanillaChestLoot(registries), BlockLootSubProvider(.., registries));
 * the members that read an enchantment (silk touch, looting) became instance members reading the provider's.
 *
 * <ul>
 * <li>a mod class with 1.20.6's generate(registries, consumer) gets 1.21's generate(consumer) synthesized (a
 * guarded bridged override hook, adapter {@link Generate}): {@link #generate} hands the old method the
 * provider's registries ({@link #registriesOf});</li>
 * <li>a 1.20.6 lambda provider is adapted the same way ({@link #adapt}); a 1.20.6 call
 * provider.generate(registries, consumer) reaches a mod's own 1.20.6 method, or 1.21's generate(consumer)
 * ({@link #call}), which reads the provider's own registries -- for BlockLootSubProvider and
 * EntityLootSubProvider those it was built with, as a 1.20.6 super.generate(registries, consumer) (reshaped by
 * the step's mechanical wiring) does too;</li>
 * <li>a 1.20.6 provider built without registries (new VanillaBlockLoot(), a subclass's super(..)) is built
 * with {@link #registries}.</li>
 * </ul>
 *
 * <p><b>Where the registries come from.</b> 1.20.6 handed generate the registries of the data generation run;
 * 1.21 hands them to the factory of each LootTableProvider.SubProviderEntry. A 1.20.6 entry's supplier is
 * wrapped in a factory ({@link #newSubProviderEntry}) that builds the provider with that run's registries
 * current and records them for the provider it built. Outside that, {@link #registries} answers the running
 * server's, else 1.21's builtin vanilla lookup -- the registries 1.20.6's own data generation ran on.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class CbLoot {

    private CbLoot() {
    }

    /** a 1.20.6 loot table provider: generate(registries, consumer of (key, table builder)) */
    public interface Generate {
        void method_10399(net.minecraft.class_7225.class_7874 registries,
                          BiConsumer<net.minecraft.class_5321<net.minecraft.class_52>, net.minecraft.class_52.class_53> consumer);
    }

    // ---------------------------------------------------------------- the registries a provider works with

    /** registries made current while a 1.20.6 provider is built or run (innermost first) */
    private static final ThreadLocal<ArrayDeque<net.minecraft.class_7225.class_7874>> CURRENT = ThreadLocal.withInitial(ArrayDeque::new);

    /** the registries each provider built through a 1.20.6 supplier was built with, by identity */
    private static final Map<Object, net.minecraft.class_7225.class_7874> BUILT = Collections.synchronizedMap(new WeakHashMap<>());

    private static volatile net.minecraft.class_7225.class_7874 vanilla;

    /**
     * The registries a 1.20.6 provider built or called without any works with: those made current on this
     * thread (a data generation run building or running it); else the running server's; else 1.21's
     * builtin vanilla lookup (VanillaRegistries.createLookup, what data generation starts from).
     */
    public static net.minecraft.class_7225.class_7874 registries() {
        net.minecraft.class_7225.class_7874 current = CURRENT.get().peek();
        if (current != null) {
            return current;
        }
        net.minecraft.server.MinecraftServer server = CbServers.current();
        if (server != null) {
            return server.method_30611();
        }
        net.minecraft.class_7225.class_7874 v = vanilla;
        if (v == null) {
            synchronized (CbLoot.class) {
                v = vanilla;
                if (v == null) {
                    vanilla = v = net.minecraft.class_7887.method_46817();
                }
            }
        }
        return v;
    }

    /** the registries a provider works with: those it was built with, else {@link #registries} */
    public static net.minecraft.class_7225.class_7874 registriesOf(Object provider) {
        if (provider instanceof LegacyPack pack) {
            return pack.cb$registries;
        }
        if (provider instanceof net.minecraft.class_7788 block) {
            return ((CbLootBlockInvoker) block).cb$registries();
        }
        if (provider instanceof net.minecraft.class_7789 entity) {
            return ((CbLootEntityAccessor) entity).cb$registries();
        }
        net.minecraft.class_7225.class_7874 built = BUILT.get(provider);
        return built != null ? built : registries();
    }

    private static <T> T withRegistries(net.minecraft.class_7225.class_7874 registries, Supplier<T> body) {
        ArrayDeque<net.minecraft.class_7225.class_7874> current = CURRENT.get();
        current.push(registries);
        try {
            return body.get();
        } finally {
            current.pop();
        }
    }

    // ---------------------------------------------------------------- generate

    /** 1.21's generate(consumer) of a mod class with 1.20.6's generate(registries, consumer) (the bridge) */
    public static void generate(Generate provider, BiConsumer consumer) {
        net.minecraft.class_7225.class_7874 registries = registriesOf(provider);
        withRegistries(registries, () -> {
            provider.method_10399(registries, consumer);
            return null;
        });
    }

    /** a 1.20.6 lambda (or other implementation spun as {@link Generate}) as 1.21's provider */
    public static net.minecraft.class_7791 adapt(Generate provider) {
        net.minecraft.class_7225.class_7874 registries = registries();
        return new net.minecraft.class_7791() {
            @Override
            public void method_10399(BiConsumer consumer) {
                withRegistries(registries, () -> {
                    provider.method_10399(registries, consumer);
                    return null;
                });
            }
        };
    }

    /**
     * provider.generate(registries, consumer): the class's own 1.20.6 method; else 1.21's generate(consumer),
     * with these registries current for whatever 1.20.6 code it reaches (a 1.21 provider has its own).
     */
    public static void call(net.minecraft.class_7791 provider, net.minecraft.class_7225.class_7874 registries, BiConsumer consumer) {
        withRegistries(registries, () -> {
            if (provider instanceof Generate g) {
                g.method_10399(registries, consumer);
            } else {
                provider.method_10399(consumer);
            }
            return null;
        });
    }

    /**
     * A mod's super.generate(registries, consumer) into a vanilla (or Fabric API) provider class: that class's
     * 1.21 generate(consumer), reached past the mod's synthesized one by the guard, with those registries
     * current. Without this the call would dispatch back into the mod's own override.
     */
    public static void superGenerate(net.minecraft.class_7791 provider, net.minecraft.class_7225.class_7874 registries, BiConsumer consumer) {
        boolean entered = Guard.cbEnter(provider);
        try {
            withRegistries(registries, () -> {
                provider.method_10399(consumer);
                return null;
            });
        } finally {
            if (entered) {
                Guard.cbExit(provider);
            }
        }
    }

    /** ... keyed on BlockLootSubProvider (the receiver type a super redirect passes) */
    public static void superGenerate(net.minecraft.class_7788 provider, net.minecraft.class_7225.class_7874 registries, BiConsumer consumer) {
        superGenerate((net.minecraft.class_7791) provider, registries, consumer);
    }

    /** ... keyed on EntityLootSubProvider */
    public static void superGenerate(net.minecraft.class_7789 provider, net.minecraft.class_7225.class_7874 registries, BiConsumer consumer) {
        superGenerate((net.minecraft.class_7791) provider, registries, consumer);
    }

    /** ... keyed on Fabric API's FabricBlockLootTableProvider */
    public static void superGenerate(net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider provider,
                                     net.minecraft.class_7225.class_7874 registries, BiConsumer consumer) {
        superGenerate((net.minecraft.class_7791) provider, registries, consumer);
    }

    /** provider.generate(registries, consumer) through FabricBlockLootTableProvider */
    public static void call(net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider provider,
                            net.minecraft.class_7225.class_7874 registries, BiConsumer consumer) {
        call((net.minecraft.class_7791) provider, registries, consumer);
    }

    // ---------------------------------------------------------------- LootTableProvider.SubProviderEntry

    /** new SubProviderEntry(supplier, params): 1.21's takes a factory of the run's registries */
    public static net.minecraft.class_2438.class_7790 newSubProviderEntry(Supplier<net.minecraft.class_7791> provider, net.minecraft.class_176 params) {
        return new net.minecraft.class_2438.class_7790(new SubProviderFactory(provider), params);
    }

    /** entry.provider(): 1.20.6's supplier -- the one it was built with, else 1.21's factory on {@link #registries} */
    public static Supplier<net.minecraft.class_7791> comp_1068(net.minecraft.class_2438.class_7790 entry) {
        java.util.function.Function<net.minecraft.class_7225.class_7874, net.minecraft.class_7791> f = entry.comp_1068();
        if (f instanceof SubProviderFactory s) {
            return s.supplier();
        }
        return () -> f.apply(registries());
    }

    /** the factory a 1.20.6 supplier stands for: it builds the provider with the run's registries current */
    private record SubProviderFactory(Supplier<net.minecraft.class_7791> supplier)
            implements java.util.function.Function<net.minecraft.class_7225.class_7874, net.minecraft.class_7791> {
        @Override
        public net.minecraft.class_7791 apply(net.minecraft.class_7225.class_7874 registries) {
            net.minecraft.class_7791 provider = withRegistries(registries, supplier);
            if (provider != null) {
                BUILT.put(provider, registries);
            }
            return provider;
        }
    }

    // ---------------------------------------------------------------- BlockLootSubProvider

    /** the block loot provider whose generate runs on this thread (CbLootBlockMixin), innermost first */
    private static final ThreadLocal<ArrayDeque<net.minecraft.class_7788>> BLOCKS = ThreadLocal.withInitial(ArrayDeque::new);

    public static void enterBlock(net.minecraft.class_7788 provider) {
        BLOCKS.get().push(provider);
    }

    public static void exitBlock(net.minecraft.class_7788 provider) {
        ArrayDeque<net.minecraft.class_7788> blocks = BLOCKS.get();
        blocks.remove(provider);
    }

    /**
     * The provider a 1.20.6 static table builder of BlockLootSubProvider works for: the one whose generate is
     * running (1.20.6 subclasses call them from their generate()), else one on {@link #registries}. 1.21 made
     * them instance members reading the provider's registries (silk touch is an enchantment of its lookup).
     */
    private static CbLootBlockInvoker block() {
        net.minecraft.class_7788 running = BLOCKS.get().peek();
        return (CbLootBlockInvoker) (running != null ? running : new BlockTables(registries()));
    }

    /** a BlockLootSubProvider used only for its table builders (nothing calls its generate) */
    private static final class BlockTables extends net.minecraft.class_7788 {
        BlockTables(net.minecraft.class_7225.class_7874 registries) {
            super(java.util.Set.of(), net.minecraft.class_7701.field_40183, registries);
        }

        // public: Fabric API's access widener (applied to the compile stub, as at runtime) makes
        // BlockLootSubProvider.generate public, and an override may not narrow it
        @Override
        public void method_10379() {
        }
    }

    /** 1.20.6's HAS_SILK_TOUCH (a static of 1.20.6; 1.21's hasSilkTouch() of the provider) */
    public static net.minecraft.class_5341.class_210 field_40602() {
        return block().cb$hasSilkTouch();
    }

    /** 1.20.6's HAS_NO_SILK_TOUCH (1.21's doesNotHaveSilkTouch()) */
    public static net.minecraft.class_5341.class_210 field_40603() {
        return block().cb$doesNotHaveSilkTouch();
    }

    /** createSilkTouchDispatchTable(block, entry) */
    public static net.minecraft.class_52.class_53 method_45989(net.minecraft.class_2248 block, net.minecraft.class_79.class_80<?> entry) {
        return block().cb$createSilkTouchDispatchTable(block, entry);
    }

    /** createShearsDispatchTable(block, entry) */
    public static net.minecraft.class_52.class_53 method_46001(net.minecraft.class_2248 block, net.minecraft.class_79.class_80<?> entry) {
        return block().cb$createShearsDispatchTable(block, entry);
    }

    /** createSilkTouchOrShearsDispatchTable(block, entry) */
    public static net.minecraft.class_52.class_53 method_46008(net.minecraft.class_2248 block, net.minecraft.class_79.class_80<?> entry) {
        return block().cb$createSilkTouchOrShearsDispatchTable(block, entry);
    }

    /** createBeeNestDrop(block) */
    public static net.minecraft.class_52.class_53 method_46014(net.minecraft.class_2248 block) {
        return block().cb$createBeeNestDrop(block);
    }

    /** createBeeHiveDrop(block) */
    public static net.minecraft.class_52.class_53 method_46015(net.minecraft.class_2248 block) {
        return block().cb$createBeeHiveDrop(block);
    }

    /** createCaveVinesDrop(block) */
    public static net.minecraft.class_52.class_53 method_46016(net.minecraft.class_2248 block) {
        return block().cb$createCaveVinesDrop(block);
    }

    /** createDoublePlantShearsDrop(block) */
    public static net.minecraft.class_52.class_53 method_46019(net.minecraft.class_2248 block) {
        return block().cb$createDoublePlantShearsDrop(block);
    }

    // ---------------------------------------------------------------- EntityLootSubProvider

    /** 1.20.6's EntityLootSubProvider.ENTITY_ON_FIRE: an entity that is on fire (1.21 keeps the predicate inline) */
    public static net.minecraft.class_2048.class_2049 field_40612() {
        return net.minecraft.class_2048.class_2049.method_8916().method_8919(net.minecraft.class_2040.class_2041.method_8897().method_8898(Boolean.TRUE));
    }

    // ---------------------------------------------------------------- the record packs

    /**
     * The base of the facades of 1.20.6's vanilla loot packs that 1.21 made final records taking the
     * registries (VanillaChestLoot, VanillaFishingLoot, ...; facade/class_NNNN, generated by
     * gen_s6fin_loot.py): a 1.20.6 class, built without registries and open to subclassing as 1.20.6's was.
     * The facade is built on {@link #registries} and generates through 1.21's record of the same pack on
     * them -- the same tables, from 1.21's data ({@link #pack}); a 1.20.6 subclass that overrides generate
     * and calls super gets exactly those.
     */
    public abstract static class LegacyPack implements net.minecraft.class_7791, Generate {
        /** the registries the pack works with (those of the data generation run it was built in) */
        public final net.minecraft.class_7225.class_7874 cb$registries;

        protected LegacyPack() {
            this.cb$registries = registries();
        }

        /** 1.21's record of this pack, on these registries */
        protected abstract net.minecraft.class_7791 cb$pack(net.minecraft.class_7225.class_7874 registries);

        /** 1.20.6's generate(registries, consumer): 1.21's record of the pack on those registries */
        @Override
        public void method_10399(net.minecraft.class_7225.class_7874 registries,
                                 BiConsumer<net.minecraft.class_5321<net.minecraft.class_52>, net.minecraft.class_52.class_53> consumer) {
            net.minecraft.class_7791 pack = cb$pack(registries);
            withRegistries(registries, () -> {
                pack.method_10399(consumer);
                return null;
            });
        }

        /** 1.21's generate(consumer): 1.20.6's on the pack's registries (a subclass's override of it included) */
        @Override
        public void method_10399(BiConsumer consumer) {
            this.method_10399(cb$registries, consumer);
        }
    }

    // ---------------------------------------------------------------- the vanilla providers' table builders

    /** VanillaEntityLoot.elderGuardianLoot(), a static in 1.20.6, an instance member of 1.21's provider */
    public static net.minecraft.class_52.class_53 method_48515() {
        return new net.minecraft.class_2434(registries()).method_48515();
    }

    // ---------------------------------------------------------------- loot functions and conditions

    /** an enchantment registry to read through: the registries of {@link #registries} */
    private static net.minecraft.class_7225.class_7226<net.minecraft.class_1887> enchantments() {
        return registries().method_46762(net.minecraft.class_7924.field_41265);
    }

    /**
     * 1.20.6 EnchantWithLevelsFunction.Builder.allowTreasure(): treasure enchantments may come up too. 1.21's
     * builder names the enchantments to draw from instead (fromOptions); 1.20.6 drew the enchantments the item
     * takes that are discoverable, treasure ones only with allowTreasure -- 1.21's on_random_loot tag (the
     * discoverable ones, treasure included) is that set, where its default (in_enchanting_table) is the
     * non-treasure one.
     */
    public static net.minecraft.class_106.class_107 method_484(net.minecraft.class_106.class_107 builder) {
        if (builder instanceof LegacyAllowTreasure legacy) {
            return legacy.method_484();
        }
        return superAllowTreasure(builder);
    }

    /** a 1.20.6 EnchantWithLevelsFunction.Builder subclass's own allowTreasure() */
    public interface LegacyAllowTreasure {
        net.minecraft.class_106.class_107 method_484();
    }

    /** 1.20.6's body of allowTreasure(), for a subclass's super.allowTreasure() */
    public static net.minecraft.class_106.class_107 superAllowTreasure(net.minecraft.class_106.class_107 builder) {
        return builder.method_60295(enchantments().method_46735(net.minecraft.class_9636.field_51550));
    }

    /** the world holder of an enchantment key in {@link #registries} (1.21's builders take holders where 1.20.6's took the object) */
    public static net.minecraft.class_6880<net.minecraft.class_1887> enchantment(net.minecraft.class_5321<net.minecraft.class_1887> key) {
        return enchantments().method_46747(key);
    }

    /** new EnchantedCountIncreaseFunction.Builder(count) of 1.20.6, which counted looting: the looting holder */
    public static net.minecraft.class_6880<net.minecraft.class_1887> looting() {
        return enchantments().method_46747(net.minecraft.class_1893.field_9110);
    }

    /** new LootItemRandomChanceWithLootingCondition(chance, multiplier) of 1.20.6, as 1.21's record built by method_953 */
    public static net.minecraft.class_225 newRandomChanceWithLooting(float chance, float multiplier) {
        return new net.minecraft.class_225(chance, new net.minecraft.class_9704.class_9709(chance + multiplier, multiplier), looting());
    }

    /** 1.20.6 percent() of the looting condition: the chance without enchantment */
    public static float comp_1882(net.minecraft.class_225 condition) {
        return condition.comp_2865();
    }

    /**
     * 1.20.6 lootingMultiplier() of the looting condition: what each looting level adds. A 1.21 condition
     * built as 1.20.6's (a linear bonus) answers its per-level step; any other bonus has no single step and
     * answers the first level's bonus over the base.
     */
    public static float comp_1883(net.minecraft.class_225 condition) {
        if (condition.comp_2866() instanceof net.minecraft.class_9704.class_9709 linear) {
            return linear.comp_2699();
        }
        return condition.comp_2866().method_60188(1) - condition.comp_2865();
    }

    /** new LootItemRandomChanceCondition(probability) of 1.20.6: 1.21's takes a number provider, the constant */
    public static net.minecraft.class_5658 constant(float probability) {
        return net.minecraft.class_44.method_32448(probability);
    }

    /**
     * 1.20.6 probability() of the random chance condition. A 1.21 condition of a constant (as 1.20.6 built
     * them all) answers it; a condition of another number provider has no fixed probability -- 1.20.6 could
     * not express one -- and an IllegalStateException says so.
     */
    public static float comp_1881(net.minecraft.class_219 condition) {
        if (condition.comp_2781() instanceof net.minecraft.class_44 c) {
            return c.comp_1895();
        }
        throw new IllegalStateException("a 1.21 random chance condition of " + condition.comp_2781()
            + " has no fixed probability, which 1.20.6's LootItemRandomChanceCondition.probability() answers");
    }

    /**
     * 1.20.6 SetAttributesFunction.modifier(name, attribute, operation, amount) and new ModifierBuilder(..): a
     * modifier built with a fresh random uuid unless given one (ModifierBuilder.withUuid). 1.21's builder takes
     * the modifier's id; a 1.20.6 builder's modifiers get the id 1.20.6's uuid stands for (CbEntityAttributes,
     * the same as every 1.20.6 modifier) -- a random one per builder, as 1.20.6 drew one.
     */
    public static net.minecraft.class_2960 modifierId(String name) {
        return CbEntityAttributes.idOf(java.util.UUID.randomUUID());
    }

    /** ModifierBuilder.withUuid(uuid): the builder's modifier takes the id that uuid stands for; the builder itself, as 1.20.6 answered */
    public static net.minecraft.class_137.class_6156 method_35530(net.minecraft.class_137.class_6156 builder, java.util.UUID uuid) {
        if (builder instanceof LegacyWithUuid legacy) {
            return legacy.method_35530(uuid);
        }
        return superWithUuid(builder, uuid);
    }

    /** a 1.20.6 SetAttributesFunction.ModifierBuilder subclass's own withUuid(uuid) */
    public interface LegacyWithUuid {
        net.minecraft.class_137.class_6156 method_35530(java.util.UUID uuid);
    }

    /** 1.20.6's body of withUuid(uuid), for a subclass's super.withUuid(uuid) */
    public static net.minecraft.class_137.class_6156 superWithUuid(net.minecraft.class_137.class_6156 builder, java.util.UUID uuid) {
        ((CbLootModifierBuilderAccessor) builder).cb$setId(CbEntityAttributes.idOf(uuid));
        return builder;
    }
    // ---------------------------------------------------------------- LootDataType

    /** the directory each 1.20.6-built LootDataType was given, by identity (1.21 derives it from the registry key) */
    private static final Map<net.minecraft.class_8490<?>, String> DIRECTORIES = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * new LootDataType(registryKey, codec, directory, validator) of 1.20.6: 1.21's takes no directory, reading
     * the elements from the registry key's own (Registries.elementsDirPath). The type 1.21 builds is the one
     * 1.20.6 built; the directory 1.20.6 was given is kept for {@link #comp_2521}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_8490 newLootDataType(net.minecraft.class_5321 registryKey, com.mojang.serialization.Codec codec,
                                                         String directory, net.minecraft.class_8490.class_8491 validator) {
        net.minecraft.class_8490 type = new net.minecraft.class_8490(registryKey, codec, validator);
        DIRECTORIES.put(type, directory);
        return type;
    }

    /**
     * 1.20.6 LootDataType.directory(): the directory it was built with. 1.21's three vanilla types answer
     * 1.20.6's names for theirs (predicates, item_modifiers, loot_tables -- 1.21 reads them from predicate,
     * item_modifier, loot_table); any other 1.21 type the path 1.21 loads its elements from.
     */
    public static String comp_2521(net.minecraft.class_8490<?> type) {
        String d = DIRECTORIES.get(type);
        if (d != null) {
            return d;
        }
        if (type == net.minecraft.class_8490.field_44496) {
            return "predicates";
        }
        if (type == net.minecraft.class_8490.field_44497) {
            return "item_modifiers";
        }
        if (type == net.minecraft.class_8490.field_44498) {
            return "loot_tables";
        }
        return net.minecraft.class_7924.method_60915(type.comp_2519());
    }
}
