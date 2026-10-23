package top.dext.centurybridge.step.v1_21.rt;

import java.util.function.Consumer;
import top.dext.centurybridge.step.v1_21.mixin.CbDataEnchantmentTagsInvoker;
import top.dext.centurybridge.step.v1_21.mixin.CbDataOverworldBiomesInvoker;

/**
 * Data generation members of 1.20.6 (net/minecraft/data outside loot, which is CbLoot's) that 1.21 reshaped,
 * with 1.20.6's behaviour. Wired from s6fin-loot.json.
 */
public final class CbData {

    private CbData() {
    }

    // ------------------------------------------------------------------------------------------------ biomes

    /**
     * 1.20.6 OverworldBiomes.swamp(placedFeatures, carvers, spawnModifier): 1.20.6's swamp, its spawns
     * (farm animals, slimes 1.20.6's way -- weight 100, groups of 1 -- the swamp's frogs and the common monsters)
     * handed to {@code spawnModifier} before the biome is built. 1.21's swamp() takes no modifier and adds the
     * bogged; this is 1.20.6's body on 1.21's (unchanged) helpers.
     */
    public static net.minecraft.class_1959 method_30708(net.minecraft.class_7871<net.minecraft.class_6796> placedFeatures,
                                                       net.minecraft.class_7871<net.minecraft.class_2922<?>> carvers,
                                                       Consumer<net.minecraft.class_5483.class_5496> spawnModifier) {
        net.minecraft.class_5483.class_5496 spawns = new net.minecraft.class_5483.class_5496();
        net.minecraft.class_3864.method_30580(spawns);
        commonMonsters(spawns);
        spawns.method_31011(net.minecraft.class_1311.field_6302, new net.minecraft.class_5483.class_1964(net.minecraft.class_1299.field_6069, 1, 1, 1));
        spawns.method_31011(net.minecraft.class_1311.field_6294, new net.minecraft.class_5483.class_1964(net.minecraft.class_1299.field_37419, 10, 2, 5));
        spawnModifier.accept(spawns);
        net.minecraft.class_5485.class_5495 generation = new net.minecraft.class_5485.class_5495(placedFeatures, carvers);
        net.minecraft.class_3864.method_16990(generation);
        CbDataOverworldBiomesInvoker.cb$globalOverworldGeneration(generation);
        net.minecraft.class_3864.method_38568(generation);
        net.minecraft.class_3864.method_17011(generation);
        net.minecraft.class_3864.method_16972(generation);
        net.minecraft.class_3864.method_16982(generation);
        net.minecraft.class_3864.method_16988(generation);
        generation.method_30992(net.minecraft.class_2893.class_2895.field_13178, net.minecraft.class_6811.field_35971);
        return swampBiome(spawns, generation, 6388580, 2302743, 6975545);
    }

    /**
     * 1.20.6 OverworldBiomes.mangroveSwamp(placedFeatures, carvers, spawnModifier): 1.20.6's mangrove swamp (its
     * slimes, frogs, tropical fish), the spawns handed to {@code spawnModifier} first.
     */
    public static net.minecraft.class_1959 method_43232(net.minecraft.class_7871<net.minecraft.class_6796> placedFeatures,
                                                       net.minecraft.class_7871<net.minecraft.class_2922<?>> carvers,
                                                       Consumer<net.minecraft.class_5483.class_5496> spawnModifier) {
        net.minecraft.class_5483.class_5496 spawns = new net.minecraft.class_5483.class_5496();
        commonMonsters(spawns);
        spawns.method_31011(net.minecraft.class_1311.field_6302, new net.minecraft.class_5483.class_1964(net.minecraft.class_1299.field_6069, 1, 1, 1));
        spawns.method_31011(net.minecraft.class_1311.field_6294, new net.minecraft.class_5483.class_1964(net.minecraft.class_1299.field_37419, 10, 2, 5));
        spawns.method_31011(net.minecraft.class_1311.field_24460, new net.minecraft.class_5483.class_1964(net.minecraft.class_1299.field_6111, 25, 8, 8));
        spawnModifier.accept(spawns);
        net.minecraft.class_5485.class_5495 generation = new net.minecraft.class_5485.class_5495(placedFeatures, carvers);
        net.minecraft.class_3864.method_16990(generation);
        CbDataOverworldBiomesInvoker.cb$globalOverworldGeneration(generation);
        net.minecraft.class_3864.method_38568(generation);
        net.minecraft.class_3864.method_43231(generation);
        net.minecraft.class_3864.method_43230(generation);
        generation.method_30992(net.minecraft.class_2893.class_2895.field_13178, net.minecraft.class_6811.field_35971);
        return swampBiome(spawns, generation, 3832426, 5077600, 9285927);
    }

