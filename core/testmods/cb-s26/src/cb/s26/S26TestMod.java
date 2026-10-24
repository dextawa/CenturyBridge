package cb.s26;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;

/**
 * A 26.1.2-era server mod (Mojang names, Fabric Loader API only) for s26's bridges of the 26.1.2 -> 26.2 step,
 * converted one step and run on a Fabric 26.2 server at initialisation (after the game's bootstrap). Every
 * constant 26.2 folded into a record or moved must be what 26.1.2 had under its registry and id; static members
 * 26.2 moved and members it renamed must answer as on 26.1.2. Prints "CB-S26 ok|FAIL ..." lines and
 * "CB-S26 DONE passed/checks".
 */
public class S26TestMod implements ModInitializer {
    private static final List<String> FAILS = new ArrayList<>();
    private static int checks;

    static void check(String what, boolean ok, Object detail) {
        checks++;
        if (!ok) {
            FAILS.add(what);
            System.out.println("CB-S26 FAIL " + what + " : " + detail);
        }
    }

    static void check(String what, String actual, String expected) {
        check(what, expected.equals(actual), actual + " (want " + expected + ")");
    }

    /** what a constant is, as core/tools/RegistryDump.java prints it: kind, registry, id */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static String describe(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof ResourceKey<?> k) {
            return "key\t" + k.registry() + "\t" + k.identifier();
        }
        if (v instanceof TagKey<?> t) {
            return "tag\t" + t.registry().identifier() + "\t" + t.location();
        }
        if (v instanceof Holder<?> h) {
            Optional<? extends ResourceKey<?>> k = (Optional) h.unwrapKey();
            return k.map(x -> "holder\t" + x.registry() + "\t" + x.identifier()).orElse("holder\tunbound");
        }
        Registry<Registry<?>> root = (Registry) BuiltInRegistries.REGISTRY;
        for (Registry<?> reg : root) {
            Object id = ((Registry) reg).getKey(v);
            if (id != null && ((Registry) reg).getValue((net.minecraft.resources.Identifier) id) == v) {
                return "value\t" + root.getKey(reg) + "\t" + id;
            }
        }
        return "unregistered " + v.getClass().getName();
    }

    @Override
    public void onInitialize() {
        RegConstChecks.run();

        // static members 26.2 moved to another class (gen_static_moves.py)
        check("moved.EntityType.PIG", describe(EntityType.PIG), "value\tminecraft:entity_type\tminecraft:pig");
        check("moved.EntityType.ITEM", describe(EntityType.ITEM), "value\tminecraft:entity_type\tminecraft:item");
        check("moved.BlockEntityType.CHEST", describe(BlockEntityType.CHEST), "value\tminecraft:block_entity_type\tminecraft:chest");
        check("moved.BlockEntityType.FURNACE", describe(BlockEntityType.FURNACE), "value\tminecraft:block_entity_type\tminecraft:furnace");
        check("moved.DripstoneUtils.isEmptyOrWater", DripstoneUtils.isEmptyOrWater(Blocks.AIR.defaultBlockState())
            && DripstoneUtils.isEmptyOrWater(Blocks.WATER.defaultBlockState())
            && !DripstoneUtils.isEmptyOrWater(Blocks.STONE.defaultBlockState()), "");
        check("moved.DripstoneUtils.isEmptyOrWaterOrLava", DripstoneUtils.isEmptyOrWaterOrLava(Blocks.LAVA.defaultBlockState())
            && !DripstoneUtils.isEmptyOrWaterOrLava(Blocks.DIRT.defaultBlockState()), "");
        check("moved.DripstoneUtils.isNeitherEmptyNorWater", DripstoneUtils.isNeitherEmptyNorWater(Blocks.STONE.defaultBlockState())
            && !DripstoneUtils.isNeitherEmptyNorWater(Blocks.AIR.defaultBlockState()), "");

        // members 26.2 renamed (pkgmoves-gen.json methodRenames)
        check("renamed.MobEffect.isInstantenous", MobEffects.INSTANT_HEALTH.value().isInstantenous()
            && !MobEffects.SPEED.value().isInstantenous(), "");

        // 26.1.2's copper records, which 26.2 replaced by WeatheringCopperCollection (facades + CbS26Copper)
        net.minecraft.world.level.block.WeatheringCopperBlocks bars = Blocks.COPPER_BARS;
        check("copper.Blocks.COPPER_BARS.exposed", describe(bars.exposed()), "value\tminecraft:block\tminecraft:exposed_copper_bars");
        check("copper.Blocks.COPPER_BARS.waxedOxidized", describe(bars.waxedOxidized()), "value\tminecraft:block\tminecraft:waxed_oxidized_copper_bars");
        check("copper.Blocks.COPPER_BARS.same", bars == Blocks.COPPER_BARS && bars.asList().size() == 8
            && bars.weatheringMapping().get(bars.unaffected()) == bars.exposed()
            && bars.waxedMapping().get(bars.weathered()) == bars.waxedWeathered(), bars);
        net.minecraft.world.item.WeatheringCopperItems lanterns = net.minecraft.world.item.Items.COPPER_LANTERN;
        check("copper.Items.COPPER_LANTERN.weathered", describe(lanterns.weathered()), "value\tminecraft:item\tminecraft:weathered_copper_lantern");
        List<Object> order = new ArrayList<>();
        lanterns.forEach(order::add);
        check("copper.Items.COPPER_LANTERN.forEach", order.size() == 8 && order.get(0) == lanterns.unaffected()
            && order.get(4) == lanterns.waxed(), order);
        net.minecraft.world.item.WeatheringCopperItems chains = net.minecraft.world.item.WeatheringCopperItems.create(
            Blocks.COPPER_CHAIN, net.minecraft.world.level.block.Block::asItem);
        check("copper.WeatheringCopperItems.create", chains.equals(net.minecraft.world.item.Items.COPPER_CHAIN), chains);

        // 26.1.2's Tuple, which 26.2 deleted (facade)
        net.minecraft.util.Tuple<String, Integer> t = new net.minecraft.util.Tuple<>("a", 1);
        t.setB(2);
        check("tuple", "a".equals(t.getA()) && t.getB() == 2, t);

        System.out.println("CB-S26 DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS: " + FAILS));
    }
}
