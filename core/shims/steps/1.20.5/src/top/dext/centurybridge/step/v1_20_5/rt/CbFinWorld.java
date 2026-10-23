package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.List;
import java.util.Map;

/**
 * 1.20.4 world members (area s4fin world/level, world/inventory, core/dispenser and the rest) on 1.20.5:
 * <ul>
 * <li>Formulas 1.20.5 changed or folded elsewhere, kept as 1.20.4 computed them: a container's reach check, the
 *     armor damage reduction, where a dispenser places an entity poking out of it.</li>
 * <li>Registries 1.20.5 hands around as maps (WorldDimensions' level stems): a frozen registry built from the map
 *     where 1.20.4 code expects one, the map of the registry's entries where 1.20.5 wants one.</li>
 * <li>Loot builders whose arguments 1.20.5 generalised: lore replacement as a list operation, a name's target,
 *     a container's contents manipulator, banner patterns by holder.</li>
 * <li>SavedData.Factory's loader takes the registries in 1.20.5; 1.20.4's ignores them, and 1.20.4 code that
 *     asks for the loader gets one that loads with the running world's.</li>
 * </ul>
 */
public final class CbFinWorld {

    // ---------------------------------------------------------------- formulas

    /** Container.stillValidBlockEntity(be, player, distance)'s 1.20.4 body: the same block entity, within distance blocks of its center */
    public static boolean stillValid(net.minecraft.class_2586 be, net.minecraft.class_1657 player, int distance) {
        net.minecraft.class_1937 level = be.method_10997();
        net.minecraft.class_2338 pos = be.method_11016();
        if (level == null || level.method_8321(pos) != be) {
            return false;
        }
        return player.method_5649(pos.method_10263() + 0.5, pos.method_10264() + 0.5, pos.method_10260() + 0.5) <= (double) (distance * distance);
    }

    /** CombatRules.getDamageAfterAbsorb(damage, armor, toughness)'s 1.20.4 body */
    public static float damageAfterAbsorb(float damage, float armor, float toughness) {
        float f = 2.0F + toughness / 4.0F;
        float g = net.minecraft.class_3532.method_15363(armor - damage / f, armor * 0.2F, 20.0F);
        return damage * (1.0F - g / 25.0F);
    }

    /** DispenseItemBehavior.getEntityPokingOutOfBlockPos(source, type, direction)'s 1.20.4 body */
    public static net.minecraft.class_243 pokingOut(net.minecraft.class_2342 source, net.minecraft.class_1299<?> type, net.minecraft.class_2350 direction) {
        return source.method_53906().method_1031(
            direction.method_10148() * (0.5000099999997474 - type.method_17685() / 2.0),
            direction.method_10164() * (0.5000099999997474 - type.method_17686() / 2.0) - type.method_17686() / 2.0,
            direction.method_10165() * (0.5000099999997474 - type.method_17685() / 2.0));
    }

    /** DispenserBlock.getDispensePosition(source) of 1.20.4: 0.7 blocks out of the dispenser's face */
    public static net.minecraft.class_2374 dispensePosition(net.minecraft.class_2342 source) {
        return net.minecraft.class_2315.method_10010(source, 0.7, net.minecraft.class_243.field_1353);
    }

    // ---------------------------------------------------------------- BundleTooltip

    /** BundleTooltip(items, weight): 1.20.5's bundle contents of the items (whose weight is theirs) */
    public static net.minecraft.class_9276 bundle(net.minecraft.class_2371<net.minecraft.class_1799> items, int weight) {
        return new net.minecraft.class_9276(List.copyOf(items));
    }

    /** BundleTooltip.getItems(): the contents as 1.20.4's list */
    public static net.minecraft.class_2371<net.minecraft.class_1799> bundleItems(net.minecraft.class_9276 contents) {
        net.minecraft.class_2371<net.minecraft.class_1799> list = net.minecraft.class_2371.method_10211();
        contents.method_57421().forEach(list::add);
        return list;
    }