    /**
     * 1.20.6 BiomeDefaultFeatures.commonMonsters(spawns): monsters with the zombie villager weight 5 and the
     * skeleton weight 100, no bogged (1.21's commonMonsters(spawns) is 1.21's monsters(spawns, 95, 5, 100, false)
     * with skeleton weight 100 too -- 1.21 kept that one; its swamp alone took the skeleton weight down to 70 for
     * the bogged).
     */
    private static void commonMonsters(net.minecraft.class_5483.class_5496 spawns) {
        net.minecraft.class_3864.method_30581(spawns);
    }

    /** the biome both 1.20.6 swamps built, with that water / water fog / foliage colour */
    private static net.minecraft.class_1959 swampBiome(net.minecraft.class_5483.class_5496 spawns, net.minecraft.class_5485.class_5495 generation,
                                                      int water, int waterFog, int foliage) {
        net.minecraft.class_5195 music = net.minecraft.class_1143.method_27283(net.minecraft.class_3417.field_38925);
        return new net.minecraft.class_1959.class_1960()
            .method_48164(true)
            .method_8747(0.8F)
            .method_8727(0.9F)
            .method_24379(new net.minecraft.class_4763.class_4764()
                .method_24395(water)
                .method_24397(waterFog)
                .method_24392(12638463)
                .method_30820(CbDataOverworldBiomesInvoker.cb$calculateSkyColor(0.8F))
                .method_30821(foliage)
                .method_30818(net.minecraft.class_4763.class_5486.field_26428)
                .method_24943(net.minecraft.class_4968.field_23146)
                .method_27346(music)
                .method_24391())
            .method_30974(spawns.method_31007())
            .method_30973(generation.method_46671())
            .method_30972();
    }

    // ------------------------------------------------------------------------------------------------ structures

    /**
     * 1.20.6 Structures.structure(biomes, spawnOverrides, step, terrainAdaptation): 1.21 has that constructor
     * of StructureSettings itself (Structures.structure was the factory 1.20.6 used for it).
     */
    public static net.minecraft.class_3195.class_7302 method_42759(net.minecraft.class_6885<net.minecraft.class_1959> biomes,
                                                                  java.util.Map<net.minecraft.class_1311, net.minecraft.class_7061> spawnOverrides,
                                                                  net.minecraft.class_2893.class_2895 step, net.minecraft.class_5847 terrainAdaptation) {
        return new net.minecraft.class_3195.class_7302(biomes, spawnOverrides, step, terrainAdaptation);
    }

    /**
     * new JigsawStructure(settings, startPool, startJigsawName, maxDepth, startHeight, useExpansionHack,
     * projectStartToHeightmap, maxDistanceFromCenter, poolAliases) of 1.20.6: 1.21's also takes the dimension
     * padding and the liquid settings, which 1.20.6 did not have. 1.21's own defaults for both
     * (JigsawStructure.DEFAULT_DIMENSION_PADDING: none; DEFAULT_LIQUID_SETTINGS: apply waterlogging) are what
     * 1.20.6 did -- it neither padded nor kept structure blocks dry.
     */
    public static net.minecraft.class_9778 defaultPadding() {
        return net.minecraft.class_5434.field_51911;
    }

    public static net.minecraft.class_9822 defaultLiquidSettings() {
        return net.minecraft.class_5434.field_52235;
    }

    /** 1.20.6 JigsawStructure.getPoolAliases(): the structure's pool aliases (1.21 keeps the field, drops the getter) */
    public static java.util.List<net.minecraft.class_8889> method_54521(net.minecraft.class_5434 structure) {
        return ((top.dext.centurybridge.step.v1_21.mixin.CbDataJigsawAccessor) (Object) structure).cb$poolAliases();
    }

    // ------------------------------------------------------------------------------------------------ enchantment tags

    /**
     * 1.20.6 EnchantmentTagsProvider(output, registries, enabledFeatures): 1.21's takes no feature flags. 1.20.6
     * used them only in tooltipOrder's check that every enabled enchantment was listed; the check reads the
     * flags from the provider ({@link #flags}), so they are kept for it.
     */
    private static final java.util.Map<Object, net.minecraft.class_7699> FLAGS =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    public static void keepFlags(net.minecraft.class_9674 provider, net.minecraft.class_7699 enabledFeatures) {
        FLAGS.put(provider, enabledFeatures);
    }

    /** a 1.20.6 EnchantmentTagsProvider subclass's own tooltipOrder(registries, enchantments...) */
    public interface LegacyTooltipOrder {
        void method_59781(net.minecraft.class_7225.class_7874 registries, top.dext.centurybridge.step.v1_21.facade.class_1887... enchantments);
    }

    /** provider.tooltipOrder(registries, enchantments...): a 1.20.6 subclass's own override, else 1.20.6's body */
    public static void method_59781(net.minecraft.class_9674 provider, net.minecraft.class_7225.class_7874 registries,
                                    top.dext.centurybridge.step.v1_21.facade.class_1887... enchantments) {
        if (provider instanceof LegacyTooltipOrder legacy) {
            legacy.method_59781(registries, enchantments);
        } else {
            superTooltipOrder(provider, registries, enchantments);
        }
    }

    /**
     * 1.20.6 EnchantmentTagsProvider.tooltipOrder(registries, enchantments...): the tooltip_order tag in that
     * order, and an IllegalStateException naming every enchantment of the registries enabled by the provider's
     * features that the order leaves out; then 1.21's tooltipOrder(registries, keys...), which adds the same tag
     * (and checks every enchantment of the registries -- 1.21's have no feature flags, so its check and 1.20.6's
     * differ only for a disabled 1.20.6 mod enchantment, which a data generation lookup never holds: its record
     * is made when a world loads its enchantment registry, CbLegacyRegistries.inject).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void superTooltipOrder(net.minecraft.class_9674 provider, net.minecraft.class_7225.class_7874 registries,
                                         top.dext.centurybridge.step.v1_21.facade.class_1887... enchantments) {
        java.util.List<net.minecraft.class_5321<net.minecraft.class_1887>> keys = new java.util.ArrayList<>();
        for (top.dext.centurybridge.step.v1_21.facade.class_1887 e : enchantments) {
            keys.add(CbEnchantments.wKeyOf(e));
        }
        net.minecraft.class_7699 enabled = FLAGS.get(provider);
        if (enabled != null) {
            java.util.Set<net.minecraft.class_5321<net.minecraft.class_1887>> listed = new java.util.HashSet<>(keys);
            java.util.List<String> missing = new java.util.ArrayList<>();
            registries.method_46762(net.minecraft.class_7924.field_41265).method_42017().forEach(h -> {
                top.dext.centurybridge.step.v1_21.facade.class_1887 legacy = CbEnchantments.legacyOf(h.comp_349());
                if ((legacy == null || legacy.method_45382(enabled)) && !listed.contains(h.method_40237())) {
                    missing.add(h.method_40237().method_29177().toString());
                }
            });
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Not all enchantments were registered for tooltip ordering. Missing: " + String.join(", ", missing));
            }
        }
        ((CbDataEnchantmentTagsInvoker) provider).cb$tooltipOrder(registries, keys.toArray(new net.minecraft.class_5321[0]));
    }
}