    /** BundleTooltip.getWeight(): 1.20.4's weight in sixty-fourths of a bundle */
    public static int bundleWeight(net.minecraft.class_9276 contents) {
        return contents.method_57428().multiplyBy(org.apache.commons.lang3.math.Fraction.getFraction(64, 1)).intValue();
    }

    // ---------------------------------------------------------------- level stems

    /** a 1.20.4 level stem registry as 1.20.5's map */
    public static Map<net.minecraft.class_5321<net.minecraft.class_5363>, net.minecraft.class_5363> stems(net.minecraft.class_2378<net.minecraft.class_5363> registry) {
        Map<net.minecraft.class_5321<net.minecraft.class_5363>, net.minecraft.class_5363> map = new java.util.LinkedHashMap<>();
        for (Map.Entry<net.minecraft.class_5321<net.minecraft.class_5363>, net.minecraft.class_5363> e : registry.method_29722()) {
            map.put(e.getKey(), e.getValue());
        }
        return map;
    }

    /** 1.20.5's level stem map as 1.20.4's (frozen) registry, in the map's order */
    public static net.minecraft.class_2378<net.minecraft.class_5363> stemRegistry(Map<net.minecraft.class_5321<net.minecraft.class_5363>, net.minecraft.class_5363> map) {
        net.minecraft.class_2370<net.minecraft.class_5363> registry =
            new net.minecraft.class_2370<>(net.minecraft.class_7924.field_41224, com.mojang.serialization.Lifecycle.experimental());
        map.forEach((key, stem) -> registry.method_10272(key, stem, net.minecraft.class_9248.field_49136));
        return registry.method_40276();
    }

    /** WorldDimensions.withOverworld(dimensionTypes, stems, generator) */
    public static net.minecraft.class_2378<net.minecraft.class_5363> withOverworld(net.minecraft.class_2378<net.minecraft.class_2874> types,
            net.minecraft.class_2378<net.minecraft.class_5363> stems, net.minecraft.class_2794 generator) {
        return stemRegistry(net.minecraft.class_7723.method_45520(types, stems(stems), generator));
    }

    /** WorldDimensions.withOverworld(stems, dimensionType, generator) */
    public static net.minecraft.class_2378<net.minecraft.class_5363> withOverworld(net.minecraft.class_2378<net.minecraft.class_5363> stems,
            net.minecraft.class_6880<net.minecraft.class_2874> type, net.minecraft.class_2794 generator) {
        return stemRegistry(net.minecraft.class_7723.method_45519(stems(stems), type, generator));
    }

    /** WorldDimensions.dimensions(): 1.20.4's registry */
    public static net.minecraft.class_2378<net.minecraft.class_5363> dimensions(net.minecraft.class_7723 dims) {
        return stemRegistry(dims.comp_1014());
    }

    /** WorldPresets.fromSettings(stems): 1.20.5 takes the dimensions */
    public static net.minecraft.class_7723 worldDimensions(net.minecraft.class_2378<net.minecraft.class_5363> stems) {
        return new net.minecraft.class_7723(stems);
    }

    // ---------------------------------------------------------------- loot

    /** SetLoreFunction(.., replace, ..): replace is 1.20.5's replace-all, else append */
    public static net.minecraft.class_9368 loreMode(boolean replace) {
        return replace ? net.minecraft.class_9368.class_9371.field_49852 : net.minecraft.class_9368.class_9369.field_49847;
    }

    /** a list operation as 1.20.4's replace flag (a 1.20.4 builder override's argument) */
    public static boolean loreReplace(net.minecraft.class_9368 mode) {
        return mode instanceof net.minecraft.class_9368.class_9371;
    }

    /** SetNameFunction.setName(..): 1.20.4 set the custom name */
    public static net.minecraft.class_3670.class_9475 nameTarget() {
        return net.minecraft.class_3670.class_9475.field_50210;
    }

    /** SetContainerContents(type): 1.20.4 wrote the items a container block entity holds; 1.20.5's container component */
    public static net.minecraft.class_9666<?> containerContents(net.minecraft.class_2591<?> type) {
        return net.minecraft.class_9667.field_51416;
    }

    /** SetBannerPatternFunction.Builder.addPattern(key, color): the pattern's holder in the running world */
    public static net.minecraft.class_6880<net.minecraft.class_2582> bannerPattern(net.minecraft.class_5321<net.minecraft.class_2582> key) {
        return CbRegistries.current().method_46762(net.minecraft.class_7924.field_41252).method_46747(key);
    }

    /** SetBannerPatternFunction.Builder.addPattern(key, color): 1.20.5's by holder */
    public static net.minecraft.class_5592.class_6157 addPattern(net.minecraft.class_5592.class_6157 builder,
                                                                 net.minecraft.class_5321<net.minecraft.class_2582> key, net.minecraft.class_1767 color) {
        return builder.method_35533(bannerPattern(key), color);
    }

    /** SetLoreFunction.Builder.setReplace(replace): 1.20.5's setMode */
    public static net.minecraft.class_3671.class_6159 setReplace(net.minecraft.class_3671.class_6159 builder, boolean replace) {
        return builder.method_58460(loreMode(replace));
    }

    /** ExplorationMapFunction.DEFAULT_DECORATION */
    public static top.dext.centurybridge.step.v1_20_5.facade.class_20$class_21 field_1034() {
        return CbRestMaps.typeOf(net.minecraft.class_111.field_1034);
    }

    /** ShipwreckPieces.addPieces(..): 1.20.5's addRandomPiece, the piece it added dropped */
    public static void addShipwreck(net.minecraft.class_3485 templates, net.minecraft.class_2338 pos, net.minecraft.class_2470 rotation,
                                    net.minecraft.class_6130 pieces, net.minecraft.class_5819 random, boolean beached) {
        net.minecraft.class_3415.method_59864(templates, pos, rotation, pieces, random, beached);
    }

    // ---------------------------------------------------------------- SavedData.Factory

    /** SavedData.Factory(constructor, loader, fixType): 1.20.4's loader ignores the registries 1.20.5 passes */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.function.BiFunction savedLoader(java.util.function.Function loader) {
        return (tag, registries) -> loader.apply(tag);
    }

    /** Factory.deserializer(): 1.20.5's loader with the running world's registries */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.function.Function savedLoaderOf(java.util.function.BiFunction loader) {
        return tag -> loader.apply(tag, CbRegistries.current());
    }

    // ---------------------------------------------------------------- ContainerSingleItem

    /**
     * the adapter a 1.20.4 ContainerSingleItem naming its block entity gets: 1.20.5's BlockContainerSingleItem, whose
     * stillValid default is 1.20.4's ContainerSingleItem default
     */
    public interface BlockContainer extends net.minecraft.class_8181.class_9210 {
        @Override
        net.minecraft.class_2586 method_54080();
    }

    /** LootItemFunctions.SET_NBT: 1.20.5's set_custom_data, the same function on the custom data component */
    public static net.minecraft.class_5339 field_25217() {
        return net.minecraft.class_131.field_49441;
    }

    /** LootItemFunctions.COPY_NBT: 1.20.5's copy_custom_data (CopyNbtFunction's class, renamed) */
    public static net.minecraft.class_5339 field_25233() {
        return net.minecraft.class_131.field_49443;
    }

    /** container.getContainerBlockEntity(): 1.20.5's BlockContainerSingleItem's (a 1.20.4 implementor is one: adapter hook) */
    public static net.minecraft.class_2586 method_54080(net.minecraft.class_8181 container) {
        if (container instanceof net.minecraft.class_8181.class_9210 block) {
            return block.method_54080();
        }
        throw new AbstractMethodError(container.getClass().getName() + " has no container block entity");
    }

    private CbFinWorld() {
    }
}
